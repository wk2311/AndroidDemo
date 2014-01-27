package com.justingzju.fm.service;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.justingzju.fm.storage.Audio;
import com.justingzju.fm.storage.Feed;
import com.justingzju.fm.storage.PodProvider;
import com.justingzju.util.LogUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class LoadImageTask extends AsyncTask<Object, Void, String> {

	private static final LogUtil mLog = new LogUtil(
			LoadImageTask.class.getSimpleName(), true);

	private Context context;

	private ImageView imageView;

	public LoadImageTask(Context context, ImageView imageView) {
		this.context = context;
		this.imageView = imageView;
	}

	@Override
	protected String doInBackground(Object... params) {
		if (params[0] instanceof Audio) {
			long feedId = ((Audio) params[0]).getFeed();
			Cursor cursor = context.getContentResolver().query(
					ContentUris.withAppendedId(PodProvider.CONTENT_URI_FEEDS,
							feedId), null, null, null, null);
			if (cursor == null) {
				mLog.e(context.getClass().getSimpleName(), "feed not found");
				return null;
			} else if (cursor.getCount() <= 0) {
				mLog.e(context.getClass().getSimpleName(), "feed not found");
				cursor.close();
				return null;
			}
			cursor.moveToFirst();
			String imageLink = cursor.getString(cursor.getColumnIndex(Feed.IMAGE_LINK));
			cursor.close();
			return imageLink;
		} else {
			return ((Feed) params[0]).getImageLink();
		}
	}

	@Override
	protected void onPostExecute(String result) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory().cacheOnDisc().build();
		ImageLoader.getInstance().displayImage(result, imageView, options);
		super.onPostExecute(result);
	}

}
