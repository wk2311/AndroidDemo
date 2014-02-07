package com.justingzju.fm.service;

import java.io.IOException;
import java.lang.ref.WeakReference;

import com.justingzju.fm.storage.Audio;
import com.justingzju.fm.storage.Feed;
import com.justingzju.fm.storage.PodProvider;
import com.justingzju.util.LogUtil;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

public class PlayService extends Service implements OnPreparedListener, OnBufferingUpdateListener {

	private static final LogUtil mLog = new LogUtil(
			PlayService.class.getSimpleName(), true);

	public static final String ACTION_CHANGE_AUDIO = PlayService.class
			.getName() + ".change_audio";

	public static final String BROADCAST_AUDIO_CHANGED = PlayService.class
			.getName() + ".audio_changed";
	
	public static final String BROADCAST_PLAYSTATE_CHANGED = PlayService.class
			.getName() + ".playstate_changed";

	public static final String EXTRA_AUDIO = Audio.class.getName();

	public static final String EXTRA_FEED = Feed.class.getName();

	public static final String EXTRA_PLAYSTATE = "playstate";

	public static final String PLAYSTATE_PAUSED = "playstate_paused";

	public static final String PLAYSTATE_PLAYING = "playstate_playing";

	private MediaPlayer mMediaPlayer = new MediaPlayer();

	private PlayStub mBinder = new PlayStub();

	public boolean mMediaPlayerPreparing = false;

	private Cursor mAudioCursor = null;

	private int mBufferingPercent = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setOnBufferingUpdateListener(this);
	}

	@Override
	public void onDestroy() {
		mMediaPlayer.release();
		if (mAudioCursor != null) {
			mAudioCursor.close();
		}
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
		}
		return START_STICKY;
	}

	private void onAudioChange(Audio audio) {
		if (mAudioCursor!=null && audio.getId()==mAudioCursor.getInt(mAudioCursor.getColumnIndex(Audio._ID))) {
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
		if (mAudioCursor != null && audio.getFeed()==mAudioCursor.getInt(mAudioCursor.getColumnIndex(Audio.FEED))) {
			moveCursor(audio.getId());
		} else {
			if (mAudioCursor != null) {
				mAudioCursor.close();
			}
			
			// all fields of Audio is selected, or Audio(Cursor) will fail
			String selection = Audio.FEED + "=?";
			String[] selectionArgs = new String[]{String.valueOf(audio.getFeed())};
			String sortOrder = Audio.PUB_DATE + " ASC";
			mAudioCursor = getContentResolver().query(PodProvider.CONTENT_URI_AUDIOS, null, selection, selectionArgs, sortOrder);
			
			moveCursor(audio.getId());
		}
	}

	private void moveCursor(long audioId) {
		mAudioCursor.moveToFirst();
		while (!mAudioCursor.isAfterLast()) {
			if (audioId == mAudioCursor.getInt(mAudioCursor.getColumnIndex(Audio._ID))) {
				break;
			}
			mAudioCursor.moveToNext();
		}
	}

	private void notifyAudioChange(Audio audio) {
		Cursor cursor = getContentResolver().query(PodProvider.CONTENT_URI_FEEDS, null, Feed._ID + "=?", new String[]{String.valueOf(audio.getFeed())}, null);
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
		sendStickyBroadcast(new Intent(BROADCAST_AUDIO_CHANGED).putExtra(EXTRA_AUDIO, audio).putExtra(EXTRA_FEED, feed));
	}

	private void notifyPlayStateChange(String playstate) {
		sendStickyBroadcast(new Intent(BROADCAST_PLAYSTATE_CHANGED).putExtra(EXTRA_PLAYSTATE, playstate));
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
				notifyPlayStateChange( PLAYSTATE_PAUSED);
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

}
