package com.justingzju;

import android.net.Uri;

import com.justingzju.audioplay.R;
import com.justingzju.audioplay.R.drawable;
import com.justingzju.database.AudioProvider;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class Constant {
	
	public static final Uri PROVIDER_AUDIO = Uri.parse("content://"+AudioProvider.class.getName());
	
	public static final DisplayImageOptions DISPLAY_IMAGE_OPTIONS = new DisplayImageOptions.Builder()
	.showStubImage(R.drawable.no_art_normal)
	.cacheInMemory()
	.cacheOnDisc()
	.displayer(new RoundedBitmapDisplayer(0xff424242, 30))
	.build();
	
}
