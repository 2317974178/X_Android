package com.qdreamer.qvoice;


import android.os.Handler;

import java.util.concurrent.Semaphore;

import dalvik.system.PathClassLoader;

public class OggHelper {

	private long handler = 0;
	private boolean isInit = false;
	private final int START_STATE = 0;
	private final int STOP_STATE = 1;
	private int STATE = 2;
	private Thread thread = null;
	private boolean run = false;
	private Semaphore sem = new Semaphore(1);
	private int ret;

	private native long init(String params);

	private native int start(long handler);

	private native byte[] read(long hander);

	private native int feed(long handler, byte data[], int len, int isEnd);

	private native int stop(long handler);

	private native int delete(long handler);



	/**
	 * 创建引擎实例
	 * 
	 * @param cfgPath
	 *            资源路径
	 * @return true：创建成功 false：创建失败
	 */
	public boolean createEngine(String cfgPath, final Handler ui_handler) {
		boolean b;
		if (handler != 0) {
			return true;
		}
		b = sem.tryAcquire();// 只有一个信号量的时候可以调用
		if (!b) {
			sem.release();
			return false;
		}
		handler = init(cfgPath);
		if (handler == 0) {
			sem.release();
			return false;
		}
		run = true;
		thread = new Thread() {
			public void run() {
				while (run) {
					byte[] buffer = read(handler);
					if (buffer == null) {
						continue;
					}
					ui_handler.obtainMessage(0x01, buffer).sendToTarget();

				}
			}
		};
		thread.start();
		sem.release();
		isInit = true;
		return true;
	}

	/**
	 * 启动引擎
	 * 
	 */
	public boolean startEngine() {
		if (isInit && (STATE == STOP_STATE || STATE == 2)) {
			ret = start(handler);
			if (ret == 0) {
				STATE=START_STATE;
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * 引擎处理数据
	 * 
	 */
	public boolean feedData(byte[] data, int len, int isEnd) {
		if (isInit) {
			ret = feed(handler, data, len, isEnd);
			if (ret == 0) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * 停止引擎
	 * 
	 */
	public boolean stopEngine() {
		if (isInit && STATE == START_STATE) {
			feed(handler, null, 0, 1);
			ret = stop(handler);
			if (ret == 0) {
				STATE=STOP_STATE;
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * 删除引擎
	 */
	public void deleteEngine() {
		if (isInit) {
			boolean b = sem.tryAcquire();
			if (!b) {
				return;
			}
			run = false;
			delete(handler);
			try {
				sem.release();
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				sem.release();
				return;
			}
			thread = null;
			handler = 0;
			isInit = false;
			sem.release();
		}
	}
}
