package com.justingzju.fm.v4.fragment;

import com.justingzju.fm.R;
import com.justingzju.fm.service.LoadImageTask;
import com.justingzju.fm.service.PlayService;
import com.justingzju.fm.service.PlayUtil;
import com.justingzju.fm.storage.Audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class AudioPlayerFragment extends Fragment implements OnClickListener {
	
	private ImageView mTrackImage;
	private TextView mTrackName;
	private TextView mArtistName;
	private ImageButton mPlay;
	private ImageButton mPrev;
	private ImageButton mNext;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_audio_player, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		mTrackImage = (ImageView) view
				.findViewById(R.id.audio_player_album_art);
		mTrackName = (TextView) view
				.findViewById(R.id.audio_player_track);
		mArtistName = (TextView) view
				.findViewById(R.id.audio_player_album_artist);
		mPlay = (ImageButton) view.findViewById(R.id.audio_player_play);
		mPrev = (ImageButton) view
				.findViewById(R.id.audio_player_prev);
		mNext = (ImageButton) view.findViewById(R.id.audio_player_next);
		
		mPlay.setOnClickListener(this);
		mPrev.setOnClickListener(this);
		mNext.setOnClickListener(this);
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
				Audio audio = intent
						.getParcelableExtra(PlayService.EXTRA_AUDIO);
				onAudioChange(audio);
			} else if (action.equals(PlayService.BROADCAST_PLAYSTATE_CHANGED)) {
				String playstate = intent
						.getStringExtra(PlayService.EXTRA_PLAYSTATE);
				onPlayStateChange(playstate);
			}
		}
	
	};
	
	private void onAudioChange(Audio audio) {
		new LoadImageTask(getActivity(), mTrackImage).execute(audio);
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
