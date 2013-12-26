package com.justingzju.fm.service;

import java.io.IOException;

import com.justingzju.fm.storage.Audio;
import com.justingzju.util.LogUtil;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;
import android.os.RemoteException;

public class PlayService extends Service {

	private static final LogUtil mLog = new LogUtil(
			PlayService.class.getSimpleName(), true);

	public static final String ACTION_CHANGE_AUDIO = PlayService.class
			.getName() + ".change_audio";

	public static final String BROADCAST_AUDIO_CHANGED = PlayService.class
			.getName() + ".audio_changed";
	
	public static final String BROADCAST_PLAYSTATE_CHANGED = PlayService.class
			.getName() + ".playstate_changed";

	public static final String EXTRA_AUDIO = Audio.class.getName();

	public static final String EXTRA_PLAYSTATE = "playstate";

	public static final String PLAYSTATE_PAUSED = "playstate_paused";

	public static final String PLAYSTATE_PLAYING = "playstate_playing";

	private MediaPlayer mMediaPlayer = new MediaPlayer();

	private PlayServiceStub mBinder = new PlayServiceStub();

	public boolean mMediaPlayerPreparing = false;

	@Override
	public void onCreate() {
		super.onCreate();
		mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				mMediaPlayerPreparing = false;
				try {
					mBinder.playOrPause();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onDestroy() {
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
		}
		return START_STICKY;
	}

	private void onAudioChange(Audio audio) {
		mMediaPlayer.reset();
		try {
			mMediaPlayer.setDataSource(audio.getAudioURL());
			mMediaPlayer.prepareAsync();
			mMediaPlayerPreparing = true;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		notifyAudioChange(audio);
	}
	
	private void notifyAudioChange(Audio audio) {
		sendBroadcast(new Intent(BROADCAST_AUDIO_CHANGED).putExtra(EXTRA_AUDIO, audio));
	}

	private void notifyPlayStateChange(String playstate) {
		sendBroadcast(new Intent(BROADCAST_PLAYSTATE_CHANGED).putExtra(EXTRA_PLAYSTATE, playstate));
	}

	private class PlayServiceStub extends IPlayService.Stub {

		@Override
		public void playOrPause() throws RemoteException {
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

		@Override
		public void prev() throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public void next() throws RemoteException {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isPlaying() throws RemoteException {
			return mMediaPlayer.isPlaying();
		}

	}

}
