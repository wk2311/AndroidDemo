package com.justingzju.fm.v4.activity;

import java.util.Locale;

import com.justingzju.fm.R;
import com.justingzju.fm.R.id;
import com.justingzju.fm.R.layout;
import com.justingzju.fm.R.menu;
import com.justingzju.fm.R.string;
import com.justingzju.fm.v4.fragment.AudioPlayerFragment;
import com.justingzju.fm.v4.fragment.PlaylistFragment;
import com.justingzju.fm.v4.fragment.SummaryFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AudioPlayer extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	PlayerPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_player);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new PlayerPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setCurrentItem(PlayerPagerAdapter.PAGER_AUDIOPLAYER);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.audio_player, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class PlayerPagerAdapter extends FragmentPagerAdapter {

		private static final int PAGER_PLAYLIST = 0;
		private static final int PAGER_AUDIOPLAYER = 1;
		private static final int PAGER_SUMMARY = 2;

		public PlayerPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			switch (position) {
			case PAGER_PLAYLIST:
				return new PlaylistFragment();
			case PAGER_AUDIOPLAYER:
				return new AudioPlayerFragment();
			case PAGER_SUMMARY:
				return new SummaryFragment();
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case PAGER_PLAYLIST:
				return getString(R.string.title_pager_playlist).toUpperCase(l);
			case PAGER_AUDIOPLAYER:
				return getString(R.string.title_pager_audioplayer).toUpperCase(l);
			case PAGER_SUMMARY:
				return getString(R.string.title_pager_audiosummary).toUpperCase(l);
			}
			return null;
		}
	}

}
