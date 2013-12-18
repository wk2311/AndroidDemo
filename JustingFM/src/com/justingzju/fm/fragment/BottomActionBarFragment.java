package com.justingzju.fm.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.MediaStore.Audio;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.justingzju.fm.R;
import com.justingzju.fm.widgets.BottomActionBar;

public class BottomActionBarFragment extends Fragment {

	private BottomActionBar mBottomActionBar;
	private ImageButton mPrev;
	private ImageButton mPlay;
	private ImageButton mNext;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.bottom_action_bar, container);
		mBottomActionBar = (BottomActionBar) root
				.findViewById(R.id.bottom_action_bar);

		mBottomActionBar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// mLog.w("bottom_action_bar onClick");
			}
		});

		mBottomActionBar.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// mLog.w("bottom_action_bar onLongClick");
				// getActivity().getContentResolver().delete(Constant.PROVIDER_AUDIO,
				// Audio.TITLE+" LIKE ?", new String[]{"%福利%"});
				return false;
			}
		});

		mPrev = (ImageButton) root
				.findViewById(R.id.bottom_action_bar_previous);
		mPrev.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// System.out.println("bottom_action_bar_previous onClick");
				// try {
				// AudioClient.getService().prev();
				// } catch (RemoteException e) {
				// e.printStackTrace();
				// }
			}
		});

		mPlay = (ImageButton) root.findViewById(R.id.bottom_action_bar_play);
		mPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// mLog.w("bottom_action_bar_play onClick");
				// try {
				// if(AudioClient.getService().isPlaying()) {
				// AudioClient.getService().pause();
				// } else {
				// AudioClient.getService().play();
				// }
				// } catch (RemoteException e) {
				// e.printStackTrace();
				// }
			}
		});

		mNext = (ImageButton) root.findViewById(R.id.bottom_action_bar_next);
		mNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// mLog.w("bottom_action_bar_next onClick");
				// try {
				// AudioClient.getService().next();
				// } catch (RemoteException e) {
				// e.printStackTrace();
				// }
			}
		});

		return root;
	}

}
