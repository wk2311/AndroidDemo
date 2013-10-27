package com.justingzju.audioplay;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class ConnectionWrapper implements ServiceConnection {
	
	private ServiceConnection serviceConnection;
	public ConnectionWrapper(ServiceConnection connection) {
		this.serviceConnection = connection;
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		AudioClient.setService(IAudioService.Stub.asInterface(service));
		serviceConnection.onServiceConnected(name, service);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		serviceConnection.onServiceDisconnected(name);
		AudioClient.setService(null);
	}
	
}