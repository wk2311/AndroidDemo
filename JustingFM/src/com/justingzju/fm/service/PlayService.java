package com.justingzju.fm.service;

import java.io.IOException;
import java.lang.ref.WeakReference;

import com.justingzju.fm.R;
import com.justingzju.fm.storage.Audio;
import com.justingzju.fm.storage.Feed;
import com.justingzju.fm.storage.PodProvider;
import com.justingzju.fm.v4.activity.AudioPlayer;
import com.justingzju.util.ImageUtil;
import com.justingzju.util.LogUtil;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.widget.RemoteViews;

public class PlayService extends Service implements OnPreparedListener,
		OnBufferingUpdateListener {

	private static final LogUtil mLog = new LogUtil(
			PlayService.class.getSimpleName(), true);

	public static final String ACTION_CHANGE_AUDIO = PlayService.class
			.getName() + ".change_audio";

	private static final String ACTION_PLAY_PAUSE = PlayService.class
	.getName() + ".play_pause";

	private static final String ACTION_NEXT = PlayService.class
	.getName() + ".next";

	private static final String ACTION_COLLAPSE = PlayService.class
	.getName() + ".finish";

	public static final String BROADCAST_AUDIO_CHANGED = PlayService.class
			.getName() + ".audio_changed";

	public static final String BROADCAST_PLAYSTATE_CHANGED = PlayService.class
			.getName() + ".playstate_changed";

	public static final String EXTRA_AUDIO = Audio.class.getName();

	public static final String EXTRA_FEED = Feed.class.getName();

	public static final String EXTRA_PLAYSTATE = "playstate";

	public static final String PLAYSTATE_PAUSED = "playstate_paused";

	public static final String PLAYSTATE_PLAYING = "playstate_playing";

	private static final int NOTIFICATION_STATUS_BAR = 0x01;

	private static final int REQUEST_AUDIOPLAYER = 0x01;

	private static final int REQUEST_PLAYSERVICE = 0x02;

	private MediaPlayer mMediaPlayer = new MediaPlayer();

	private PlayStub mBinder = new PlayStub();

	private boolean mMediaPlayerPreparing = false;

	private Cursor mAudioCursor = null;

	private int mBufferingPercent = 0;

	private NotificationHolder mNotificationHolder;

	@Override
	public void onCreate() {
		super.onCreate();

		mNotificationHolder = new NotificationHolder();

		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setOnBufferingUpdateListener(this);
	}
	
	@Override
	public void onDestroy() {
		mNotificationHolder.collapseNotification();
		if (mAudioCursor != null) {
			mAudioCursor.close();
		}
		mMediaPlayer.release();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null) {
			mLog.e("onStartCommand intent null");
			return START_STICKY;
		}
		String action = intent.getAction();
		if (ACTION_CHANGE_AUDIO.equals(action)) {
			Audio audio = intent.getParcelableExtra(EXTRA_AUDIO);
			onAudioChange(audio);
		} else if (ACTION_PLAY_PAUSE.equals(action)) {
			mBinder.playOrPause();
		} else if (ACTION_NEXT.equals(action)) {
			mBinder.next();
		}
		return START_STICKY;
	}

	private void onAudioChange(Audio audio) {
		if (mAudioCursor != null
				&& audio.getId() == mAudioCursor.getInt(mAudioCursor
						.getColumnIndex(Audio._ID))) {
			return;
		}
		alterCursor(audio);
		changeAudio(audio);
	}

	private void changeAudio(Audio audio) {
		mMediaPlayer.reset();
		try {
			mMediaPlayer.setDataSource(audio.getLink());
			mMediaPlayer.prepareAsync();
			mMediaPlayerPreparing = true;
			notifyAudioChange(audio);
		} catch (Exception e) {
			e.printStackTrace();
			notifyPlayStateChange(PLAYSTATE_PAUSED);
			return;
		}
	}

	private void alterCursor(Audio audio) {
		if (mAudioCursor != null
				&& audio.getFeed() == mAudioCursor.getInt(mAudioCursor
						.getColumnIndex(Audio.FEED))) {
			moveCursor(audio.getId());
		} else {
			if (mAudioCursor != null) {
				mAudioCursor.close();
			}

			// all fields of Audio is selected, or Audio(Cursor) will fail
			String selection = Audio.FEED + "=?";
			String[] selectionArgs = new String[] { String.valueOf(audio
					.getFeed()) };
			String sortOrder = Audio.PUB_DATE + " ASC";
			mAudioCursor = getContentResolver().query(
					PodProvider.CONTENT_URI_AUDIOS, null, selection,
					selectionArgs, sortOrder);

			moveCursor(audio.getId());
		}
	}

	private void moveCursor(long audioId) {
		mAudioCursor.moveToFirst();
		while (!mAudioCursor.isAfterLast()) {
			if (audioId == mAudioCursor.getInt(mAudioCursor
					.getColumnIndex(Audio._ID))) {
				break;
			}
			mAudioCursor.moveToNext();
		}
	}

	private void notifyAudioChange(Audio audio) {
		Cursor cursor = getContentResolver().query(
				PodProvider.CONTENT_URI_FEEDS, null, Feed._ID + "=?",
				new String[] { String.valueOf(audio.getFeed()) }, null);
		Feed feed;
		if (cursor == null || cursor.getCount() <= 0) {
			feed = null;
		} else {
			cursor.moveToFirst();
			feed = new Feed(cursor);
		}
		if (cursor != null) {
			cursor.close();
		}
		// XXX check feed is null or not
		mNotificationHolder.updateNotification(feed.getImageLink());
		sendStickyBroadcast(new Intent(BROADCAST_AUDIO_CHANGED).putExtra(
				EXTRA_AUDIO, audio).putExtra(EXTRA_FEED, feed));
	}

	private void notifyPlayStateChange(String playstate) {
		mNotificationHolder.updateNotification(null);
		sendStickyBroadcast(new Intent(BROADCAST_PLAYSTATE_CHANGED).putExtra(
				EXTRA_PLAYSTATE, playstate));
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		mBufferingPercent = percent;
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
		mMediaPlayerPreparing = false;
		mBinder.playOrPause();
	}

	public class PlayStub extends Binder {

		public void playOrPause() {
			if (mAudioCursor == null) {
				return;
			}
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.pause();
				notifyPlayStateChange(PLAYSTATE_PAUSED);
			} else if (mMediaPlayerPreparing) {
				// TODO nofity user the audio is preparing
				mLog.i("audio is loading");
			} else {
				mMediaPlayer.start();
				notifyPlayStateChange(PLAYSTATE_PLAYING);
			}
		}

		public void prev() {
			if (mAudioCursor == null) {
				return;
			}
			if (mAudioCursor.isFirst()) {
				mAudioCursor.moveToLast();
			} else {
				mAudioCursor.moveToPrevious();
			}
			changeAudio(new Audio(mAudioCursor));
		}

		public void next() {
			if (mAudioCursor == null) {
				return;
			}
			if (mAudioCursor.isLast()) {
				mAudioCursor.moveToFirst();
			} else {
				mAudioCursor.moveToNext();
			}
			changeAudio(new Audio(mAudioCursor));
		}

		public int getCurrentPosition() {
			if (mAudioCursor == null) {
				return 0;
			}
			return mMediaPlayer.getCurrentPosition();
		}

		public int getBufferingPercent() {
			if (mAudioCursor == null) {
				return 0;
			}
			return mBufferingPercent;
		}

		public int getDuration() {
			if (mAudioCursor == null) {
				return 0;
			}
			return mMediaPlayer.getDuration();
		}

	}

	private class NotificationHolder implements ImageLoadingListener {
		
		private NotificationManager mNotificationManager;
		
		private NotificationCompat.Builder mNotificationBuilder;
		 
		public NotificationHolder() {
			mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			
			RemoteViews notificationViews = new RemoteViews(getPackageName(), R.layout.status_bar);
			notificationViews.setOnClickPendingIntent(R.id.status_bar_play, createNotificationClickIntent(ACTION_PLAY_PAUSE));
			notificationViews.setOnClickPendingIntent(R.id.status_bar_next, createNotificationClickIntent(ACTION_NEXT));
			notificationViews.setOnClickPendingIntent(R.id.status_bar_collapse, createNotificationClickIntent(ACTION_COLLAPSE));
			
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(PlayService.this)
					.addParentStack(AudioPlayer.class)
					.addNextIntent(new Intent(PlayService.this, AudioPlayer.class));
			mNotificationBuilder = new Builder(PlayService.this)
					.setOngoing(true)
					.setContent(notificationViews)
					.setContentIntent(
							stackBuilder.getPendingIntent(REQUEST_AUDIOPLAYER,
									PendingIntent.FLAG_UPDATE_CURRENT));
		}
	
		public void collapseNotification() {
			mNotificationManager.cancel(NOTIFICATION_STATUS_BAR);
		}

		private PendingIntent createNotificationClickIntent(String action) {
			Intent buttonIntent = new Intent(PlayService.this, PlayService.class).setAction(action);
			return PendingIntent.getService(getApplicationContext(), REQUEST_PLAYSERVICE, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		}
	
		public void updateNotification(String imageLink) {
			// FIXME loadImage make cause OutOfMemoryException
//			if (imageLink != null) {
//				ImageUtil.loadImage(imageLink, this);
//			}
			mNotificationManager.notify(NOTIFICATION_STATUS_BAR, getNotification(null));
		}

		private Notification getNotification(Bitmap bitmap) {
			Notification notification = mNotificationBuilder.build();
			notification.icon = R.drawable.ic_launcher;
			notification.contentView
					.setTextViewText(R.id.status_bar_track_name, mAudioCursor
							.getString(mAudioCursor.getColumnIndex(Audio.TITLE)));
			notification.contentView.setTextViewText(R.id.status_bar_artist_name,
					mAudioCursor.getString(mAudioCursor
							.getColumnIndex(Audio.AUTHOR)));
			if (mMediaPlayer.isPlaying()) {
				notification.contentView.setImageViewResource(R.id.status_bar_play, R.drawable.apollo_holo_dark_pause);
			} else {
				notification.contentView.setImageViewResource(R.id.status_bar_play, R.drawable.apollo_holo_dark_play);
			}
			
			if (bitmap != null) {
				notification.contentView.setImageViewBitmap(R.id.status_bar_icon, bitmap);
			}
			return notification;
		}

		@Override
		public void onLoadingCancelled(String arg0, View arg1) {
			
		}

		@Override
		public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
			mNotificationManager.notify(NOTIFICATION_STATUS_BAR, getNotification(arg2));
		}

		@Override
		public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			
		}

		@Override
		public void onLoadingStarted(String arg0, View arg1) {
			
		}
	}

}
