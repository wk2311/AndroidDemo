package com.justingzju.fm.storage.test;

import com.justingzju.fm.storage.Audio;
import com.justingzju.fm.storage.AudioProvider;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.ProviderTestCase2;

public class AudioProviderTest extends ProviderTestCase2<AudioProvider> {

	private static final int INTT_ROWS = 10;
	private static final int INIT_DURATION = 300;
	private static final long INIT_PUBDATE = System.currentTimeMillis();

	public AudioProviderTest() {
		super(AudioProvider.class, AudioProvider.CONTENT_URI.getAuthority());
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		ContentValues[] valuesArray = new ContentValues[INTT_ROWS];
		for (int i = 0; i < valuesArray.length; i++) {
			ContentValues contentValues = new ContentValues();
			contentValues.put(Audio.TITLE, Audio.TITLE+i);
			contentValues.put(Audio.AUTHOR, Audio.AUTHOR+i);
			contentValues.put(Audio.BROADCASTER, Audio.BROADCASTER+i);
			contentValues.put(Audio.DURATION, INIT_DURATION+i);
			contentValues.put(Audio.AUDIO_URL, Audio.AUDIO_URL+i);
			contentValues.put(Audio.AUDIO_URI, Audio.AUDIO_URI+i);
			contentValues.put(Audio.PUB_DATE, INIT_PUBDATE+i);
			valuesArray[i] = contentValues;
		}
		getMockContentResolver().bulkInsert(AudioProvider.CONTENT_URI, valuesArray);
	}

	public void testQuery() {
		for (int i = 0; i < INTT_ROWS; i++) {
			queryTestWithColumn(Audio.TITLE, i);
			queryTestWithColumn(Audio.AUTHOR, i);
			queryTestWithColumn(Audio.BROADCASTER, i);
			queryTestWithColumn(Audio.DURATION, i);
			queryTestWithColumn(Audio.AUDIO_URL, i);
			queryTestWithColumn(Audio.AUDIO_URI, i);
			queryTestWithColumn(Audio.PUB_DATE, i);
		}
	}
	
	private void queryTestWithColumn(String column, int index) {
		String[] projection = new String[]{};
		String selection = column + "=?";
		String[] selectionArgs;
		if (column.equals(Audio.DURATION)) {
			selectionArgs = new String[]{String.valueOf(INIT_DURATION + index)};
		} else if (column.equals(Audio.PUB_DATE)) {
			selectionArgs = new String[]{String.valueOf(INIT_PUBDATE + index)};
		} else {
			selectionArgs = new String[]{column + index};
		}
		Cursor cursor = getMockContentResolver().query(AudioProvider.CONTENT_URI, projection, selection, selectionArgs, null);
		
		assertNotNull(cursor);
		assertEquals(1, cursor.getCount());
		
		if (!cursor.isClosed()) {
			cursor.close();
		}
	}
	
	public void testInsert() {
		insertTestWithColumn(Audio.TITLE);
		insertTestWithColumn(Audio.AUTHOR);
		insertTestWithColumn(Audio.BROADCASTER);
		insertTestWithColumn(Audio.DURATION);
		insertTestWithColumn(Audio.AUDIO_URL);
		insertTestWithColumn(Audio.AUDIO_URI);
		insertTestWithColumn(Audio.PUB_DATE);
	}
	
	private void insertTestWithColumn(String column) {
		ContentValues contentValues = new ContentValues();
		if (column.equals(Audio.DURATION)) {
			contentValues.put(column, INIT_DURATION+(INTT_ROWS+1));
		} else if (column.equals(Audio.PUB_DATE)) {
			contentValues.put(column, INIT_PUBDATE+(INTT_ROWS+1));
		} else {
			contentValues.put(column, column+(INTT_ROWS+1));
		}
		getMockContentResolver().insert(AudioProvider.CONTENT_URI, contentValues);
		
		queryTestWithColumn(column, INTT_ROWS+1);
	}
	
