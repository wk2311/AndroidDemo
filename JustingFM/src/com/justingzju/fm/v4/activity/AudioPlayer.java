package com.justingzju.fm.v4.activity;

import java.util.Locale;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.justingzju.fm.R;
import com.justingzju.fm.service.PlayService;
import com.justingzju.fm.service.PlayService.PlayStub;
import com.justingzju.fm.storage.Audio;
import com.justingzju.fm.v4.fragment.AudioPlayerFragment;
import com.justingzju.fm.v4.fragment.PlaylistFragment;
import com.justingzju.fm.v4.fragment.SummaryFragment;
import com.justingzju.fm.v4.fragment.TracksFragment;
import com.viewpagerindicator.UnderlinePageIndicator;

public class AudioPlayer extends FragmentActivity implements OnClickListener {

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

	private ImageButton mPlay;

	private ImageButton mPrev;

	private ImageButton mNext;

	private SeekBar mSeekBar;

	private PlayStub mPlayStub;

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

		UnderlinePageIndicator indicator = (UnderlinePageIndicator) findViewById(R.id.underline_page_indicator);
		indicator
				.setViewPager(mViewPager, PlayerPagerAdapter.PAGER_AUDIOPLAYER);

		mSeekBar = (SeekBar) findViewById(android.R.id.progress);

		mPlay = (ImageButton) findViewById(R.id.audio_player_play);
		mPrev = (ImageButton) findViewById(R.id.audio_player_prev);
		mNext = (ImageButton) findViewById(R.id.audio_player_next);

		mPlay.setOnClickListener(this);
		mPrev.setOnClickListener(this);
		mNext.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		bindService(new Intent(this, PlayService.class), mConnection,
				Service.BIND_AUTO_CREATE);

		IntentFilter filter = new IntentFilter();
		filter.addAction(PlayService.BROADCAST_PLAYSTATE_CHANGED);
		filter.addAction(PlayService.BROADCAST_AUDIO_CHANGED);
		registerReceiver(mReceiver, filter);
	}

	@Override
	protected void onStop() {
		unregisterReceiver(mReceiver);

		unbindService(mConnection);
		super.onStop();
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mPlayStub = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mPlayStub = (PlayStub) service;
			mSeekBar.setProgress(mPlayStub.getCurrentPosition() / 1000);
			mSeekBar.setSecondaryProgress(mSeekBar.getMax() * mPlayStub.getBufferingPercent() / 100);
		}
	};

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(PlayService.BROADCAST_PLAYSTATE_CHANGED)) {
				String playstate = intent
						.getStringExtra(PlayService.EXTRA_PLAYSTATE);
				onPlayStateChange(playstate);
			} else if (action.equals(PlayService.BROADCAST_AUDIO_CHANGED)) {
				Audio audio = intent
						.getParcelableExtra(PlayService.EXTRA_AUDIO);
				onAudioChange(audio);
			}
		}

	};

	private void onPlayStateChange(String playstate) {
		if (playstate.equals(PlayService.PLAYSTATE_PLAYING)) {
			mSeekHandler.post(mSeekUpdeteTask);
			mPlay.setImageResource(R.drawable.apollo_holo_light_pause);
		} else {
			mSeekHandler.removeCallbacks(mSeekUpdeteTask);
			mPlay.setImageResource(R.drawable.apollo_holo_light_play);
		}
	}

	private final Handler mSeekHandler = new Handler();

	private final Runnable mSeekUpdeteTask = new Runnable() {

		private static final long UPDATE_INTERVAL = 500;

		@Override
		public void run() {
			mSeekBar.setProgress(mPlayStub.getCurrentPosition() / 1000);
			mSeekBar.setSecondaryProgress(mSeekBar.getMax() * mPlayStub.getBufferingPercent() / 100);
			mSeekHandler.postDelayed(this, UPDATE_INTERVAL);
		}
	};

	private void onAudioChange(Audio audio) {
		mSeekBar.setMax(audio.getDuration());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.audio_player, menu);
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

	@Override
	public void onClick(View v) {
		try {
			if (v.equals(mPlay)) {
				mPlayStub.playOrPause();
			} else if (v.equals(mPrev)) {
				mPlayStub.prev();
			} else if (v.equals(mNext)) {
				mPlayStub.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
				return getString(R.string.title_pager_audioplayer).toUpperCase(
						l);
			case PAGER_SUMMARY:
				return getString(R.string.title_pager_audiosummary)
						.toUpperCase(l);
			}
			return null;
		}
	}

}
