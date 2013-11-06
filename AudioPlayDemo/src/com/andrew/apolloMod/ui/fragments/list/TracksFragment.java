package com.andrew.apolloMod.ui.fragments.list;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.andrew.apolloMod.ui.fragments.BottomActionBarFragment;
import com.justingzju.Constant;
import com.justingzju.LogUtil;
import com.justingzju.audioplay.AudioClient;
import com.justingzju.audioplay.R;
import com.justingzju.database.AudioProvider;
import com.justingzju.service.AudioService;
import com.justingzju.service.DownloadService;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class TracksFragment extends Fragment {

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		mLog.e("onPause");
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mLog.e("onResume");
	}

	private final static LogUtil mLog = new LogUtil(
			TracksFragment.class.getSimpleName(), true);

	private SimpleCursorAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO Auto-generated method stub
		mLog.e("onCreate");
		mAdapter = new TrackAdapter(getActivity(), R.layout.listview_items,
				null, new String[] { "name", "url" }, new int[] {
						R.id.listview_item_line_one,
						R.id.listview_item_line_two }, 0);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mLog.e("onDestroy");
		super.onDestroy();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mLog.e("onActivityCreated");
	}

	private PullToRefreshListView mListView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mLog.e("onCreateView");
		View root = inflater.inflate(R.layout.listview, container);
		mListView = (PullToRefreshListView) root.findViewById(android.R.id.list);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				try {
					AudioClient.getService().setPostion(arg2-1);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
		mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				getActivity().startService(new Intent(getActivity(), DownloadService.class));
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				
			}
		});
		return root;
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		mLog.e("onDestroyView");
		super.onDestroyView();
	}

	/**
	 * Update the list as needed
	 */
	private final BroadcastReceiver mediaStatusReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(AudioService.PLAYSTATE_CHANGED)) {
				mAdapter.notifyDataSetChanged();
			}
		}
	};

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		mLog.e("onStart");
		super.onStart();
		IntentFilter filter = new IntentFilter();
		filter.addAction(AudioService.PLAYSTATE_CHANGED);
		 filter.addAction(AudioService.META_CHANGED);
		getActivity().registerReceiver(mediaStatusReceiver, filter);
		getLoaderManager().initLoader(0, null, new CursorLoaderCbks());
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		mLog.e("onStop");
		getActivity().unregisterReceiver(mediaStatusReceiver);
		super.onStop();
	}

	private class CursorLoaderCbks implements LoaderCallbacks<Cursor> {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			// TODO Auto-generated method stub
			return new CursorLoader(getActivity(), Constant.PROVIDER_AUDIO, null, null, null, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
			// TODO Auto-generated method stub
			mLog.w("CursorLoaderCbks onLoadFinished");
			mAdapter.changeCursor(arg1);
			mListView.onRefreshComplete();
		}

		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {
			// TODO Auto-generated method stub
			mAdapter.changeCursor(null);
		}

	}

}
