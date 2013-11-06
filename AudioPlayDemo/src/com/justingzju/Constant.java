package com.justingzju;

import android.net.Uri;

import com.justingzju.database.AudioProvider;

public class Constant {
	
	public static final Uri PROVIDER_AUDIO = Uri.parse("content://"+AudioProvider.class.getName());

}
