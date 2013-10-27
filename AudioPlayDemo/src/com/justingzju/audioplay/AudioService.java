package com.justingzju.audioplay;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;

public class AudioService extends Service {

	private MediaPlayer mMediaPlayer;
	
	private List<String> urls;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		// TODO Auto-generated method stub
		mMediaPlayer = new MediaPlayer();
		mBinder = new AudioServiceStub();
		
//		urls = mDBManager.select("url");
		
//		try {
//			mBinder.next();
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mMediaPlayer.stop();
		mMediaPlayer.release();
		
		super.onDestroy();
	}

	public static final String PLAYSTATE_CHANGED = AudioService.class.getName()+".PLAYSTATE_CHANGED";
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		return super.onStartCommand(intent, flags, startId);
	}

	private void broadcastChange(String action) {
		Intent sendIntent = new Intent(action);
		sendStickyBroadcast(sendIntent);
	}

	private AudioServiceStub mBinder;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	private int mPlayPos = -1;
	
	public class AudioServiceStub extends IAudioService.Stub {
		
		@Override
		public void play() throws RemoteException {
			mMediaPlayer.start();
			broadcastChange(PLAYSTATE_CHANGED);
		}

		@Override
		public void pause() throws RemoteException {
			mMediaPlayer.pause();
			broadcastChange(PLAYSTATE_CHANGED);
		}

		@Override
		public boolean isPlaying() throws RemoteException {
			return mMediaPlayer.isPlaying();
		}

		@Override
		public void prev() throws RemoteException {
			mPlayPos = (mPlayPos<=0)? urls.size()-1 : mPlayPos-1;
			load(urls.get(mPlayPos));
//			mDBManager.drop();
		}

		@Override
		public void next() throws RemoteException {
			mPlayPos = (mPlayPos>=urls.size()-1 || mPlayPos<0)? 0 : mPlayPos+1;
			load(urls.get(mPlayPos));
//			mDBManager.insert("福利国家将带来什么", "http://dl.justing.com.cn:8081/pod2/spectopic/free/4806cFaJmqI6.mp3");
//			mDBManager.insert("皆大欢喜等于空欢喜", "http://dl.justing.com.cn:8081/pod2/spectopic/free/53384cuZGQv5.mp3");
//			mDBManager.insert("如果国家福利体系缺失", "http://dl.justing.com.cn:8081/pod2/spectopic/free/5148cvDSlV8c1.mp3");
//			mDBManager.insert("奥运史上的丑闻", "http://dl.justing.com.cn:8081/pod2/spectopic/free/1220cDSPLeGi.mp3");
//			mDBManager.insert("中国冬残奥运动员的孤单", "http://dl.justing.com.cn:8081/pod2/spectopic/free/7122c6qI3yUs.mp3");
//			mDBManager.insert("近代武力解救人质事件", "http://dl.justing.com.cn:8081/pod2/spectopic/free/1157c6ssgOD9Y.mp3");
//			mDBManager.insert("威尔玛鲁道夫：一只巴掌拍响世界", "http://dl.justing.com.cn:8081/pod2/spectopic/free/978chNCeMxE.mp3");
//			mDBManager.insert("瞬间华彩：1968年有2分钟属于史密斯", "http://dl.justing.com.cn:8081/pod2/spectopic/free/1570cAs_xF.mp3");
//			mDBManager.insert("NBA－建立在篮球场上的跨国公司", "http://dl.justing.com.cn:8081/pod2/spectopic/free/202cho4bMn.mp3");
//			mDBManager.insert("民主是个技术活29：英国免费医疗", "http://dl.justing.com.cn:8081/pod2/spectopic/free/45586cXrK7Vzpe.mp3");
		}

		@Override
		public void load(String url) throws RemoteException {
			// TODO Auto-generated method stub
			try {
				mMediaPlayer.reset();
				mMediaPlayer.setDataSource(url);
				mMediaPlayer.prepareAsync();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
