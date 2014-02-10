package com.justingzju.util;

import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ImageUtil {
	
	private static final DisplayImageOptions options = new DisplayImageOptions.Builder()
	.cacheInMemory(true).cacheOnDisc(true).build();

	public static void displayImage(String uri, ImageView imageView) {
		ImageLoader.getInstance().displayImage(uri, imageView, options);
	}

	public static void loadImage(String imageLink, ImageLoadingListener listener) {
		ImageLoader.getInstance().loadImage(imageLink, options, listener);
	}

}
