package com.component.benjamin.utils.testframe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestFrame {

	Logger log = LoggerFactory.getLogger(TestFrame.class);

	ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);

	ExecutorService executorService = Executors.newFixedThreadPool(100);

	// 统计线程

	// 任务创建器
	// 建立线程池
	// 将创建的任务丢进线程池
	int threadCount = 100;
	int totalCount = 10000000;

	public void test() {
		log.info("test start");
		long timecost = System.currentTimeMillis();

		AtomicInteger queuedCounter = new AtomicInteger();

		List<AtomicLong> list = new ArrayList<AtomicLong>();
		// 多少个线程 list当中就存放多少个自增对象

		AtomicLong number = new AtomicLong(1000000000l);
		for (int i = 0; i < threadCount; i++) {
			list.add(new AtomicLong(number.getAndAdd(1000000000l)));
		}

		long count = 0;
		for (int i = 0; i < threadCount; i++) {

			if (count++ >= totalCount) {
				break;
			}
			// executorService.execute(new TestThread(queuedCounter,
			// list.get(i), jp.getResource()));

			if (queuedCounter.get() >= 500000) {
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (i >= threadCount) {
				i = 0;
			}

		}

		// Jedis jedis = jp.getResource();

		int sleepCounter = 0;
		while (queuedCounter.get() > 0) {
			// try {
			// TimeUnit.MILLISECONDS.sleep(1);
			// sleepCounter++;
			// if (sleepCounter >= 1000) {
			// sleepCounter = 0;
			// log.info("timecost " + timecost + " " +
			// (Integer.valueOf(jedis.get(TEST_KEY)) * 1000) / timecost
			// + "/SECOND");
			// }
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}

		// Assert.assertEquals(totalCount * threadCount + "",
		// jedis.get(TEST_KEY));

		timecost = System.currentTimeMillis() - timecost;
		log.info("test end, cost " + timecost + " " + (totalCount * threadCount * 1000) / timecost + "/SECOND");

	}
}
