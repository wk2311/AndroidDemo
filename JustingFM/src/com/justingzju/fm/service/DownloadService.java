package com.justingzju.fm.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Xml;

import com.justingzju.fm.storage.Audio;
import com.justingzju.fm.storage.AudioProvider;
import com.justingzju.util.LogUtil;

public class DownloadService extends Service {

	private static final LogUtil mLog = new LogUtil(
			DownloadService.class.getSimpleName(), true);

	public static final String ACTION_SUBMIT_DOWNLOADS = DownloadService.class
			.getName() + ".submit_downloads";
	public static final String ACTION_UPDATE_PODLIST = DownloadService.class
			.getName() + ".update_podlist";

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

	private static final int MAX_INIT_PODCAST_NUMBER = 20;

	private static final long INVALID_TIME = -1;

	private final BroadcastReceiver mDownloadReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
				final long downloadId = intent.getLongExtra(
						DownloadManager.EXTRA_DOWNLOAD_ID, -1);
				mLog.v("download id " + downloadId + " complete");
				onDownloadComplete(downloadId);
			}
		}

	};

	private long mUpdateTaskId = -1;

	private long mLastUpdateTime = -1;

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
		} else if (action.equals(ACTION_UPDATE_PODLIST)) {
			onUpdateSubmit();
		}
		return START_NOT_STICKY;
	}

	private void onUpdateSubmit() {
		String listSource = "http://www.justing.com.cn:8081/podcast/podxml/free/justing_free.xml";
		String listFileName = "justing_free.xml";
		File listFile = new File(getExternalFilesDir(null), listFileName);
		if (listFile.exists()) {
			listFile.delete();
		}
		
		if (mLastUpdateTime == -1) {
			mLastUpdateTime = queryLastUpdateTime();
		}
		
		Request request = new Request(Uri.parse(listSource))
				.setDestinationInExternalFilesDir(this, null, listFileName)
				.setNotificationVisibility(Request.VISIBILITY_HIDDEN)
				.setVisibleInDownloadsUi(false);
		mUpdateTaskId = mDownloadManager.enqueue(request);
	}

	private long queryLastUpdateTime() {
		String[] projection = new String[]{Audio.PUB_DATE};
		String sortOrder = Audio.PUB_DATE + " DESC";
		Cursor cursor = getContentResolver().query(AudioProvider.CONTENT_URI, projection, null, null, sortOrder);
		if (cursor == null) {
			return -1;
		} else if (cursor.getCount() <= 0) {
			cursor.close();
			return -1;
		}
		// FIXME if sortOrder is right, the lastUpdateTime should be the first result
		cursor.moveToFirst();
		long time = cursor.getLong(cursor.getColumnIndex(Audio.PUB_DATE));
		cursor.close();
		return time;
	}

	private void onDownloadComplete(long downloadId) {
		if (downloadId == mUpdateTaskId) {
			updateList(downloadId);
			mUpdateTaskId = -1;
		}
		Query query = new Query();
		query.setFilterByStatus(DownloadManager.STATUS_RUNNING
				| DownloadManager.STATUS_PENDING);
		Cursor cursor = mDownloadManager.query(query);
		if (cursor == null || cursor.getCount() <= 0) {
			stopSelf();
		}
		cursor.close();
	}

	private void updateList(long downloadId) {
		try {
			String localUri = queryLocalUri(downloadId);
			FileInputStream inputStream = new FileInputStream(localUri);
			ContentValues[] valuesArray = parseListXml(inputStream);
			if (valuesArray != null) {
				getContentResolver().bulkInsert(AudioProvider.CONTENT_URI, valuesArray);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	private ContentValues[] parseListXml(InputStream inputStream) throws XmlPullParserException, IOException {
		final List<ContentValues> valueslist = new ArrayList<ContentValues>();
		ContentValues values = null;
		long pubDate = INVALID_TIME;
		long latestPubDate = INVALID_TIME;
		
		final XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inputStream, "UTF-8");
		int eventType = parser.getEventType();
		boolean done = false;
		
		while(eventType!=XmlPullParser.END_DOCUMENT && !done ) {
			String name;
			switch (eventType) {
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if(name.equalsIgnoreCase("item")) {
					values = new ContentValues();
				} else if (values!=null) {
					if (name.equalsIgnoreCase("title")){
                        values.put(Audio.TITLE, parser.nextText());
                    } else if (name.equalsIgnoreCase("pubDate")){
                    	pubDate = parsePubDate(parser.nextText());
                    	values.put(Audio.PUB_DATE, pubDate);
                    } else if (name.equalsIgnoreCase("duration")){
                    	values.put(Audio.DURATION, parseDuration(parser.nextText()));
                    } else if (name.equalsIgnoreCase("enclosure")){
                    	values.put(Audio.AUDIO_URL, parser.getAttributeValue(null, "url"));
                    }
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("item") && values!=null) {
					values.put(Audio.AUTHOR, "佚名");
					values.put(Audio.BROADCASTER, "未知");
					if (pubDate!=INVALID_TIME && pubDate>mLastUpdateTime) {
						valueslist.add(values);
						latestPubDate = (pubDate>latestPubDate)? pubDate : latestPubDate;
						pubDate = INVALID_TIME;
						done = valueslist.size()>=MAX_INIT_PODCAST_NUMBER;
					} else {
						done = true;
					}
					values = null;
				}
				break;
			default:
				break;
			}
			eventType = parser.next();
		}
		mLastUpdateTime = latestPubDate;
		
		if (valueslist == null || valueslist.size() <= 0) {
			return null;
		}
		return valueslist.toArray(new ContentValues[valueslist.size()]);
	}

	private long parseDuration(String time) {
		if (!time.matches("[0-9]{1,2}:[0-9]{2}")) {
			return -1;
		}
		String[] numbers = time.split(":");
		int minute = Integer.valueOf(numbers[0]);
		int second = Integer.valueOf(numbers[1]);
		return minute*60 + second;
	}

	private long parsePubDate(String time) {
		SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z", Locale.US);
		try {
			Date date = format.parse(time);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return -1;
		}
	}

	private String queryLocalUri(long downloadId) {
		Query query = new Query().setFilterById(downloadId);
		Cursor cursor = mDownloadManager.query(query);
		if (cursor == null) {
			mLog.e("download task not found!");
			return null;
		} else if (cursor.getCount() != 1) {
			mLog.e("download task not found!");
			cursor.close();
			return null;
		}
		cursor.moveToFirst();
		String localUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
		cursor.close();
		// Query result of COLUMN_LOCAL_URI startWith "/file:/", "/file://mnt/sdcard" for example
		return localUri.substring("/file:/".length());
	}

	private void onDownloadSubmit(ArrayList<DownloadRequest> requests) {
		for (DownloadRequest downloadRequest : requests) {
			String source = downloadRequest.getSource();
			String destination = downloadRequest.getDestination();
			if (TextUtils.isEmpty(source) || TextUtils.isEmpty(destination)) {
				mLog.e("dowload url or name is empty!");
				return;
			}
			Request request = new Request(Uri.parse(source))
					.setDestinationInExternalFilesDir(this,
							Environment.DIRECTORY_PODCASTS, destination)
					.setNotificationVisibility(Request.VISIBILITY_HIDDEN)
					.setVisibleInDownloadsUi(false);
			mDownloadManager.enqueue(request);
		}
	}

}
