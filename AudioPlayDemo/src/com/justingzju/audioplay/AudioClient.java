package com.justingzju.audioplay;

import java.util.HashMap;

import com.justingzju.service.AudioService;
import com.justingzju.service.IAudioService;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;

public class AudioClient {
	
	private static IAudioService mService;
	
	private static HashMap<Context, ConnectionWrapper> connectionMap = new HashMap<Context, ConnectionWrapper>();
	
	public static IAudioService getService() {
		return mService;
	}
	
	public static void setService(IAudioService service) {
		mService = service;
	}
	
	public static boolean startAndBindService(Activity activity, ServiceConnection connection) {
		Activity realActivity = activity.getParent();
        if (realActivity == null) {
            realActivity = activity;
        }
        realActivity.startService(new Intent(realActivity, AudioService.class));
        ConnectionWrapper connectionWrapper = new ConnectionWrapper(connection);
		if(realActivity.bindService(new Intent(realActivity, AudioService.class), connectionWrapper , Service.BIND_AUTO_CREATE)) {
			connectionMap.put(realActivity, connectionWrapper);
			return true;
		} else {
			return false;
		}
	}
	
	public static void unbindService(Activity activity) {
		Activity realActivity = activity.getParent();
        if (realActivity == null) {
            realActivity = activity;
        }
        ConnectionWrapper connectionWrapper = connectionMap.get(realActivity);
        if(connectionWrapper==null)
        	return;
        realActivity.unbindService(connectionWrapper);
	}

}
