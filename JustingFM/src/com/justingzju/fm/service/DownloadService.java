package com.justingzju.fm.service;

import static com.justingzju.util.Constant.INVALID_ID;
import static com.justingzju.util.Constant.INVALID_TIME;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.SparseArray;

import com.justingzju.fm.storage.Audio;
import com.justingzju.fm.storage.Feed;
import com.justingzju.fm.storage.PodProvider;
import com.justingzju.util.LogUtil;

public class DownloadService extends Service {

	private static final LogUtil mLog = new LogUtil(
			DownloadService.class.getSimpleName(), true);

	public static final String ACTION_SUBMIT_DOWNLOADS = DownloadService.class
			.getName() + ".submit_downloads";
	public static final String ACTION_INIT_FEED = "init_feed";

	public static final String ACTION_UPDATE_FEED = DownloadService.class
			.getName() + ".update_feed";

	public static final String EXTRA_DOWNLOAD_REQUEST = "download_request";

	public static final String EXTRA_FEED_ID = "feed_id";

	public static final String EXTRA_FEED_LINK = "feed_link";

	// FIXME maxInitNum should be read from configuration.xml
	/**
	 * the number of audio to insert when mLastUpdateTime is INVALID_TIME
	 */
	private static final int MAX_INIT_NUM = 20;

	private DownloadManager mDownloadManager;

	private SparseArray<DownloadRequest> mRequestArray = new SparseArray<DownloadRequest>();

