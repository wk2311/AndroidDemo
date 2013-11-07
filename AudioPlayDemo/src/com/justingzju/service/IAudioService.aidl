package com.justingzju.service;

interface IAudioService {
	void play();
	void pause();
	void prev();
	void next();
	boolean isPlaying();
	String getAudioName();
	String getAudioImage();
	String getAuthor();
	int getPosition();
	void setPostion(int position);
}