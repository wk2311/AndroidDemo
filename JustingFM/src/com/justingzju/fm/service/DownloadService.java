package com.justingzju.fm.service;

import java.util.ArrayList;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;

import com.justingzju.util.LogUtil;

public class DownloadService extends Service {

	private static final LogUtil mLog = new LogUtil(
			DownloadService.class.getSimpleName(), true);

	public static final String ACTION_SUBMIT_DOWNLOADS = DownloadService.class
			.getName() + ".submit_downloads";

	public static final String EXTRA_DOWNLOAD_REQUEST = "download_request";

	private DownloadManager mDownloadManager;

	@Override
	public void onCreate() {
		super.onCreate();
		mLog.v("onCreate");
		mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		registerReceiver(mDownloadReceiver, mDownloadIntentFilter);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mDownloadReceiver);
		mLog.v("onDestroy");
		super.onDestroy();
	}

	private static final IntentFilter mDownloadIntentFilter = new IntentFilter(
			DownloadManager.ACTION_DOWNLOAD_COMPLETE);

	private final BroadcastReceiver mDownloadReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
				final long downloadId = intent.getLongExtra(
						DownloadManager.EXTRA_DOWNLOAD_ID, -1);
				mLog.v("download id " + downloadId + " complete");
				onDownloadCheck();
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
			final ArrayList<DownloadRequest> requests = intent
					.getParcelableArrayListExtra(EXTRA_DOWNLOAD_REQUEST);
			onDownloadSubmit(requests);
		}
		return START_NOT_STICKY;
	}

	private void onDownloadCheck() {
		Query query = new Query();
		query.setFilterByStatus(DownloadManager.STATUS_RUNNING
				| DownloadManager.STATUS_PENDING);
		Cursor cursor = mDownloadManager.query(query);
		if (cursor == null || cursor.getCount() <= 0) {
			stopSelf();
		}
		cursor.close();
	}

	private void onDownloadSubmit(ArrayList<DownloadRequest> requests) {
		for (DownloadRequest downloadRequest : requests) {
			String source = downloadRequest.getSource();
			String destination = downloadRequest.getDestination();
			if (TextUtils.isEmpty(source)
					|| TextUtils.isEmpty(destination)) {
				mLog.e("dowload url or name is empty!");
				return;
			}
			Request request = new Request(Uri.parse(source))
					.setDestinationInExternalFilesDir(this,
							Environment.DIRECTORY_DOWNLOADS, destination)
					.setNotificationVisibility(Request.VISIBILITY_HIDDEN)
					.setVisibleInDownloadsUi(false);
			mDownloadManager.enqueue(request);
		}
	}

}
