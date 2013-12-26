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

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.justingzju.fm.R;
import com.justingzju.fm.service.DownloadService;
import com.justingzju.fm.service.PlayService;
import com.justingzju.fm.storage.Audio;
import com.justingzju.fm.storage.AudioProvider;
import com.justingzju.fm.widgets.TrackAdapter;
import com.justingzju.util.LogUtil;

public class TracksFragment extends Fragment implements
		LoaderCallbacks<Cursor>, OnItemClickListener {

	private static final LogUtil mLog = new LogUtil(
			TracksFragment.class.getSimpleName(), true);

	private PullToRefreshListView mListView;
	private TrackAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new TrackAdapter(getActivity(), R.layout.listview_items,
				null, new String[] { Audio.TITLE, Audio.AUTHOR }, new int[] {
						R.id.listview_item_line_one,
						R.id.listview_item_line_two }, 0);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.pull_refresh_listview, container);
		mListView = (PullToRefreshListView) root
				.findViewById(android.R.id.list);
		mListView.setAdapter(mAdapter);

		mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				Intent updateIntent = new Intent(getActivity(),
						DownloadService.class)
						.setAction(DownloadService.ACTION_UPDATE_PODLIST);
				getActivity().startService(updateIntent);
				mRefreshHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						mListView.onRefreshComplete();
					}
				}, 5000);
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub

			}
		});
		mListView.setOnItemClickListener(this);
		return root;
	}

	Handler mRefreshHandler = new Handler();

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String sortOrder = Audio.PUB_DATE + " DESC";
		// all fields of Audio is selected, or Audio(Cursor) will fail
		return new CursorLoader(getActivity(), AudioProvider.CONTENT_URI, null,
				null, null, sortOrder);
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
		Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
		Audio audio = new Audio(cursor);
		getActivity().startService(
				new Intent(getActivity(), PlayService.class).setAction(
						PlayService.ACTION_CHANGE_AUDIO).putExtra(
						PlayService.EXTRA_AUDIO, audio));
	}
}
