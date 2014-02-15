package com.justingzju.fm.v4.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.justingzju.fm.R;
import com.justingzju.fm.storage.Feed;
import com.justingzju.fm.v4.fragment.FeedDetailFragment;
import com.justingzju.fm.v4.fragment.FeedsFragment;
import com.justingzju.fm.v4.fragment.FeedsFragment.onFeedSelectListener;

public class HomePage extends FragmentActivity implements onFeedSelectListener, OnBackStackChangedListener {

	private static final String TRANSACTION_FEED_DETAIL = FeedsFragment.TAG + "_" + FeedDetailFragment.TAG;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_page);

		if (getSupportFragmentManager().findFragmentByTag(FeedsFragment.TAG) == null) {
			getSupportFragmentManager()
					.beginTransaction().add(R.id.fragment_container_main, new FeedsFragment(),
					FeedsFragment.TAG).commit();
		}
		
		getSupportFragmentManager().addOnBackStackChangedListener(this);
	}

	@Override
	protected void onDestroy() {
		getSupportFragmentManager().removeOnBackStackChangedListener(this);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.activity_home_page, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			getSupportFragmentManager().popBackStack();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onFeedSelect(Feed feed) {
		Fragment fragment = getSupportFragmentManager().findFragmentByTag(
				FeedDetailFragment.TAG);
		if (fragment == null) {
			fragment = FeedDetailFragment.newInstance(feed);
		}
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.fragment_container_main, fragment,
						FeedDetailFragment.TAG).addToBackStack(TRANSACTION_FEED_DETAIL).commit();
	}

	@Override
	public void onBackStackChanged() {
		int entryCount = getSupportFragmentManager().getBackStackEntryCount();
		if (entryCount == 0) {
			getActionBar().setTitle(R.string.app_name);
			getActionBar().setDisplayHomeAsUpEnabled(false);
			return;
		}
		String lastEntryName = getSupportFragmentManager().getBackStackEntryAt(entryCount-1).getName();
		if (TRANSACTION_FEED_DETAIL.equals(lastEntryName)) {
			getActionBar().setTitle(R.string.title_fragment_feed_detail);
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
}
