package com.justingzju.fm.storage;

import android.content.ContentValues;
import android.database.Cursor;

public class Feed {
	
	public static final String _ID = PodDBHelper.KEY_ID;
	public static final String TITLE = "title";
	public static final String OWNER = "owner";
	public static final String LINK = "link";
	public static final String IMAGE_LINK = "image_link";
	
	private long _id;
	private String title;
	private String owner;
	private String link;
	private String imageLink;
	
	public Feed(String title, String owner, String link, String imageLink) {
		this.title = title;
		this.owner = owner;
		this.link = link;
		this.imageLink = imageLink;
	}

	public Feed(Cursor cursor) {
		this._id = cursor.getLong(cursor.getColumnIndex(_ID));
		this.title = cursor.getString(cursor.getColumnIndex(TITLE));
		this.owner = cursor.getString(cursor.getColumnIndex(OWNER));
		this.link = cursor.getString(cursor.getColumnIndex(LINK));
		this.imageLink = cursor.getString(cursor.getColumnIndex(IMAGE_LINK));
	}
	
	public long getId() {
		return _id;
	}

	public String getTitle() {
		return title;
	}

	public String getOwner() {
		return owner;
	}

	public String getLink() {
		return link;
	}

	public String getImageLink() {
		return imageLink;
	}

	public String getDestination() {
		return getDestinationFromLink(link);
	}

	public ContentValues getContentValues() {
		ContentValues values = new ContentValues();
		values.put(TITLE, getTitle());
		values.put(OWNER, getOwner());
		values.put(LINK, getLink());
		values.put(IMAGE_LINK, getImageLink());
		return values;
	}

	public static String getDestinationFromLink(String link) {
		String[] segments = link.split("/");
		return segments[segments.length-1];
	}

}
