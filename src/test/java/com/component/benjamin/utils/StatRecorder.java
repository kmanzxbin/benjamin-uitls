package com.component.benjamin.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class StatRecorder {

	File concurrentStatFile = new File("./target/statOutput/concurrentStat.txt");

	// 分钟时间
	String minuteStr;
	// 秒时间
	String secondStr;

	List<String> types = new ArrayList<String>();

	// 每秒 类型 个数
	Map<String, Map<String, Integer>> perSecondMap = new LinkedHashMap<String, Map<String, Integer>>();

	public static Map<Long, Integer> toRangeMap(Map<Long, Integer> map, List<Long> range) {

		Map<Long, Integer> rangeMap = new LinkedHashMap<Long, Integer>();

		for (Entry<Long, Integer> entry : map.entrySet()) {
			if (entry.getKey() < 0) {
				throw new RuntimeException("illegal key " + entry.getKey());
			}

			boolean added = false;
			for (Long long1 : range) {
				if (entry.getKey() < long1) {
					Integer count = rangeMap.get(long1);
					if (count == null) {
						rangeMap.put(long1, entry.getValue());
					} else {
						rangeMap.put(long1, count + entry.getValue());
					}
					added = true;
					break;
				}
			}
			if (!added) {
				throw new RuntimeException("over range key " + entry.getKey());
			}
		}
		return rangeMap;
	}

	public void stat() {
		if (!concurrentStatFile.getParentFile().exists()) {
			concurrentStatFile.getParentFile().mkdirs();
		}

		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(concurrentStatFile, true);

			// fileWriter.append(" ").append(types.toString()).append("\n");

			for (Entry<String, Map<String, Integer>> entry : perSecondMap.entrySet()) {

				fileWriter.append(entry.getKey());
				for (String type : types) {

					Integer count = entry.getValue().get(type);
					fileWriter.append(" ").append(count == null ? "0" : count + "");

				}

				fileWriter.append("\n");

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fileWriter != null) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public void recorder(String type, String time) {
		if (!types.contains(type)) {
			throw new RuntimeException("illegal type " + type);
		}
		if (minuteStr == null || secondStr == null) {
			minuteStr = time.substring(0, 16);
			secondStr = time.substring(0, 19);
		}
		String newMinuteStr = time.substring(0, 16);
		String newSecondStr = time.substring(0, 19);
		// // 秒数不同 换秒
		// if (newSecondStr.compareTo(secondStr) > 0) {
		//
		// Map<String, Integer> typeCount = new HashMap<String, Integer>();
		// perSecondMap.put(secondStr, typeCount);
		// typeCount.put(type, 1);
		// secondStr = newSecondStr;
		// }

		// 分钟数不同 输出到文件
		if (newMinuteStr.compareTo(minuteStr) > 0) {
			stat();
			perSecondMap = new LinkedHashMap<String, Map<String, Integer>>();
			minuteStr = newMinuteStr;
		}

		Map<String, Integer> map = perSecondMap.get(newSecondStr);
		if (map == null) {
			map = new HashMap<String, Integer>();
			perSecondMap.put(newSecondStr, map);
		}

		Integer count = map.get(type);
		if (count == null) {
			map.put(type, 1);
		} else {
			map.put(type, count + 1);
		}

	}

}