	public void testDelete() {
		deleteTestWithColumn(Audio.TITLE);
		deleteTestWithColumn(Audio.AUTHOR);
		deleteTestWithColumn(Audio.BROADCASTER);
		deleteTestWithColumn(Audio.DURATION);
		deleteTestWithColumn(Audio.AUDIO_URL);
		deleteTestWithColumn(Audio.AUDIO_URI);
		deleteTestWithColumn(Audio.PUB_DATE);
	}
	
	private void deleteTestWithColumn(String column) {
		String selection = column + "=?";
		String[] selectionArgs;
		if (column.equals(Audio.DURATION)) {
			selectionArgs = new String[]{String.valueOf(INIT_DURATION + 1)};
		} else if (column.equals(Audio.PUB_DATE)) {
			selectionArgs = new String[]{String.valueOf(INIT_PUBDATE + 1)};
		} else {
			selectionArgs = new String[]{column + 1};
		}
		
		getMockContentResolver().delete(AudioProvider.CONTENT_URI, selection, selectionArgs);
		
		String[] projection = new String[]{};

		Cursor cursor = getMockContentResolver().query(AudioProvider.CONTENT_URI, projection, selection, selectionArgs, null);
		
		assertNotNull(cursor);
		assertEquals(0, cursor.getCount());
		
		if (!cursor.isClosed()) {
			cursor.close();
		}
	}
	
	public void testUpdate() {
		updateTestWithColumn(Audio.TITLE);
		updateTestWithColumn(Audio.AUTHOR);
		updateTestWithColumn(Audio.BROADCASTER);
		updateTestWithColumn(Audio.DURATION);
		updateTestWithColumn(Audio.AUDIO_URL);
		updateTestWithColumn(Audio.AUDIO_URI);
		updateTestWithColumn(Audio.PUB_DATE);
	}
	
	private void updateTestWithColumn(String column) {
		String selection = Audio._ID + "=?";
		String oldColumnValue;
		if (column.equals(Audio.DURATION)) {
			oldColumnValue = String.valueOf(INIT_DURATION + 1);
		} else if (column.equals(Audio.PUB_DATE)) {
			oldColumnValue = String.valueOf(INIT_PUBDATE + 1);
		} else {
			oldColumnValue = column + 1;
		}
		String[] selectionArgs = new String[]{String.valueOf(getID(column, oldColumnValue))};
		
		ContentValues contentValues = new ContentValues();
		if (column.equals(Audio.DURATION)) {
			contentValues.put(column, INIT_DURATION+(INTT_ROWS+1));
		} else if (column.equals(Audio.PUB_DATE)) {
			contentValues.put(column, INIT_PUBDATE+(INTT_ROWS+1));
		} else {
			contentValues.put(column, column+(INTT_ROWS+1));
		}
		getMockContentResolver().update(AudioProvider.CONTENT_URI, contentValues, selection, selectionArgs);
		
		String[] projection = new String[]{column};

		Cursor cursor = getMockContentResolver().query(AudioProvider.CONTENT_URI, projection, selection, selectionArgs, null);
		
		assertNotNull(cursor);
		assertEquals(1, cursor.getCount());
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(column);
		assertEquals(0, columnIndex);
		if (column.equals(Audio.DURATION)) {
			assertEquals(INIT_DURATION+(INTT_ROWS+1), cursor.getLong(columnIndex));
		} else if (column.equals(Audio.PUB_DATE)) {
			assertEquals(INIT_PUBDATE+(INTT_ROWS+1), cursor.getLong(columnIndex));
		} else {
			assertEquals(column+(INTT_ROWS+1), cursor.getString(columnIndex));
		}
		
		if (!cursor.isClosed()) {
			cursor.close();
		}
	}
	
	private int getID(String column, String value) {
		String selection = column + "=?";
		String[] selectionArgs = new String[]{value};
		String[] projection = new String[]{Audio._ID};
		Cursor cursor = getMockContentResolver().query(AudioProvider.CONTENT_URI, projection, selection, selectionArgs, null);
		assertNotNull(cursor);
		assertEquals(1, cursor.getCount());
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(Audio._ID);
		assertEquals(0, columnIndex);
		
		int id = cursor.getInt(columnIndex);
		if (!cursor.isClosed()) {
			cursor.close();
		}
		return id;
	}

}
