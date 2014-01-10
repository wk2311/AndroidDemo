package com.justingzju.fm.activity;

import com.justingzju.fm.R;
import com.justingzju.fm.R.id;
import com.justingzju.fm.R.layout;
import com.justingzju.fm.R.menu;
import com.justingzju.fm.fragment.FeedDetailFragment;
import com.justingzju.fm.fragment.FeedsFragment;
import com.justingzju.fm.fragment.FeedsFragment.onFeedSelectListener;
import com.justingzju.fm.storage.Feed;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.Menu;

public class HomePage extends Activity implements onFeedSelectListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_page);

		if (getFragmentManager().findFragmentByTag(FeedsFragment.TAG) == null) {
			getFragmentManager()
					.beginTransaction().add(R.id.fragment_container_main, new FeedsFragment(),
					FeedsFragment.TAG).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_home_page, menu);
		return true;
	}

	@Override
	public void onFeedSelect(Feed feed) {
		Fragment fragment = getFragmentManager().findFragmentByTag(
				FeedDetailFragment.TAG);
		if (fragment == null) {
			fragment = FeedDetailFragment.newInstance(feed);
		}
		getFragmentManager()
				.beginTransaction()
				.replace(R.id.fragment_container_main, fragment,
						FeedDetailFragment.TAG).addToBackStack(null).commit();
	}
}
