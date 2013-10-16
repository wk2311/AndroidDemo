package com.justingzju.audioplay;

import android.app.Activity;
import android.os.Bundle;

public abstract class InitializableActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData(savedInstanceState);
		initView();
		initListener();
	}
	
	protected abstract void initData(Bundle savedInstanceState);
		
	protected abstract void initView();
	
	protected abstract void initListener();

}
