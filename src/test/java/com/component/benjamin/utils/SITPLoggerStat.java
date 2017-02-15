package com.component.benjamin.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 根据sitp的日志分析消息的处理时间
 * 
 * @author Benjamin
 *
 */
public class SITPLoggerStat {

	private static final Logger log = LoggerFactory.getLogger(SITPLoggerStat.class);

	// 遍历文件行
	// 根据关键字查找消息
	// 获取seqNumber和时间
	// 计算栈收到和处理线程收到的时间差
	// 计算处理线程收到和消息返回的时间差

	static List<String> timeStampKeys = new ArrayList<String>();

	static {
		timeStampKeys.add("receive message, class: SITPStackImpl");
		timeStampKeys.add("receive message, class: ListenerRunner");
		timeStampKeys.add("send message, class: SITPBase");
		timeStampKeys.add("send message, class: SITPStackImpl");
	}

	static class TimeStamp {

		/**
		 * 0 receive message, class: SITPStackImpl<br>
		 * 1 receive message, class: ListenerRunner<br>
		 * 2 send message, class: SITPBaseServerImpl<br>
		 * 3 send message, class: SITPStackImpl<br>
		 */
		List<Long> timeList = new ArrayList<Long>();

	}

	StatRecorder statRecorder = new StatRecorder();

	private static final String[] REQEUST_IN_MATCHER = new String[] { "receive message, class: SITPStackImpl",
			"MsgType:Request" };

	private static final String[] RESPONSE_OUT_MATCHER = new String[] { "send message, class: SITPStackImpl",
			"MsgType:Response" };

	static class StatBean {
		/**
		 * 保存每个阶段到下一阶段的耗时时间统计数据 key为耗时时间 value为个数
		 */
		List<LinkedHashMap<Long, Integer>> list = new ArrayList<LinkedHashMap<Long, Integer>>();

		public void order() {
			List<LinkedHashMap<Long, Integer>> orderdList = new ArrayList<LinkedHashMap<Long, Integer>>();
			for (LinkedHashMap<Long, Integer> map : list) {

				List<Long> keyList = new ArrayList<Long>();
				keyList.addAll(map.keySet());
				Collections.sort(keyList, new Comparator<Long>() {

					@Override
					public int compare(Long o1, Long o2) {
						if (o1.longValue() > o2.longValue()) {
							return 1;
						} else if (o1.longValue() < o2.longValue()) {
							return -1;
						} else {
							return 0;
						}
						// return (int) (o1.longValue() - o1.longValue());
					}
				});

				// System.out.println(keyList);
				LinkedHashMap<Long, Integer> orderdMap = new LinkedHashMap<Long, Integer>();
				for (Long long1 : keyList) {
					orderdMap.put(long1, map.get(long1));
				}
				orderdList.add(orderdMap);
			}
			list = orderdList;
		}

		@Override
		public String toString() {

			StringBuilder stringBuilder = new StringBuilder();
			int i = 0;
			for (Map<Long, Integer> map : list) {
				stringBuilder.append("========== stage[" + i++ + "] ===========\n");

				for (Entry<Long, Integer> entry : map.entrySet()) {
					stringBuilder.append(entry).append("\n");
				}
			}
			return stringBuilder.toString();
		}

		public String toRangeString() {

			StringBuilder stringBuilder = new StringBuilder();
			int i = 0;
			for (Map<Long, Integer> map : list) {
				stringBuilder.append("========== stage[" + i++ + "] ===========\n");

				Map<Long, Integer> rangeMap = StatRecorder.toRangeMap(map, rangeList);
				for (Entry<Long, Integer> entry : rangeMap.entrySet()) {
					stringBuilder.append(entry).append("\n");
				}
			}
			return stringBuilder.toString();
		}
	}

	private static List<Long> rangeList = new ArrayList<Long>();

	static {
		rangeList.add(10l);
		rangeList.add(20l);
		rangeList.add(50l);
		rangeList.add(100l);
		rangeList.add(200l);
		rangeList.add(500l);
		rangeList.add(1000l);
		rangeList.add(2000l);
		rangeList.add(5000l);
		rangeList.add(10000l);
		rangeList.add(20000l);
		rangeList.add(50000l);
	}

	Map<String, StatBean> statBeans = new HashMap<String, StatBean>();

	Map<String, TimeStamp> timeStamps = new HashMap<String, TimeStamp>();
	static List<String> matches = new ArrayList<String>();

	static {
		matches.add("[searchMessageTemp]");
		matches.add("[getGroupPkgs]");
		matches.add("[sendMessageByTemplate]");
		matches.add("[searchGroupById]");

	}

	/**
	 * seqNumber到类型的映射
	 */
	Map<String, String> seqTypeMapping = new HashMap<String, String>();

