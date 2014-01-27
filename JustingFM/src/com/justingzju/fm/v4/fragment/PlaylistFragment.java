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
import com.justingzju.fm.storage.PodProvider;
import com.justingzju.util.LogUtil;

import static com.justingzju.util.Constant.INVALID_ID;

public class PlaylistFragment extends Fragment implements
		LoaderCallbacks<Cursor>, OnItemClickListener {

	public static final String TAG = PlaylistFragment.class.getSimpleName();

	private static final LogUtil mLog = new LogUtil(
			PlaylistFragment.class.getSimpleName(), true);

	private static final int LOADER_PLAY_LIST = 0;

	private ListView mListView;
	private TrackAdapter mAdapter;

	private long mFeedId = INVALID_ID;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new TrackAdapter(getActivity(), R.layout.listview_items,
				null, new String[] { Audio.TITLE, Audio.AUTHOR }, new int[] {
						R.id.listview_item_line_one,
						R.id.listview_item_line_two }, 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.listview, container, false);
		mListView = (ListView) root.findViewById(android.R.id.list);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(this);
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

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
				onAudioChange(audio);
			}
		}

	};

	private void onAudioChange(Audio audio) {
		if (getLoaderManager().getLoader(LOADER_PLAY_LIST) == null
				|| mFeedId != audio.getFeed()) {
			mFeedId = audio.getFeed();
			getLoaderManager().initLoader(LOADER_PLAY_LIST, null, this);
		}
		// TODO change the animation that shows the playing audio
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// all fields of Audio is selected, or Audio(Cursor) will fail
		String selection = Audio.FEED + "=?";
		String[] selectionArgs = new String[] { String.valueOf(mFeedId) };
		String sortOrder = Audio.PUB_DATE + " DESC";
		return new CursorLoader(getActivity(), PodProvider.CONTENT_URI_AUDIOS,
				null, selection, selectionArgs, sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mLog.v("onLoadFinished");
		mAdapter.changeCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mLog.v("onLoaderReset");
		mAdapter.changeCursor(null);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mLog.v("onItemClick: position " + position + ", id " + id);
		Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
		Audio audio = new Audio(cursor);
		getActivity().startService(
				new Intent(getActivity(), PlayService.class).setAction(
						PlayService.ACTION_CHANGE_AUDIO).putExtra(
						PlayService.EXTRA_AUDIO, audio));
	}
}
