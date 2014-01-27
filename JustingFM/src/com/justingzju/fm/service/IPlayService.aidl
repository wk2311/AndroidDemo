package com.justingzju.fm.service;

interface IPlayService {
	void playOrPause();
	void prev();
	void next();
	boolean isPlaying();
}