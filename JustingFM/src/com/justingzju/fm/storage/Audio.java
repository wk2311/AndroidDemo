package com.justingzju.fm.storage;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class Audio implements Parcelable {

	public static final String _ID = PodDBHelper.KEY_ID;
	public static final String TITLE = "title";
	public static final String AUTHOR = "author";
	public static final String ANNOUNCER = "announcer";
	public static final String DURATION = "duration";
	public static final String LINK = "link";
	public static final String LOCAL_URI = "localURI";
	public static final String PUB_DATE = "pubDate";
	public static final String FEED = "feed";

	private long _id;
	private String title;
	private String author;
	private String announcer;
	private int duration;
	private String link;
	private String localURI;
	private long pubDate;
	private long feed;

	public Audio(Parcel source) {
		this._id = source.readLong();
		this.title = source.readString();
		this.author = source.readString();
		this.announcer = source.readString();
		this.duration = source.readInt();
		this.link = source.readString();
		this.localURI = source.readString();
		this.pubDate = source.readLong();
		this.feed = source.readLong();
	}
	
	public Audio(Cursor cursor) {
		this._id = cursor.getLong(cursor.getColumnIndex(_ID));
		this.title = cursor.getString(cursor.getColumnIndex(TITLE));
		this.author = cursor.getString(cursor.getColumnIndex(AUTHOR));
		this.announcer = cursor.getString(cursor.getColumnIndex(ANNOUNCER));
		this.duration = cursor.getInt(cursor.getColumnIndex(DURATION));
		this.link = cursor.getString(cursor.getColumnIndex(LINK));
		this.localURI = cursor.getString(cursor.getColumnIndex(LOCAL_URI));
		this.pubDate = cursor.getLong(cursor.getColumnIndex(PUB_DATE));
		this.feed = cursor.getLong(cursor.getColumnIndex(FEED));
	}

	public long getId() {
		return _id;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public String getAnnouncer() {
		return announcer;
	}

	public int getDuration() {
		return duration;
	}

	public String getLink() {
		return link;
	}

	public String getLocalURI() {
		return localURI;
	}

	public long getPubDate() {
		return pubDate;
	}
	
	public long getFeed() {
		return feed;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(_id);
		dest.writeString(title);
		dest.writeString(author);
		dest.writeString(announcer);
		dest.writeInt(duration);
		dest.writeString(link);
		dest.writeString(localURI);
		dest.writeLong(pubDate);
		dest.writeLong(feed);
	}

	public static final Parcelable.Creator<Audio> CREATOR = new Creator<Audio>() {

		@Override
		public Audio createFromParcel(Parcel source) {
			return new Audio(source);
		}

		@Override
		public Audio[] newArray(int size) {
			return new Audio[size];
		}

	};

}
