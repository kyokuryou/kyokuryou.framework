package org.smarty.core.common;

import java.util.LinkedList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ThreadPool extends ThreadGroup {
	private static Log logger = LogFactory.getLog(ThreadPool.class);
	private boolean isClosed = false;  //线程池是否关闭
	private LinkedList<Runnable> workQueue;      //工作队列
	private static int threadPoolID = 1;  //线程池的id

	/**
	 * poolSize 表示线程池中的工作线程的数量
	 */
	public ThreadPool(int poolSize) {
		//指定ThreadGroup的名称
		super(threadPoolID + "");
		//继承到的方法，设置是否守护线程池
		setDaemon(true);
		//创建工作队列
		workQueue = new LinkedList<Runnable>();
		for (int i = 0; i < poolSize; i++) {
			//创建并启动工作线程,线程池数量是多少就创建多少个工作线程
			new WorkThread(i).start();
		}
	}

	/**
	 * 向工作队列中加入一个新任务,由工作线程去执行该任务
	 */
	public synchronized void execute(Runnable task) {
		if (isClosed) {
			throw new IllegalStateException();
		}
		if (task != null) {
			//向队列中加入一个任务
			workQueue.add(task);
			//唤醒一个正在getTask()方法中待任务的工作线程
			notify();
		}
	}

	/**
	 * 从工作队列中取出一个任务,工作线程会调用此方法
	 */
	private synchronized Runnable getTask() throws InterruptedException {
		while (workQueue.size() == 0) {
			if (isClosed)
				return null;
			//如果工作队列中没有任务,就等待任务
			wait();
		}
		//反回队列中第一个元素,并从队列中删除
		return workQueue.removeFirst();
	}

	/**
	 * 关闭线程池
	 */
	public synchronized void closePool() {
		if (!isClosed) {
			//等待工作线程执行完毕
			waitFinish();
			isClosed = true;
			//清空工作队列
			workQueue.clear();
			//中断线程池中的所有的工作线程,此方法继承自ThreadGroup类
			interrupt();
		}
	}

	/**
	 * 等待工作线程把所有任务执行完毕
	 */
	public void waitFinish() {
		synchronized (this) {
			isClosed = true;
			//唤醒所有还在getTask()方法中等待任务的工作线程
			notifyAll();
		}
		//activeCount() 返回该线程组中活动线程的估计值。
		Thread[] threads = new Thread[activeCount()];
		//enumerate()方法继承自ThreadGroup类，根据活动线程的估计值获得线程组中当前所有活动的工作线程
		int count = enumerate(threads);
		//等待所有工作线程结束
		for (int i = 0; i < count; i++) {
			try {
				//等待工作线程结束
				threads[i].join();
			} catch (InterruptedException ex) {
				logger.warn(ex);
			}
		}
	}

	/**
	 * 内部类,工作线程,负责从工作队列中取出任务,并执行
	 */
	private class WorkThread extends Thread {
		public WorkThread(int id) {
			//父类构造方法,将线程加入到当前ThreadPool线程组中
			super(ThreadPool.this, id + "");
		}

		public void run() {
			//isInterrupted()方法继承自Thread类，判断线程是否被中断
			while (!isInterrupted()) {
				Runnable task = null;
				try {
					//取出任务
					task = getTask();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				//如果getTask()返回null或者线程执行getTask()时被中断，则结束此线程
				if (task == null)
					return;

				try {
					//运行任务
					task.run();
				} catch (Throwable t) {
					logger.warn(t);
				}
			}
		}
	}
}