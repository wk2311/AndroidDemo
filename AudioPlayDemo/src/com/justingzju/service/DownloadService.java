package com.justingzju.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.justingzju.Constant;
import com.justingzju.database.AudioProvider;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.JsonReader;

public class DownloadService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		new Thread() {
			@Override
			public void run() {
				try {
					URL url = new URL("http://justingzju.sinaapp.com/SearchAudio.php?listName=playlist");
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					List<Audio> list = readJsonStream(connection.getInputStream());
					getContentResolver().insert(Constant.PROVIDER_AUDIO, list.get(0).getContentValues());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		return START_NOT_STICKY;
	}
	
	private List<Audio> readJsonStream(InputStream in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		try {
			return readAudioArray(reader);
		}
		finally {
			reader.close();
		}
	}

	private List<Audio> readAudioArray(JsonReader reader) throws IOException {
		List<Audio> messages = new ArrayList<Audio>();
		
		reader.beginArray();
		while (reader.hasNext()) {
			messages.add(readAudio(reader));
		}
		reader.endArray();
		return messages;
	}

	private Audio readAudio(JsonReader reader) throws IOException {
		String audioTitle = null;
		String audioURL = null;
		
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("Title")) {
				audioTitle = reader.nextString();
			} else if (name.equals("AudioURL")) {
				audioURL = reader.nextString();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return new Audio(audioTitle, audioURL);
	}

	private class Audio {
		ContentValues mContentValues;
		
		public Audio(String name, String url) {
			mContentValues = new ContentValues();
			mContentValues.put("name", name);
			mContentValues.put("url", url);
		}
		
		public ContentValues getContentValues() {
			return mContentValues;
		}
	}

}
