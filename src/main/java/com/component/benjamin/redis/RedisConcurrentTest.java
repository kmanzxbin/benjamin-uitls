package com.component.benjamin.redis;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.component.benjamin.utils.Arguments;
import com.component.utils.FileFinder;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConcurrentTest {

	private static final Logger log = LoggerFactory.getLogger(RedisConcurrentTest.class);
	private static final String EXECUTION_POOL_SIZE = "executionPoolSize";
	private static final String TOTAL_COUNT = "totalCount";
	private static final String URI = "uri";
	private static final String MAX_IDLE = "maxIdle";
	private static final String MAX_TOTAL = "maxTotal";
	private static final String MAX_WAIT_MILLIS = "maxWaitMillis";

	static {

		URL url = RedisConcurrentTest.class.getResource("log4j.xml");
		if (url != null) {
			DOMConfigurator.configure(url);// 加载.xml文件
		} else {

			Set<File> files = FileFinder.find(new File("./"), "log4j\\.xml");
			if (files == null || files.size() == 0) {
				files = FileFinder.find(new File("../"), "log4j\\.xml");
			}

			if (files == null || files.size() == 0) {
				throw new NullPointerException("can not found logj4.xml");
			}

			File file = null;
			for (File tmpFile : files) {
				file = tmpFile;
				break;
			}

			if (file == null) {
				throw new NullPointerException("can not found logj4.xml");
			}

			DOMConfigurator.configure(file.getAbsolutePath());
		}
	}

	Arguments arguments;
	JedisPool jedisPool;
	JedisPoolConfig JedisPoolConfig;

	// 执行线程池
	ExecutorService executorService;

	StatisticPrinter statisticPrinter;
	AtomicInteger queuedCounter;
	AtomicLong totalProcessedCount;

	public static void main(String[] args) {

		Arguments arguments = new Arguments(args);

		arguments.set(EXECUTION_POOL_SIZE, "100");
		arguments.set(TOTAL_COUNT, "1000000");
		arguments.set(URI, "redis://:admin@192.168.0.19:6379");
		arguments.set(MAX_IDLE, "10");
		arguments.set(MAX_TOTAL, "200");
		arguments.set(MAX_WAIT_MILLIS, "20000");

		arguments.setArgs(args);

		RedisConcurrentTest redisConcurrentTest = new RedisConcurrentTest();
		redisConcurrentTest.arguments = arguments;
		redisConcurrentTest.init();
		redisConcurrentTest.test();
		redisConcurrentTest.destory();
	}

	class TestRunner implements Runnable {

		AtomicLong number;

		public TestRunner(AtomicLong number) {
			super();
			queuedCounter.incrementAndGet();
			this.number = number;
		}

		public void run() {

			Jedis jedis = null;
			try {
				jedis = jedisPool.getResource();
				long longNumber = number.getAndIncrement();
				String key = "106585103_" + longNumber + "_" + longNumber;
				jedis.set(key, key);

				// 106585103_2000009999_2000009999

				secondProcessedCounter.incrementAndGet();
				// jedis.expire(key, seconds)
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}

			queuedCounter.decrementAndGet();
		}
	}

	// class RequestGenerator extends Thread {
	//
	// long testCountPerThread;
	// AtomicInteger requestCounter;
	// int totalCount;
	//
	// public void run() {
	//
	// for (int i = 0; i < totalCount; i++) {
	//
	// // 满50万时等待再插入
	// if (requestCounter.get() >= 500000) {
	// try {
	// TimeUnit.MILLISECONDS.sleep(100);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	// }

	AtomicLong secondProcessedCounter = new AtomicLong();

	/**
	 * 负责每秒打印一次状态
	 * 
	 * @author Benjamin
	 *
	 */
	class StatisticPrinter extends Thread {

		@Override
		public void run() {

			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long secondsCount = secondProcessedCounter.getAndSet(0);
				totalProcessedCount.addAndGet(secondsCount);
				log.info("" + secondsCount + "/SEC, total " + totalProcessedCount.get());

			}
		}

	}

	public void init() {

		log.info(arguments + "");

		queuedCounter = new AtomicInteger();
		totalProcessedCount = new AtomicLong();

		executorService = Executors.newFixedThreadPool(arguments.getInt(EXECUTION_POOL_SIZE));

		JedisPoolConfig = new JedisPoolConfig();
		JedisPoolConfig.setMaxIdle(arguments.getInt(MAX_IDLE));
		JedisPoolConfig.setMaxTotal(arguments.getInt(MAX_TOTAL));
		JedisPoolConfig.setMaxWaitMillis(arguments.getInt(MAX_WAIT_MILLIS));

		try {
			URI uri = new URI(arguments.getString(URI));
			log.info(uri + "");
			jedisPool = new JedisPool(JedisPoolConfig, uri);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void test() {
		log.info("test start");
		long timecost = System.currentTimeMillis();

		int threadCount = arguments.getInt(EXECUTION_POOL_SIZE);
		int totalCount = arguments.getInt(TOTAL_COUNT);

		List<AtomicLong> list = new ArrayList<AtomicLong>();
		// 多少个线程 list当中就存放多少个自增对象

		AtomicLong number = new AtomicLong(1000000000l);
		for (int i = 0; i < threadCount; i++) {
			list.add(new AtomicLong(number.getAndAdd(1000000000l)));
		}

		statisticPrinter = new StatisticPrinter();
		statisticPrinter.setDaemon(true);
		statisticPrinter.start();

		for (int i = 0; i < totalCount; i++) {

			executorService.execute(new TestRunner(list.get(i % threadCount)));

			if (queuedCounter.get() >= 500000) {
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		while (totalProcessedCount.get() < totalCount) {

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		timecost = System.currentTimeMillis() - timecost;
		log.info("test end, cost " + (timecost / 1000.0) + " seconds " + (totalCount * threadCount * 1000) / timecost
				+ "/SECOND");
	}

	public void destory() {
		if (executorService != null) {
			executorService.shutdown();
		}
		if (JedisPoolConfig != null) {
			jedisPool.close();
		}
	}

}
