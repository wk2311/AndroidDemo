package com.justingzju.fm.v4.fragment;

import static com.justingzju.util.Constant.INVALID_ID;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.justingzju.fm.R;
import com.justingzju.fm.adapter.TrackAdapter;
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

	private static final int NUM_PER_PAGE = 20;

	protected static final int LOADER_TRACK_LIST = 0x00;

	private static final String PREFERENCE_LIMIT = "limit";

	private PullToRefreshListView mListView;
	private TrackAdapter mAdapter;
	
	protected long mFeedId = INVALID_ID;

	private int mFeedLimit;

	private Mode mMode = Mode.DISABLED;

	public static TracksFragment newInstance(long feedId, Mode mode) {
		TracksFragment instance = new TracksFragment();
		instance.mFeedId = feedId;
		instance.mMode  = mode;
		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLog.i("onCreate");
		mFeedLimit = getActivity().getSharedPreferences(TracksFragment.PREFERENCE_LIMIT, Context.MODE_PRIVATE).getInt(String.valueOf(mFeedId), NUM_PER_PAGE);
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
				.findViewById(R.id.track_list);
		mListView.setOnItemClickListener(this);
		mListView.setMode(mMode);
		mListView.setOnRefreshListener(refreshListener);
		
		mAdapter = new TrackAdapter(getActivity(), R.layout.listview_items,
				null, new String[] { Audio.TITLE, Audio.AUTHOR }, new int[] {
						R.id.listview_item_line_one,
						R.id.listview_item_line_two }, 0);
		mListView.setAdapter(mAdapter);
	}

	private OnRefreshListener2<ListView> refreshListener = new OnRefreshListener2<ListView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			Intent updateIntent = new Intent(getActivity(),
					DownloadService.class)
					.setAction(DownloadService.ACTION_UPDATE_FEED);
			updateIntent.putExtra(DownloadService.EXTRA_FEED_ID, mFeedId);
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
			mFeedLimit += NUM_PER_PAGE;
			Editor editor = getActivity().getSharedPreferences(TracksFragment.PREFERENCE_LIMIT, Context.MODE_PRIVATE).edit();
			editor.putInt(String.valueOf(mFeedId), mFeedLimit).commit();
			getLoaderManager().restartLoader(LOADER_TRACK_LIST, null, TracksFragment.this);
		}
	};

	private Handler mRefreshHandler = new Handler();

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(LOADER_TRACK_LIST, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String mSelection = Audio.FEED + "=?";
		String[] mSelectionArgs = new String[]{String.valueOf(mFeedId)};
		String mSortOrder = Audio.PUB_DATE + " DESC";
		String mSortLimit = (mFeedLimit > 0)? " LIMIT " + mFeedLimit : "";
		return new CursorLoader(getActivity(), PodProvider.CONTENT_URI_AUDIOS,
				null, mSelection, mSelectionArgs, mSortOrder + mSortLimit);
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
