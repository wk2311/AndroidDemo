package com.justingzju.audioplay;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;

public class MainActivity extends InitializableActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private MediaPlayer myPlayer;
	private Timer timer;
	private TimerTask timerTask;
	@Override
	protected void initData(Bundle savedInstanceState) {
		myPlayer = new MediaPlayer();
		timer = new Timer();
	}

	private Button btnPlayPause;
	private Button btnPrepare;
	private Button btnStop;
	private SeekBar skbProgress;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_main);
		
		btnPrepare = (Button) this.findViewById(R.id.btnPlayUrl);
		btnPlayPause = (Button) this.findViewById(R.id.btnPause);
		btnStop = (Button) this.findViewById(R.id.btnStop);
		
		skbProgress = (SeekBar) this.findViewById(R.id.skbProgress);
	}

	@Override
	protected void initListener() {
		btnPrepare.setOnClickListener(new ClickListener());
		btnPlayPause.setOnClickListener(new ClickListener());
		btnStop.setOnClickListener(new ClickListener());
		
		skbProgress.setOnSeekBarChangeListener(new SeekBarChangeListener());
		
		myPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
			@Override
			public void onBufferingUpdate(MediaPlayer mp, int percent) {
				skbProgress.setSecondaryProgress(percent);
			}
		});
	}

	class ClickListener implements OnClickListener {
	
		@Override
		public void onClick(View arg0) {
			if (arg0 == btnPlayPause) {
				if(myPlayer.isPlaying()) {
					btnPlayPause.setText("播放");
					timerTask.cancel();
					myPlayer.pause();
				} else {
					myPlayer.start();
					timerTask = new TimerTask() {
						@Override
						public void run() {
							skbProgress.setProgress(skbProgress.getMax()*myPlayer.getCurrentPosition()/myPlayer.getDuration());
						}
					};
					timer.schedule(timerTask, 0, 1000);
					btnPlayPause.setText("暂停");
				}
			} else if (arg0 == btnPrepare) {
				try {
					myPlayer.reset();
					myPlayer.setDataSource("http://dl.justing.com.cn:8081/pod2/spectopic/free/4806cFaJmqI6.mp3");
					myPlayer.prepareAsync();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (arg0 == btnStop) {
				myPlayer.stop();
			}
		}
	}

	class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
		int progress;
	
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
			this.progress = progress * myPlayer.getDuration()
					/ seekBar.getMax();
		}
	
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
	
		}
	
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
			myPlayer.seekTo(progress);
		}
	}

}