	@Override
	public void onCreate() {
		super.onCreate();
		mLog.v("onCreate");
		IntentFilter filter = new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		registerReceiver(mDownloadReceiver, filter);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mDownloadReceiver);
		mLog.v("onDestroy");
		super.onDestroy();
	}

	private final BroadcastReceiver mDownloadReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
				final long downloadId = intent.getLongExtra(
						DownloadManager.EXTRA_DOWNLOAD_ID, INVALID_ID);
				mLog.v("download id " + downloadId + " complete");
				onDownloadComplete((int) downloadId);
			}
		}

	};

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		final String action = intent.getAction();
		if (action.equals(ACTION_SUBMIT_DOWNLOADS)) {
//			final ArrayList<DownloadRequest> requests = intent
//					.getParcelableArrayListExtra(EXTRA_DOWNLOAD_REQUEST);
//			onDownloadSubmit(requests);
		} else if (action.equals(ACTION_UPDATE_FEED)) {
			final long feedId = intent.getLongExtra(EXTRA_FEED_ID, INVALID_ID);
			new UpdateFeedTask().execute(ContentUris.withAppendedId(
					PodProvider.CONTENT_URI_FEEDS, feedId));
		} else if (action.equals(ACTION_INIT_FEED)) {
			final String[] feedLinks = intent.getStringArrayExtra(EXTRA_FEED_LINK);
			new AddFeedTask().execute(feedLinks);
		}
		return START_NOT_STICKY;
	}
	
	private class AddFeedTask extends AsyncTask<String, Void, Void> implements
	DownloadRequest.onCompleteListener {

		@Override
		protected Void doInBackground(String... links) {
			for (String link : links) {
				DownloadRequest request = new DownloadRequest(link, Feed.getDestinationFromLink(link));
				request.setOnDownloadCompleteListener(this);
				int downlaodId = (int) onDownloadSubmit(request);
				if (downlaodId != INVALID_ID) {
					mRequestArray.put(downlaodId, request);
				}
			}
			return null;
		}

		@Override
		public void onDownloadComplete(DownloadRequest request) {
			try {
				FileInputStream inputStream = new FileInputStream(new File(getExternalFilesDir(null),
						request.getDestination()));
				FeedParser parser = new FeedParser(inputStream);
				ContentValues values = parser.parseFeedValues();
				values.put(Feed.LINK, request.getSource());
				getContentResolver().insert(PodProvider.CONTENT_URI_FEEDS, values);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		
	}

	private class UpdateFeedTask extends AsyncTask<Uri, Void, Void> implements
			DownloadRequest.onCompleteListener {

		private Feed feed;
		private long lastUpdateTime;
		private File feedFile;
		
		@Override
		protected Void doInBackground(Uri... params) {
			feed = queryFeed(params[0]);
			lastUpdateTime = queryLastUpdateTime(feed.getId());

			feedFile = new File(getExternalFilesDir(null),
					feed.getDestination());
			feedFile.delete();
			
			DownloadRequest request = new DownloadRequest(feed.getLink(), feed.getDestination());
			request.setOnDownloadCompleteListener(this);
			int downlaodId = (int) onDownloadSubmit(request);
			if (downlaodId != INVALID_ID) {
				mRequestArray.put(downlaodId, request);
			}

			return null;
		}

		private Feed queryFeed(Uri uri) {
			Cursor cursor = getContentResolver().query(uri, null, null, null,
					null);
			if (cursor == null) {
				return null;
			} else if (cursor.getCount() <= 0) {
				cursor.close();
				return null;
			}
			cursor.moveToFirst();
			Feed feed = new Feed(cursor);
			cursor.close();
			return feed;
		}

		private long queryLastUpdateTime(long feedId) {
			Cursor cursor = getContentResolver().query(
					PodProvider.CONTENT_URI_AUDIOS,
					new String[] { Audio.PUB_DATE }, Audio.FEED + "=?",
					new String[] { String.valueOf(feedId) },
					Audio.PUB_DATE + " DESC");
			if (cursor == null) {
				return INVALID_TIME;
			} else if (cursor.getCount() <= 0) {
				cursor.close();
				return INVALID_TIME;
			}
			cursor.moveToFirst();
			long time = cursor.getLong(cursor.getColumnIndex(Audio.PUB_DATE));
			cursor.close();
			return time;
		}

		@Override
		public void onDownloadComplete(DownloadRequest request) {
			try {
				FileInputStream inputStream = new FileInputStream(feedFile);
				FeedParser parser = new FeedParser(inputStream);
				ContentValues[] valuesArray = parser.parseAudioValues(feed.getId(),
						lastUpdateTime, MAX_INIT_NUM);
				if (valuesArray != null) {
					getContentResolver().bulkInsert(
							PodProvider.CONTENT_URI_AUDIOS, valuesArray);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}

	}

//	private void onDownloadSubmit(ArrayList<DownloadRequest> requests) {
//		for (DownloadRequest downloadRequest : requests) {
//			String source = downloadRequest.getSource();
//			String destination = downloadRequest.getDestination();
//			if (TextUtils.isEmpty(source) || TextUtils.isEmpty(destination)) {
//				mLog.e("dowload url or name is empty!");
//				return;
//			}
//			Request request = new Request(Uri.parse(source))
//					.setDestinationInExternalFilesDir(this,
//							Environment.DIRECTORY_PODCASTS, destination)
//					.setNotificationVisibility(Request.VISIBILITY_HIDDEN)
//					.setVisibleInDownloadsUi(false);
//			mDownloadManager.enqueue(request);
//		}
//	}

	private long onDownloadSubmit(DownloadRequest downloadRequest) {
		String source = downloadRequest.getSource();
		String destination = downloadRequest.getDestination();
		if (TextUtils.isEmpty(source) || TextUtils.isEmpty(destination)) {
			mLog.e("dowload url or name is empty!");
			return INVALID_ID;
		}
		Request request = new Request(Uri.parse(source))
				.setDestinationInExternalFilesDir(this,
						null, destination)
				.setNotificationVisibility(Request.VISIBILITY_HIDDEN)
				.setVisibleInDownloadsUi(false);
		return mDownloadManager.enqueue(request);
	}

	private void onDownloadComplete(int downloadId) {
		DownloadRequest request = mRequestArray.get(downloadId);
		if (request != null) {
			request.onDownloadComplete();
			mRequestArray.delete(downloadId);
		}
		
		if (mRequestArray.size() <= 0) {
			stopSelf();
		}
//		Query query = new Query();
//		query.setFilterByStatus(DownloadManager.STATUS_RUNNING
//				| DownloadManager.STATUS_PENDING);
//		Cursor cursor = mDownloadManager.query(query);
//		if (cursor == null || cursor.getCount() <= 0) {
//			stopSelf();
//		}
//		cursor.close();
	}

	// FIXME download task not found! cursor.getCount() is 0
	private String queryLocalPath(long downloadId) {
		Query query = new Query().setFilterById(downloadId);
		Cursor cursor = mDownloadManager.query(query);
		if (cursor == null) {
			mLog.e("download task not found! cursor is null");
			return null;
		} else if (cursor.getCount() != 1) {
			mLog.e("download task not found! cursor.getCount() is "
					+ cursor.getCount());
			cursor.close();
			return null;
		}
		cursor.moveToFirst();
		String localUri = cursor.getString(cursor
				.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
		cursor.close();
		// Query result of COLUMN_LOCAL_URI startWith "/file:/",
		// "/file://mnt/sdcard" for example
		return localUri.substring("/file:/".length());
	}

}
