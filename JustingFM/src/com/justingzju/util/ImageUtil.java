package com.justingzju.util;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.widget.ImageView;

public class ImageUtil {

	public static void displayImage(String uri, ImageView imageView) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory().cacheOnDisc().build();
		ImageLoader.getInstance().displayImage(uri, imageView, options);
	}

}
