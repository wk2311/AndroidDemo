package com.andrew.apolloMod.ui.fragments.list;

import com.justingzju.audioplay.AudioClient;
import com.justingzju.audioplay.R;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

public class TrackAdapter extends SimpleCursorAdapter {

	public TrackAdapter(Context context, int layout, Cursor c, String[] from,
			int[] to, int flags) {
		super(context, layout, c, from, to, flags);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final View view = super.getView(position, convertView, parent);
		ImageView peekOne = (ImageView) view.findViewById(R.id.peak_one);
		ImageView peekTwo = (ImageView) view.findViewById(R.id.peak_two);
		try {
			if(AudioClient.getService().getPosition() == position) {
				peekOne.setImageResource(R.anim.peak_meter_1);
				peekTwo.setImageResource(R.anim.peak_meter_2);
				if(AudioClient.getService().isPlaying()) {
					((AnimationDrawable)peekOne.getDrawable()).start();
					((AnimationDrawable)peekTwo.getDrawable()).start();
				} else {
					((AnimationDrawable)peekOne.getDrawable()).stop();
					((AnimationDrawable)peekTwo.getDrawable()).stop();
				}
			} else {
				peekOne.setImageResource(0);
				peekTwo.setImageResource(0);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return view;
	}

}
