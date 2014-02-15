package com.justingzju.fm.v4.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.justingzju.fm.R;
import com.justingzju.fm.adapter.TrackAdapter;
import com.justingzju.fm.service.PlayService;
import com.justingzju.fm.storage.Audio;
import com.justingzju.fm.storage.Feed;
import com.justingzju.fm.storage.PodProvider;
import com.justingzju.util.LogUtil;

import static com.justingzju.util.Constant.INVALID_ID;

public class PlaylistFragment extends TracksFragment {

	@Override
	public void onStart() {
		super.onStart();
		IntentFilter filter = new IntentFilter();
		filter.addAction(PlayService.BROADCAST_AUDIO_CHANGED);
		getActivity().registerReceiver(mReceiver, filter);
	}

	@Override
	public void onStop() {
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
				Feed feed = intent
						.getParcelableExtra(PlayService.EXTRA_FEED);
				onAudioChange(audio, feed);
			}
		}
	
	};

	private void onAudioChange(Audio audio, Feed feed) {
		mFeedId = audio.getFeed();
		getLoaderManager().restartLoader(LOADER_TRACK_LIST, null, this);
	}

}
