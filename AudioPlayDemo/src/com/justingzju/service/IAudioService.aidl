package com.justingzju.service;

interface IAudioService {
	void play();
	void pause();
	void prev();
	void next();
	boolean isPlaying();
	String getAudioName();
	String getAudioImage();
	int getPosition();
	void setPostion(int position);
}