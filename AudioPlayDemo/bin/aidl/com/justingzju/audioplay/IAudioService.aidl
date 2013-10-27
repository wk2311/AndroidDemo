package com.justingzju.audioplay;

interface IAudioService {
	void load(String url);
	void play();
	void pause();
	void prev();
	void next();
	boolean isPlaying();
}