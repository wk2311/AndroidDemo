package com.justingzju.fm.service;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class PlayUtil {

	public static IPlayService mService = null;
	
	private static ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = IPlayService.Stub.asInterface(service);
		}
	};

	public static boolean startAndBindService(Activity activity) {
		Intent service = new Intent(activity, PlayService.class);
		activity.startService(service);
		return activity.bindService(service, mConnection , Service.BIND_AUTO_CREATE);
	}
	
	public static void unbindService(Activity activity) {
		activity.unbindService(mConnection);
	}
}
