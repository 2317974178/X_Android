package com.qdreamer.qvoice;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;

public class QModule {

	private static long handler = 0;
	private static Thread thread = null;
	private static boolean run = false;
	private static boolean isInit = false;
	private static boolean isStart = false;
	private static AudioTrack player = null; // 播放器
	private static AudioRecord recorder = null; // 录音机
	private static boolean isStartCut = false;// 是否开启截取音频
	private static boolean isStopTimer = false;// 是否关闭定时器

	private native static long moduleNew(long session, String params, String resPath);

	private native static void moduleDel(long m);

	private native static int moduleStart(long m);

	private native static int moduleSet(long m, String params);

	private native static int moduleStop(long m);

	private native static byte[] moduleRead(long handler);

	private native static int moduleFeed(long m, byte[] data, int len);

	/**
	 * 初始化module引擎
	 * 
	 * @param session
	 * @param params
	 * @param resPath
	 * @return
	 */
	public static boolean init(long session, String params, String resPath) {
		if (session == 0) {
			return false;
		}
		handler = moduleNew(session, params, resPath);
		if (handler == 0) {
			return false;
		} else {
			isInit = true;
			return true;
		}
	}

	/**
	 * 启动module引擎
	 */
	public static boolean start(final Handler ui_handler) {
		if (isInit) {
			run = true;
			thread = new Thread() {
				public void run() {
					while (run) {
						if (handler != 0) {

							byte[] buffer = moduleRead(handler);
							if (buffer == null) {
								continue;
							}
							int type = buffer[0];
							int conetntLen = buffer.length - 1;

							byte[] content = new byte[conetntLen];
							for (int i = 0; i < conetntLen; ++i) {
								content[i] = buffer[i + 1];
							}
							ui_handler.obtainMessage(type, content).sendToTarget();
						}
					}
				}
			};
			thread.start();
			int ret = moduleStart(handler);
			if (ret == 0) {
				isStart = true;
				return true;
			} else {
				run = false;
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * feed eval txt
	 */
	public static boolean feed(String txt) {
		if (isInit) {
			String mEvalText = "evaltxt=\"" + txt + "\";";
			int ret = moduleSet(handler, mEvalText);
			return ret == 0 ? true : false;
		}
		return false;
	}

	public static boolean feedData(byte[] data) {
		if (isInit) {
			int ret = moduleFeed(handler, data, data.length);
			return ret == 0 ? true : false;
		}
		return false;
	}

	/**
	 * 停止module引擎
	 */
	public static boolean stop() {
		if (isStart) {
			run = false;
			int ret = moduleStop(handler);
			if (ret == -1) {
				return false;
			} else {
				isStart = false;
				thread = null;
				return true;
			}
		} else {
			return false;
		}
	}

	/**
	 * 释放module和所有加载的资源
	 */
	public static void delete() {
		if (handler != 0) {
			run = false;
			stopRecord();
			isStartCut = false;
			isStopTimer = false;
			moduleDel(handler);
			handler = 0;
			thread = null;
		}
	}

	/**
	 * 设置唤醒词，init之后，start之前设置
	 */
	public static boolean setWakeWord(String wakeWord) {
		if (handler != 0) {
			String str = "wakewrd=" + "\"" + wakeWord + "\";" + "";
			if (wakeWord.length() == 4) {
				int ret = moduleSet(handler, str);
				if (ret == 0) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * 开始录音
	 */
	public static boolean startRecord() {
		if (handler != 0 && isStart) {
			String str = "stop2=0;";
			if (!isStartCut) {
				int ret = moduleSet(handler, str);
				if (ret == 0) {
					isStartCut = true;
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * 停止录音
	 */
	public static boolean stopRecord() {
		if (handler != 0) {
			String str = "stop2=1;";
			if (isStartCut) {
				int ret = moduleSet(handler, str);
				if (ret == 0) {
					isStartCut = false;
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * 设置录音音量
	 */
	public static boolean setRecordVolume(double volume) {
		if (handler != 0 && isStart) {
			String str = "volume_shift=" + volume + ";";
			int ret = moduleSet(handler, str);
			return ret == 0;
		}
		return false;
	}

	/**
	 * 设置录音音频保存路径
	 */
	public static boolean setRecordPath(String filePath) {
		if (handler != 0 && isStart) {
			String str = "wav_path=" + filePath + ";";
			int ret = moduleSet(handler, str);
			return ret == 0;
		}
		return false;
	}

	/**
	 * 设置麦克风坐标
	 */
	public static boolean setMic(String mic) {
		if (handler != 0 && isInit) {
			int ret = moduleSet(handler, mic);
			return ret == 0;
		}
		return false;
	}

	/**
	 * 设置麦克风坐标
	 */
	public static boolean setBeam(double beam) {
		if (handler != 0) {
			String str = "beam=" + beam + ";";
			int ret = moduleSet(handler, str);
			return ret == 0;
		}
		return false;
	}

	/**
	 * 设置唤醒词的置信度
	 */
	public static boolean setWakeWordConf(double conf) {
		if (handler != 0) {
			String str = "wakeconf=" + conf + ";";
			int ret = moduleSet(handler, str);
			return ret == 0;
		}
		return false;
	}

	/**
	 * 开启定时器
	 */
	public static boolean startTimer() {
		if (handler != 0 && isStopTimer) {
			String str = "stop_timer=0;";
			int ret = moduleSet(handler, str);
			if (ret == 0) {
				isStopTimer = false;
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * 设置麦克风坐标
	 */
	public static boolean stopTimer() {
		if (handler != 0 && !isStopTimer) {
			String str = "stop_timer=1;";
			int ret = moduleSet(handler, str);
			if (ret == 0) {
				isStopTimer = true;
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * 设置参数
	 */
	public static boolean setParams(String params) {
		if (handler != 0) {
			int ret = moduleSet(handler, params);
			return ret == 0;
		}
		return false;
	}

	// ##########################################
	// #
	// # jni交互录音相关接口(系统录音机)
	// #
	// ##########################################
	/**
	 * @Description: 开始录音
	 * @param rate
	 *            录音的采用率
	 * @param buf_time
	 *            录音的缓冲区时间长度(毫秒)
	 * @return
	 */
	private static int RecorderStart(int rate, int channel, int bytes_per_sample, int buf_time) {
		if (recorder == null) {
			int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
			int audioBufSize = AudioRecord.getMinBufferSize(rate, channel, audioEncoding);
			int size = rate * channel * bytes_per_sample * buf_time / 1000;
			audioBufSize = audioBufSize > size ? audioBufSize : size;
			recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, rate,
					channel == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO, audioEncoding,
					audioBufSize);
		}
		recorder.startRecording();
		return 0;
	}

	/**
	 * @Description 读取录音数据
	 * @param buff
	 * @return
	 */
	private static int RecorderRead(byte[] buff) {
		int len;

		if (recorder != null) {
			len = recorder.read(buff, 0, buff.length);
		} else {
			len = 0;
		}
		return len;
	}

	/**
	 * @Description 停止录音
	 * @return
	 */
	private static int RecorderStop() {
		if (recorder != null) {
			recorder.stop();
			recorder.release();
			recorder = null;
		}
		return 0;
	}

	// ##########################################
	// #
	// # jni交互播放相关接口(系统播放器)
	// #
	// ##########################################
	/**
	 * @Description 语音引擎调用PlayStart，准备播放音频
	 * @param rate
	 *            语音的采样率
	 * @param channel
	 *            语音的频道
	 * @param buf_time
	 *            播放语音的缓冲区时间长度(毫秒)
	 * @return
	 */

	public static int PlayerStart(int rate, int channel, int bytes_per_sample, int buf_time) {
		if (player == null) {
			int buf_size = rate * buf_time * bytes_per_sample * channel / 1000;
			player = new AudioTrack(AudioManager.STREAM_MUSIC, rate,
					channel == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
					AudioFormat.ENCODING_PCM_16BIT, buf_size, AudioTrack.MODE_STREAM);
		}
		player.play();
		return 0;
	}

	/**
	 * @Description 语音引擎调用PlayWrite，播放音频
	 * @param data
	 *            开始要播放的语音数据
	 * @param len
	 *            要播放的语音数据的有效长度
	 * @return
	 */
	private static int PlayerWrite(byte[] data, int len) {
		int ret;
		if (player != null) {
			ret = player.write(data, 0, len);
		} else {
			ret = 0;
		}
		player.flush();
		return ret;
	}

	/**
	 * @Description 停止播放
	 * @return
	 */
	private static int PlayerStop() {
		if (player != null) {
			player.flush();
			player.stop();
		}
		return 0;
	}
}