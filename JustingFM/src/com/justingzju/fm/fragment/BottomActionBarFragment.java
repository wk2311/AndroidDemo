package com.justingzju.fm.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import com.justingzju.fm.service.PlayUtil;
import com.justingzju.fm.storage.Audio;
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


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.bottom_action_bar, container);

		mBottomActionBar = (LinearLayout) root
				.findViewById(R.id.bottom_action_bar);
		mTrackImage = (ImageView) root.findViewById(R.id.bottom_action_bar_album_art);
		mTrackName = (TextView) root.findViewById(R.id.bottom_action_bar_track_name);
		mArtistName = (TextView) root.findViewById(R.id.bottom_action_bar_artist_name);
		mPlay = (ImageButton) root.findViewById(R.id.bottom_action_bar_play);
		mPrev = (ImageButton) root
				.findViewById(R.id.bottom_action_bar_previous);
		mNext = (ImageButton) root.findViewById(R.id.bottom_action_bar_next);

		mBottomActionBar.setOnClickListener(this);
		mPlay.setOnClickListener(this);
		mPrev.setOnClickListener(this);
		mNext.setOnClickListener(this);

		return root;
	}

	@Override
	public void onStart() {
		super.onStart();
		IntentFilter filter = new IntentFilter();
		filter.addAction(PlayService.BROADCAST_AUDIO_CHANGED);
		filter.addAction(PlayService.BROADCAST_PLAYSTATE_CHANGED);
		getActivity().registerReceiver(mReceiver, filter);
		
		PlayUtil.startAndBindService(getActivity());
	}

	@Override
	public void onStop() {
		PlayUtil.unbindService(getActivity());
		
		getActivity().unregisterReceiver(mReceiver);
		super.onStop();
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
	
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(PlayService.BROADCAST_AUDIO_CHANGED)) {
				Audio audio = intent.getParcelableExtra(PlayService.EXTRA_AUDIO);
				onAudioChange(audio);
			} else if (action.equals(PlayService.BROADCAST_PLAYSTATE_CHANGED)) {
				String playstate = intent.getStringExtra(PlayService.EXTRA_PLAYSTATE);
				onPlayStateChange(playstate);
			}
		}
		
	};


	private void onAudioChange(Audio audio) {
		mTrackImage.setImageResource(R.drawable.justpod_cn);
	    mTrackName.setText(audio.getTitle());
	    mArtistName.setText(audio.getAuthor());
	    mPlay.setImageResource(R.drawable.apollo_holo_light_play);
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
			return;
		}
		try {
			if (v.equals(mPlay)) {
				PlayUtil.mService.playOrPause();
			} else if (v.equals(mPrev)) {
				PlayUtil.mService.prev();
			} else if (v.equals(mNext)) {
				PlayUtil.mService.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
