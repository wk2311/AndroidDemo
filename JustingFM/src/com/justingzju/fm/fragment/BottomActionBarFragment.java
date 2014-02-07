package com.justingzju.fm.fragment;

import android.app.Fragment;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.justingzju.fm.R;
import com.justingzju.fm.service.PlayService;
import com.justingzju.fm.service.PlayService.PlayStub;
import com.justingzju.fm.storage.Audio;
import com.justingzju.fm.storage.Feed;
import com.justingzju.fm.v4.activity.AudioPlayer;
import com.justingzju.util.ImageUtil;
import com.justingzju.util.LogUtil;

public class BottomActionBarFragment extends Fragment implements
		OnClickListener {
	private static final LogUtil mLog = new LogUtil(
			BottomActionBarFragment.class.getSimpleName(), true);

	private LinearLayout mBottomActionBar;
	private ImageView mTrackImage;
	private TextView mTrackName;
	private TextView mArtistName;
	private ImageButton mPlay;
	private ImageButton mPrev;
	private ImageButton mNext;

	private PlayStub mPlayStub;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_bottom_action_bar, container,
				false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mBottomActionBar = (LinearLayout) view
				.findViewById(R.id.bottom_action_bar);
		mTrackImage = (ImageView) view
				.findViewById(R.id.bottom_action_bar_album_art);
		mTrackName = (TextView) view
				.findViewById(R.id.bottom_action_bar_track_name);
		mArtistName = (TextView) view
				.findViewById(R.id.bottom_action_bar_artist_name);
		mPlay = (ImageButton) view.findViewById(R.id.bottom_action_bar_play);
		mPrev = (ImageButton) view
				.findViewById(R.id.bottom_action_bar_previous);
		mNext = (ImageButton) view.findViewById(R.id.bottom_action_bar_next);

		mBottomActionBar.setOnClickListener(this);
		mPlay.setOnClickListener(this);
		mPrev.setOnClickListener(this);
		mNext.setOnClickListener(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		getActivity().bindService(new Intent(getActivity(), PlayService.class), mConnection, Service.BIND_AUTO_CREATE);

		IntentFilter filter = new IntentFilter();
		filter.addAction(PlayService.BROADCAST_AUDIO_CHANGED);
		filter.addAction(PlayService.BROADCAST_PLAYSTATE_CHANGED);
		getActivity().registerReceiver(mReceiver, filter);
	}

	@Override
	public void onStop() {
		getActivity().unregisterReceiver(mReceiver);

		getActivity().unbindService(mConnection);
		super.onStop();
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mPlayStub = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mPlayStub = (PlayStub) service;
		}
	};

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(PlayService.BROADCAST_AUDIO_CHANGED)) {
				Audio audio = intent
						.getParcelableExtra(PlayService.EXTRA_AUDIO);
				Feed feed = intent
						.getParcelableExtra(PlayService.EXTRA_FEED);
				onAudioChange(audio, feed);
			} else if (action.equals(PlayService.BROADCAST_PLAYSTATE_CHANGED)) {
				String playstate = intent
						.getStringExtra(PlayService.EXTRA_PLAYSTATE);
				onPlayStateChange(playstate);
			}
		}

	};
	
	private void onAudioChange(Audio audio, Feed feed) {
		ImageUtil.displayImage(feed.getImageLink(), mTrackImage);
		mTrackName.setText(audio.getTitle());
		mArtistName.setText(audio.getAuthor());
		onPlayStateChange(PlayService.PLAYSTATE_PAUSED);
	}
	
	private void onPlayStateChange(String playstate) {
		if (playstate.equals(PlayService.PLAYSTATE_PLAYING)) {
			mPlay.setImageResource(R.drawable.apollo_holo_light_pause);
		} else {
			mPlay.setImageResource(R.drawable.apollo_holo_light_play);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.equals(mBottomActionBar)) {
			mLog.v("bottom_action_bar onClick");
			startActivity(new Intent(getActivity(), AudioPlayer.class));
			return;
		}
		try {
			if (v.equals(mPlay)) {
				mPlayStub.playOrPause();
			} else if (v.equals(mPrev)) {
				mPlayStub.prev();
			} else if (v.equals(mNext)) {
				mPlayStub.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
