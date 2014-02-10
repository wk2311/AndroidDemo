package com.justingzju.fm.fragment;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.justingzju.fm.R;
import com.justingzju.fm.adapter.TrackAdapter;
import com.justingzju.fm.service.DownloadRequest;
import com.justingzju.fm.service.DownloadService;
import com.justingzju.fm.service.PlayService;
import com.justingzju.fm.storage.Audio;
import com.justingzju.fm.storage.Feed;
import com.justingzju.fm.storage.PodProvider;
import com.justingzju.util.LogUtil;

public class TracksFragment extends Fragment implements
		LoaderCallbacks<Cursor>, OnItemClickListener {

	public static final String TAG = TracksFragment.class.getSimpleName();

	private static final LogUtil mLog = new LogUtil(
			TracksFragment.class.getSimpleName(), true);

	private PullToRefreshListView mListView;
	private TrackAdapter mAdapter;

	private Feed mFeed;

	public static TracksFragment newInstance(Feed feed) {
		TracksFragment instance = new TracksFragment();
		instance.mFeed = feed;
		return instance;
	}

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
		return inflater.inflate(R.layout.fragment_tracks, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mListView = (PullToRefreshListView) view
				.findViewById(android.R.id.list);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(this);
		mListView.setOnRefreshListener(refreshListener);
	}

	private OnRefreshListener2<ListView> refreshListener = new OnRefreshListener2<ListView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			Intent updateIntent = new Intent(getActivity(),
					DownloadService.class)
					.setAction(DownloadService.ACTION_UPDATE_FEED);
			updateIntent.putExtra(DownloadService.EXTRA_FEED_ID, mFeed.getId());
			getActivity().startService(updateIntent);
			
			mRefreshHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mListView.onRefreshComplete();
				}
			}, 5000);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			// TODO Auto-generated method stub
			Toast.makeText(getActivity(), "pull up to refresh", Toast.LENGTH_SHORT).show();
		}
	};

	private Handler mRefreshHandler = new Handler();

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String sortOrder = Audio.PUB_DATE + " DESC";
		// all fields of Audio is selected, or Audio(Cursor) will fail
		String selection = Audio.FEED + "=?";
		String[] selectionArgs = new String[]{String.valueOf(mFeed.getId())};
		return new CursorLoader(getActivity(), PodProvider.CONTENT_URI_AUDIOS,
				null, selection, selectionArgs, sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mLog.v("onLoadFinished");
		mAdapter.changeCursor(data);
		mListView.onRefreshComplete();
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
