package com.justingzju.fm.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

import com.justingzju.fm.R;
import com.justingzju.fm.storage.Feed;
import com.justingzju.util.ImageUtil;

public class FeedAdapter extends SimpleCursorAdapter {

	public FeedAdapter(Context context, int layout, Cursor c, String[] from,
			int[] to, int flags) {
		super(context, layout, c, from, to, flags);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		
		Cursor cursor = (Cursor) getItem(position);
		String imageLink = cursor.getString(cursor.getColumnIndex(Feed.IMAGE_LINK));
		ImageView imageView = (ImageView) view.findViewById(R.id.gridview_image);
		ImageUtil.displayImage(imageLink, imageView);
		
		return view;
	}

}
