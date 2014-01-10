package com.justingzju.fm.fragment;

import com.justingzju.fm.R;
import com.justingzju.fm.service.LoadImageTask;
import com.justingzju.fm.storage.Feed;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FeedDetailFragment extends Fragment {

	public static final String TAG = FeedDetailFragment.class.getSimpleName();
	
	private Feed mFeed;
	
	public static FeedDetailFragment newInstance(Feed feed) {
		FeedDetailFragment instance = new FeedDetailFragment();
		instance.mFeed = feed;
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_feed_detail, container,
				false);
		getFragmentManager()
				.beginTransaction()
				.add(R.id.fragment_container_tracks, TracksFragment.newInstance(mFeed),
						TracksFragment.TAG).commit();
		return view;
	}

	@Override
	public void onDestroyView() {
		Fragment fragment = getFragmentManager().findFragmentByTag(
				TracksFragment.TAG);
		if (fragment != null) {
			getFragmentManager().beginTransaction().remove(fragment).commit();
		}
		super.onDestroyView();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		TextView textVeiwOne = (TextView) view.findViewById(R.id.half_artist_image_text_line_one);
		TextView textVeiwTwo = (TextView) view.findViewById(R.id.half_artist_image_text_line_two);
		ImageView imageView = (ImageView) view.findViewById(R.id.half_artist_image);
		
		textVeiwOne.setText(mFeed.getTitle());
		textVeiwTwo.setText(mFeed.getOwner());
		
		new LoadImageTask(getActivity(), imageView).execute(mFeed);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

}
