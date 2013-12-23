package com.justingzju.fm.service;

import android.os.Parcel;
import android.os.Parcelable;

public class DownloadRequest implements Parcelable {
	
	private final String mSource;
	private final String mDestination;

	public DownloadRequest(String mSource, String mDestination) {
		super();
		this.mSource = mSource;
		this.mDestination = mDestination;
	}

	public DownloadRequest(Parcel source) {
		mSource = source.readString();
		mDestination = source.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mSource);
		dest.writeString(mDestination);
	}
	
	public static final Parcelable.Creator<DownloadRequest> CREATOR = new Creator<DownloadRequest>() {
		
		@Override
		public DownloadRequest[] newArray(int size) {
			return new DownloadRequest[size];
		}
		
		@Override
		public DownloadRequest createFromParcel(Parcel source) {
			return new DownloadRequest(source);
		}
	};

	public String getSource() {
		return mSource;
	}

	public String getDestination() {
		return mDestination;
	}

}
