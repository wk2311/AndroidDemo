package com.justingzju.audioplay;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.justingzju.database.DBHelper;

public class AudioBarActivity extends InitializableActivity {

	@Override
	protected void initData(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		System.out.println("AudioBarActivity onCreate initData");
		DBHelper.helper = new DBHelper(this);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_audiobar);
	}

	@Override
	protected void initListener() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		DBHelper.helper.close();
		super.onDestroy();
	}

	private ServiceConnection connection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			System.out.println("BottomActionBarFragment onServiceDisconnected");
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			System.out.println("BottomActionBarFragment onServiceConnected");
		}
	};

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		AudioClient.bindService(this, connection);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		AudioClient.unbindService(this);
		super.onStop();
	}

}
