package com.justingzju.fm.service;


public class DownloadRequest {
	
	public interface onCompleteListener {
		public void onDownloadComplete(DownloadRequest request);
	}

	private final String mSource;
	private final String mDestination;
	private onCompleteListener mOnDownloadCompleteListener;

	public DownloadRequest(String mSource, String mDestination) {
		super();
		this.mSource = mSource;
		this.mDestination = mDestination;
	}

	public String getSource() {
		return mSource;
	}

	public String getDestination() {
		return mDestination;
	}

	public void setOnDownloadCompleteListener(onCompleteListener listener) {
		this.mOnDownloadCompleteListener = listener;
	}

	public void onDownloadComplete() {
		mOnDownloadCompleteListener.onDownloadComplete(this);
	}

}
