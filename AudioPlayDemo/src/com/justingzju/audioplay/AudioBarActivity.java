package com.justingzju.audioplay;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.andrew.apolloMod.ui.fragments.BottomActionBarFragment;
import com.justingzju.LogUtil;
import com.justingzju.database.DBHelper;

public class AudioBarActivity extends Activity {
	
	private final static LogUtil mLog = new LogUtil(AudioBarActivity.class.getSimpleName(), true);

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mLog.w("onPause");
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mLog.w("onResume");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mLog.w("onCreate");
		setContentView(R.layout.activity_audiobar);
		mLog.w("onCreate end");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mLog.w("onDestroy");
		super.onDestroy();
	}

	private ServiceConnection connection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mLog.i("BottomActionBarFragment onServiceDisconnected");
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mLog.i("BottomActionBarFragment onServiceConnected");
		}
	};

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mLog.w("onStart");
		AudioClient.startAndBindService(this, connection);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		AudioClient.unbindService(this);
		mLog.w("onStop");
		super.onStop();
	}

}
