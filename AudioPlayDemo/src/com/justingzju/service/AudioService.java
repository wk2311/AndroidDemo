package com.justingzju.service;

import com.justingzju.Constant;
import com.justingzju.LogUtil;
import com.justingzju.audioplay.AudioBarActivity;
import com.justingzju.database.AudioProvider;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

public class AudioService extends Service {
	
	private final static LogUtil mLog = new LogUtil(AudioService.class.getSimpleName(), true);

	public static final String PLAYSTATE_CHANGED = AudioService.class.getName()+".PLAYSTATE_CHANGED";
	
	public static final String META_CHANGED = AudioService.class.getName()+".META_CHANGED";

	private MediaPlayer mMediaPlayer;
	
	private Cursor mCursor;

	private ContentObserver mContentObserver;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mLog.i("onCreate");
		// TODO Auto-generated method stub
		mMediaPlayer = new MediaPlayer();
		mBinder = new AudioServiceStub();
		
		mCursor = getContentResolver().query(Constant.PROVIDER_AUDIO, null, null, null, null);
		mContentObserver = new AudioContentObserver(new Handler());
		getContentResolver().registerContentObserver(Constant.PROVIDER_AUDIO, true, mContentObserver);
		mBinder.next();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
		getContentResolver().unregisterContentObserver(mContentObserver);
//		mCursor.close();
		
		mMediaPlayer.stop();
		mMediaPlayer.release();
		
		mLog.i("onDestroy");
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return START_STICKY;
	}

	private AudioServiceStub mBinder;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		mLog.i("onBind");
		return mBinder;
	}
	
	private void broadcastChange(String action) {
		Intent sendIntent = new Intent(action);
		sendStickyBroadcast(sendIntent);
//		sendBroadcast(sendIntent);
	}
	
	private void reload() {
		mMediaPlayer.reset();
		broadcastChange(PLAYSTATE_CHANGED);
		String url = mCursor.getString(mCursor.getColumnIndex("url"));
		try {
			mMediaPlayer.setDataSource(url);
			mMediaPlayer.prepareAsync();
		} catch (Exception e) {
			e.printStackTrace();
		}
		broadcastChange(META_CHANGED);
	}

	private class AudioServiceStub extends IAudioService.Stub {
		
		@Override
		public void play() {
			mMediaPlayer.start();
			broadcastChange(PLAYSTATE_CHANGED);
		}

		@Override
		public void pause() {
			mMediaPlayer.pause();
			broadcastChange(PLAYSTATE_CHANGED);
		}

		@Override
		public boolean isPlaying() {
			return mMediaPlayer.isPlaying();
		}

		@Override
		public void prev() {
			if(mCursor.isFirst() || mCursor.isBeforeFirst()) {
				mCursor.moveToLast();
			}
			else {
				mCursor.moveToPrevious();
			}
			reload();
		}

		@Override
		public void next() {
			if(mCursor.isLast() || mCursor.isAfterLast()) {
				mCursor.moveToFirst();
			} else {
				mCursor.moveToNext();
			}
			reload();
		}

		@Override
		public String getAudioName() throws RemoteException {
			// TODO Auto-generated method stub
			return mCursor.getString(mCursor.getColumnIndex("name"));
		}

		@Override
		public int getPosition() throws RemoteException {
			// TODO Auto-generated method stub
			return mCursor.getPosition();
		}

		@Override
		public void setPostion(int position) throws RemoteException {
			// TODO Auto-generated method stub
			mCursor.moveToPosition(position);
			reload();
		}

	}

	private class AudioContentObserver extends ContentObserver {

		public AudioContentObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			mLog.i("AudioContentObserver onChange");
			if(mCursor!=null || !mCursor.isClosed())
				mCursor.close();
			mCursor = getContentResolver().query(Constant.PROVIDER_AUDIO, null, null, null, null);
			mBinder.next();
		}

		@Override
		public void onChange(boolean selfChange, Uri uri) {
			mLog.i("AudioContentObserver onChange");
			if(mCursor!=null || !mCursor.isClosed())
				mCursor.close();
			mCursor = getContentResolver().query(uri, null, null, null, null);
			mBinder.next();
		}
		
	}
}
