package com.justingzju;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.util.Log;

public class LogUtil {

	public static final int LOG_LEVEL_ERROR = 4;
	public static final int LOG_LEVEL_WARN = 3;
	public static final int LOG_LEVEL_INFO = 2;
	public static final int LOG_LEVEL_DEBUG = 1;
	public static final int LOG_LEVEL_VERBOSE = 0;

	private String logTag = LogUtil.class.getSimpleName();
	private int logLevel = 0;
	private Context mContext = null;
	private boolean isOpen = true;
	
	public LogUtil() {
		
	}
	
	public LogUtil(String logTag) {
		this.logTag = logTag;
	}
	
	public LogUtil(String logTag, boolean isOpen) {
		this.logTag = logTag;
		this.isOpen = isOpen;
	}
	
	public void open() {
		isOpen = true;
	}
	
	public void close() {
		isOpen = false;
	}
	
	public void openFileLog(Context context) {
		mContext = context;
	}
	
	public void closeFileLog() {
		mContext = null;
	}

	/**
	 * Log��Ϣ����>=logLevel����־��Ϣ��ӡ����
	 * @param level
	 */
	public void setLogLevel(int level) {
		logLevel = level;
	}

	/** ��ϸ��Ϣ */
	public void v(String msg) {
		v(logTag,msg);
	}
	public void v(String tag,String msg) {
		if(!isOpen)
			return;
		if (logLevel >= LOG_LEVEL_VERBOSE)
			return;
		Log.v(tag, msg);
		
		if (mContext != null) {
			writeFile(mContext, tag + " v: " + msg, "LogUtil.log");
		}
	}
	
	/** ������־ */
	public void d(String msg) {
		d(logTag,msg);
	}
	public void d(String tag,String msg) {
		if(!isOpen)
			return;
		if (logLevel >= LOG_LEVEL_DEBUG)
			return;
		Log.d(tag, msg);
		
		if (mContext != null) {
			writeFile(mContext, tag + " d: " + msg, "LogUtil.log");
		}
	}
	
	/** ��Ϣ��־ */
	public void i(String info) {
		i(logTag, info);
	}
	public void i(String tag,String msg) {
		if(!isOpen)
			return;
		if (logLevel >= LOG_LEVEL_INFO)
			return;
		Log.i(tag, msg);
		
		if (mContext != null) {
			writeFile(mContext, tag + " i: " + msg, "LogUtil.log");
		}
	}


	/** ������־ */
	public void w(String msg) {
		w(logTag,msg);
	}
	public void w(String tag,String msg) {
		if(!isOpen)
			return;
		if (logLevel >= LOG_LEVEL_WARN)
			return;
		Log.w(tag, msg);
		
		if (mContext != null) {
			writeFile(mContext, tag + " w: " + msg, "LogUtil.log");
		}
	}

	/** ������־ */
	public void e(String msg) {
		e(logTag,msg);
	}
	public void e(String tag,String msg) {
		if(!isOpen)
			return;
		if (logLevel >= LOG_LEVEL_ERROR)
			return;
		Log.e(tag, msg);
		
		if (mContext != null) {
			writeFile(mContext, tag + " e: " + msg, "LogUtil.log");
		}
	}

	public boolean writeIntoFile(Context context, String log) {
		return writeFile(context, log, "LogUtilTemp.log");
	}
	
	private boolean writeFile(Context context, String log, String logName) {
		log = log + "\n";
		boolean res = false;
		try {
			// Properties properties = new Properties();
			FileOutputStream fOut = context.openFileOutput(logName,
					Context.MODE_APPEND);
			try {
				fOut.write(log.getBytes());
				res = true;
			} catch (IOException e) {
				e(logTag, e.toString());
			}
	
		} catch (FileNotFoundException e) {
			e(logTag, e.toString());
		}
		return res;
	}
}
