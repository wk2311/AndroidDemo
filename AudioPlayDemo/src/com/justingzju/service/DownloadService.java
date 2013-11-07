package com.justingzju.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.justingzju.Audio;
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
					List<ContentValues> list = readJsonStream(connection.getInputStream());
					getContentResolver().insert(Constant.PROVIDER_AUDIO, list.get(0));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		return START_NOT_STICKY;
	}
	
	private List<ContentValues> readJsonStream(InputStream in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
		try {
			return readAudioArray(reader);
		}
		finally {
			reader.close();
		}
	}

	private List<ContentValues> readAudioArray(JsonReader reader) throws IOException {
		List<ContentValues> messages = new ArrayList<ContentValues>();
		
		reader.beginArray();
		while (reader.hasNext()) {
			messages.add(readAudio(reader));
		}
		reader.endArray();
		return messages;
	}

	private ContentValues readAudio(JsonReader reader) throws IOException {
		ContentValues audioContentValues = new ContentValues();
		
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals(Audio.TITLE)) {
				audioContentValues.put(Audio.TITLE, reader.nextString());
			} else if (name.equals(Audio.AUTHOR)) {
				audioContentValues.put(Audio.AUTHOR, reader.nextString());
			} else if (name.equals(Audio.BROADCASTER)) {
				audioContentValues.put(Audio.BROADCASTER, reader.nextString());
			} else if (name.equals(Audio.AUDIO_URL)) {
				audioContentValues.put(Audio.AUDIO_URL, reader.nextString());
			} else if (name.equals(Audio.IMAGE_URL)) {
				audioContentValues.put(Audio.IMAGE_URL, reader.nextString());
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		
		return audioContentValues;
	}

}