	public static void main(String[] args) {

		File[] files = new File("E:\\temp\\20161222_A04_接口慢定位").listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.getName().contains(".log");
			}
		});

		SITPLoggerStat sitpLoggerStat = new SITPLoggerStat();

		sitpLoggerStat.statRecorder.concurrentStatFile.delete();

		sitpLoggerStat.statRecorder.types.add("RequestIn");
		sitpLoggerStat.statRecorder.types.add("ResponseOut");

		List<File> fileList = new ArrayList<File>();
		for (File file : files) {
			fileList.add(file);
		}
		Collections.sort(fileList, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				return o2.getName().compareTo(o1.getName());
			}
		});
		for (File file : fileList) {
			sitpLoggerStat.process(file);
		}

		for (Entry<String, StatBean> entry : sitpLoggerStat.statBeans.entrySet()) {

			System.out.println("stat key: " + entry.getKey());

			entry.getValue().order();
			System.out.println(entry.getValue().toRangeString());
		}
	}

	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

	public void process(File file) {

		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {

				// 不对ProType:SITP的记录进行处理
				if (line.contains("ProType:SITP") || line.contains("MsgName:GetStatus ProType:NMA")
						|| line.contains("MsgName:SetNodeInfo ProType:NMA")
						|| line.contains("MsgName:Getsmc ProType:NMA")) {
					continue;
				}
				// 解析时间和类型
				// 2016-12-22 11:20:39,954
				String timeStr = line.substring(0, 23);
				long time = simpleDateFormat.parse(timeStr).getTime();

				// 进行并发请求量的统计 只要是开头的就进行统计
				boolean requestInMahched = true;
				for (String str : REQEUST_IN_MATCHER) {
					if (!line.contains(str)) {
						requestInMahched = false;
						break;
					}
				}
				if (requestInMahched) {
					statRecorder.recorder("RequestIn", timeStr);
				}

				boolean responseOutMahched = true;
				for (String str : RESPONSE_OUT_MATCHER) {
					if (!line.contains(str)) {
						responseOutMahched = false;
						break;
					}
				}
				if (responseOutMahched) {
					statRecorder.recorder("ResponseOut", timeStr);
				}

				String seqNumber = line.substring(line.indexOf("SeqNumber:") + "SeqNumber:".length());
				seqNumber = seqNumber.substring(0, seqNumber.indexOf(" "));

				String matcher = null;
				if (matches != null && matches.size() > 0) {

					// 是匹配的行
					for (String string : matches) {
						if (line.contains(string)) {
							matcher = string;
							break;
						}
					}

					if (matcher == null) {
						matcher = seqTypeMapping.get(seqNumber);
						if (matcher == null) {
							continue;
						}

					} else {
						seqTypeMapping.put(seqNumber, matcher);
					}
				} else {
					matcher = "";
				}

				// seqNumber样例 1e7de931-32dc-4da3-967e-6a809a66647f
				// if
				// (!seqNumber.matches("\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}")) {
				// throw new RuntimeException("illegal seqNumber: " +
				// seqNumber);
				// }

				// 根据步骤保存时间到相应的位置
				TimeStamp timeStamp = timeStamps.get(seqNumber);
				if (timeStamp == null) {
					timeStamp = new TimeStamp();
					timeStamps.put(seqNumber, timeStamp);
					for (int i = 0; i < timeStampKeys.size(); i++) {
						timeStamp.timeList.add(-1l);
					}

				}
				// 检索现在执行到第几步了
				int stage = -1;
				for (int i = 0; i < timeStampKeys.size(); i++) {
					if (line.contains(timeStampKeys.get(i))) {
						timeStamp.timeList.set(i, time);
						stage = i;
						break;
					}
				}

				if (stage == -1) {
					log.warn("can not find timeStampKey in: " + line);
					continue;
				}

				// 到最后一条了
				if (stage == timeStampKeys.size() - 1) {
					// 检查所有的步骤时间不能为空 否则记录异常日志
					boolean stageError = false;
					for (int i = 0; i < timeStamp.timeList.size(); i++) {
						if (timeStamp.timeList.get(i) == -1) {
							if (i == 0) {

								log.debug("seqNumber {} stage {} time is null!", new Object[] { seqNumber, i });
							} else {
								log.warn("seqNumber {} stage {} time is null!", new Object[] { seqNumber, i });

							}
							stageError = true;
							if (stageError) {
								break;
							}
						}
					}

					// 到最后一条了 将数据从map当中移除
					timeStamps.remove(seqNumber);
					seqTypeMapping.remove(seqNumber);
					// 如果通过检查 将记录的数据插入到
					if (!stageError) {
						// 计算各阶段差值 然后将数据插入到统计当中
						// 针对各个匹配的类型进行统计
						timecostStat(matcher, timeStamp, seqNumber);
					}
				}

			}

			// 做最后一分钟的统计
			statRecorder.stat();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void timecostStat(String key, TimeStamp timeStamp, String seqNumber) {
		StatBean statBean = statBeans.get(key);
		if (statBean == null) {
			statBean = new StatBean();
			statBeans.put(key, statBean);

			for (int i = 0; i < timeStamp.timeList.size() - 1; i++) {
				statBean.list.add(new LinkedHashMap<Long, Integer>());
			}

		}

		for (int i = 0; i < timeStamp.timeList.size() - 1; i++) {

			// 获取第i个步骤的耗时和个数统计数据
			Map<Long, Integer> map = statBean.list.get(i);

			long timeCost = 0;
			timeCost = timeStamp.timeList.get(i + 1) - timeStamp.timeList.get(i);
			if (timeCost < -100) {
				throw new RuntimeException("seqNumber: " + seqNumber + " stage " + i + " time cost is wrong");
			}
			if (timeCost < 0) {
				timeCost = 0;
			}

			// 将耗时按照时间和数量存放
			Integer count = map.get(timeCost);
			if (count == null) {
				map.put(timeCost, 1);
			} else {
				map.put(timeCost, count + 1);
			}
		}
	}

}
