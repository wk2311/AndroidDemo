package com.justingzju.fm.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Feed implements Parcelable {
	
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
	
	public Feed(Parcel source) {
		this._id = source.readLong();
		this.title = source.readString();
		this.owner = source.readString();
		this.link = source.readString();
		this.imageLink = source.readString();
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(_id);
		dest.writeString(title);
		dest.writeString(owner);
		dest.writeString(link);
		dest.writeString(imageLink);
	}
	
	public static final Parcelable.Creator<Feed> CREATOR = new Creator<Feed>() {

		@Override
		public Feed createFromParcel(Parcel source) {
			return new Feed(source);
		}

		@Override
		public Feed[] newArray(int size) {
			return new Feed[size];
		}

	};

}
