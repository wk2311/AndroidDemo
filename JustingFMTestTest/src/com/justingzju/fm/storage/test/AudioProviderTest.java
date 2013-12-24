package com.justingzju.fm.storage.test;

import com.justingzju.fm.storage.Audio;
import com.justingzju.fm.storage.AudioProvider;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.ProviderTestCase2;

public class AudioProviderTest extends ProviderTestCase2<AudioProvider> {

	private static final int INTT_ROWS = 10;

	public AudioProviderTest() {
		super(AudioProvider.class, AudioProvider.CONTENT_URI.getAuthority());
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		ContentValues[] valuesArray = new ContentValues[INTT_ROWS];
		for (int i = 0; i < valuesArray.length; i++) {
			ContentValues contentValues = new ContentValues();
			contentValues.put(Audio.TITLE, "audio"+i);
			valuesArray[i] = contentValues;
		}
		getMockContentResolver().bulkInsert(AudioProvider.CONTENT_URI, valuesArray);
	}

	public void testQuery() {
		String[] projection = new String[]{};
		String selection = Audio.TITLE + "=?";
		String[] selectionArgs = new String[]{"audio" + 1};
		Cursor cursor = getMockContentResolver().query(AudioProvider.CONTENT_URI, projection, selection, selectionArgs, null);
		
		assertNotNull(cursor);
		assertEquals(1, cursor.getCount());
		
		if (!cursor.isClosed()) {
			cursor.close();
		}
	}
	
	public void testInsert() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(Audio.TITLE, "audio"+(INTT_ROWS+1));
		getMockContentResolver().insert(AudioProvider.CONTENT_URI, contentValues);
		
		String[] projection = new String[]{};
		String selection = Audio.TITLE + "='audio" + (INTT_ROWS+1) + "'";
		Cursor cursor = getMockContentResolver().query(AudioProvider.CONTENT_URI, projection, selection, null, null);
		
		assertNotNull(cursor);
		assertEquals(1, cursor.getCount());
		
		if (!cursor.isClosed()) {
			cursor.close();
		}
	}
	
	public void testDelete() {
		String selection = Audio.TITLE + "='audio" + 1 + "'";
		
		getMockContentResolver().delete(AudioProvider.CONTENT_URI, selection, null);
		
		String[] projection = new String[]{};

		Cursor cursor = getMockContentResolver().query(AudioProvider.CONTENT_URI, projection, selection, null, null);
		
		assertNotNull(cursor);
		assertEquals(0, cursor.getCount());
		
		if (!cursor.isClosed()) {
			cursor.close();
		}
	}
	
	public void testUpdate() {
		String selection = Audio.TITLE + "='audio" + 1 + "'";
		ContentValues contentValues = new ContentValues();
		contentValues.put(Audio.AUDIO_URL, "http");
		getMockContentResolver().update(AudioProvider.CONTENT_URI, contentValues, selection, null);
		
		String[] projection = new String[]{Audio.AUDIO_URL};

		Cursor cursor = getMockContentResolver().query(AudioProvider.CONTENT_URI, projection, selection, null, null);
		
		assertNotNull(cursor);
		assertEquals(1, cursor.getCount());
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(Audio.AUDIO_URL);
		assertEquals(0, columnIndex);
		assertEquals("http", cursor.getString(columnIndex));
		
		if (!cursor.isClosed()) {
			cursor.close();
		}
	}

}
