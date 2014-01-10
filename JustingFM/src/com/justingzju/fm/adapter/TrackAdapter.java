package com.justingzju.fm.adapter;


import com.justingzju.fm.R;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
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
		
		ImageView itemImage = (ImageView) view.findViewById(R.id.listview_item_image);
		itemImage.setVisibility(View.GONE);
		return view;
	}

}
