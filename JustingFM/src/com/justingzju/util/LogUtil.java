package com.justingzju.util;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.util.Log;

import com.justingzju.fm.BuildConfig;

public class LogUtil {
	
	public static final boolean DEBUGGABLE = BuildConfig.DEBUG;

	public static final int LOG_LEVEL_ERROR = 4;
	public static final int LOG_LEVEL_WARN = 3;
	public static final int LOG_LEVEL_INFO = 2;
	public static final int LOG_LEVEL_DEBUG = 1;
	public static final int LOG_LEVEL_VERBOSE = 0;

	private String mLogTag = LogUtil.class.getSimpleName();
	private int mLogLevel = 0;
	private Context mContext = null;
	private boolean isOpen = true;
	
	public LogUtil() {
		
	}
	
	public LogUtil(String logTag) {
		this.mLogTag = logTag;
	}
	
	public LogUtil(String logTag, boolean isOpen) {
		this.mLogTag = logTag;
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

	public void setLogLevel(int level) {
		mLogLevel = level;
	}

	public void v(String msg) {
		v(mLogTag,msg);
	}
	public void v(String tag,String msg) {
		if (DEBUGGABLE && isOpen && mLogLevel<=LOG_LEVEL_VERBOSE) {
			Log.v(tag, msg);
			
			if (mContext != null) {
				writeFile(mContext, tag + " v: " + msg, "LogUtil.log");
			}
		}
	}
	
	public void d(String msg) {
		d(mLogTag,msg);
	}
	public void d(String tag,String msg) {
		if (DEBUGGABLE && isOpen && mLogLevel<=LOG_LEVEL_DEBUG) {
			Log.d(tag, msg);
			
			if (mContext != null) {
				writeFile(mContext, tag + " d: " + msg, "LogUtil.log");
			}
		}
	}
	
	public void i(String info) {
		i(mLogTag, info);
	}
	public void i(String tag,String msg) {
		if (DEBUGGABLE && isOpen && mLogLevel<=LOG_LEVEL_INFO) {
			Log.i(tag, msg);
			
			if (mContext != null) {
				writeFile(mContext, tag + " i: " + msg, "LogUtil.log");
			}
		}
	}


	public void w(String msg) {
		w(mLogTag,msg);
	}
	public void w(String tag,String msg) {
		if (DEBUGGABLE && isOpen && mLogLevel<=LOG_LEVEL_WARN) {
			Log.w(tag, msg);
			
			if (mContext != null) {
				writeFile(mContext, tag + " w: " + msg, "LogUtil.log");
			}
		}
	}

	public void e(String msg) {
		e(mLogTag,msg);
	}
	public void e(String tag,String msg) {
		if (DEBUGGABLE && isOpen && mLogLevel<=LOG_LEVEL_ERROR) {
			Log.e(tag, msg);
			
			if (mContext != null) {
				writeFile(mContext, tag + " e: " + msg, "LogUtil.log");
			}
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
				e(mLogTag, e.toString());
			}
	
		} catch (FileNotFoundException e) {
			e(mLogTag, e.toString());
		}
		return res;
	}
}
