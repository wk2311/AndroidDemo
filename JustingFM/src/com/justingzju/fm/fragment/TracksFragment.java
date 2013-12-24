package com.justingzju.fm.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.justingzju.fm.R;

public class TracksFragment extends Fragment {

//	private PullToRefreshListView mListView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View root = inflater.inflate(R.layout.listview, container);
//		mListView = (PullToRefreshListView) root.findViewById(android.R.id.list);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

}
