package com.andrew.apolloMod.ui.fragments.list;

import java.util.List;
import java.util.Map;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.justingzju.audioplay.R;

public class TracksFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO Auto-generated method stub
		System.out.println("TracksFragment onCreate");
		getLoaderManager().initLoader(0, null, new CursorLoaderCbks());
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private ListView mListView;
	private SimpleCursorAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View root = inflater.inflate(R.layout.listview, container);
		mListView = (ListView) root.findViewById(android.R.id.list);

		mAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.listview_items, null, new String[] { "name", "url" },
				new int[] { R.id.listview_item_line_one,
						R.id.listview_item_line_two }, 0);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
//				String url = datas.get(arg2).get("url");
//				try {
//					AudioClient.getService().load(url);
//				} catch (RemoteException e) {
//					e.printStackTrace();
//				}
			}
		});
		return root;
	}

	private class CursorLoaderCbks implements LoaderCallbacks<Cursor> {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			// TODO Auto-generated method stub
			return new CursorLoader(getActivity(), Uri.parse("content://com.justingzju.database.PlaylistProvider"), null, null, null, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
			// TODO Auto-generated method stub
			mAdapter.changeCursor(arg1);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {
			// TODO Auto-generated method stub
			mAdapter.changeCursor(null);
		}

	}

}
