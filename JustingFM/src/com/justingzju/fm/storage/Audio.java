package com.justingzju.fm.storage;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class Audio implements Parcelable {

	public static final String _ID = "_id";
	public static final String TITLE = "title";
	public static final String AUTHOR = "author";
	public static final String BROADCASTER = "broadcaster";
	public static final String DURATION = "duration";
	public static final String AUDIO_URL = "audioURL";
	public static final String AUDIO_URI = "audioURI";
	public static final String PUB_DATE = "pubDate";

	private int _id;
	private String title;
	private String author;
	private String broadcaster;
	private int duration;
	private String audioURL;
	private String audioURI;
	private long pubDate;

	public Audio(int _id, String title, String author, String broadcaster,
			int duration, String audioURL, String audioURI, long pubDate) {
		super();
		this._id = _id;
		this.title = title;
		this.author = author;
		this.broadcaster = broadcaster;
		this.duration = duration;
		this.audioURL = audioURL;
		this.audioURI = audioURI;
		this.pubDate = pubDate;
	}

	public Audio(Parcel source) {
		this._id = source.readInt();
		this.title = source.readString();
		this.author = source.readString();
		this.broadcaster = source.readString();
		this.duration = source.readInt();
		this.audioURL = source.readString();
		this.audioURI = source.readString();
		this.pubDate = source.readLong();
	}
	
	public Audio(Cursor cursor) {
		this._id = cursor.getInt(cursor.getColumnIndex(_ID));
		this.title = cursor.getString(cursor.getColumnIndex(TITLE));
		this.author = cursor.getString(cursor.getColumnIndex(AUTHOR));
		this.broadcaster = cursor.getString(cursor.getColumnIndex(BROADCASTER));
		this.duration = cursor.getInt(cursor.getColumnIndex(DURATION));
		this.audioURL = cursor.getString(cursor.getColumnIndex(AUDIO_URL));
		this.audioURI = cursor.getString(cursor.getColumnIndex(AUDIO_URI));
		this.pubDate = cursor.getLong(cursor.getColumnIndex(PUB_DATE));
	}

	public int get_id() {
		return _id;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public String getBroadcaster() {
		return broadcaster;
	}

	public int getDuration() {
		return duration;
	}

	public String getAudioURL() {
		return audioURL;
	}

	public String getAudioURI() {
		return audioURI;
	}

	public long getPubDate() {
		return pubDate;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(_id);
		dest.writeString(title);
		dest.writeString(author);
		dest.writeString(broadcaster);
		dest.writeInt(duration);
		dest.writeString(audioURL);
		dest.writeString(audioURI);
		dest.writeLong(pubDate);
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
