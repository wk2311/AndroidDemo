package com.justingzju.database;

import com.justingzju.Constant;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class AudioProvider extends ContentProvider {

	private static final int AUDIO = 1;
	private static final int AUDIO_ID = 2;
	private static final int AUDIO_NAME = 3;
	private static final int AUDIO_NAME_ID = 4;
	private static final int AUDIO_URL = 5;
	private static final int AUDIO_URL_ID = 6;
	private static final UriMatcher sURLMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		sURLMatcher.addURI(AudioProvider.class.getName(), "playlist", AUDIO);
		sURLMatcher.addURI(AudioProvider.class.getName(), "playlist/#",
				AUDIO_ID);
		sURLMatcher.addURI(AudioProvider.class.getName(), "playlist/#/name",
				AUDIO_NAME);
		sURLMatcher.addURI(AudioProvider.class.getName(), "playlist/#/name/#",
				AUDIO_NAME_ID);
		sURLMatcher.addURI(AudioProvider.class.getName(), "playlist/#/url",
				AUDIO_URL);
		sURLMatcher.addURI(AudioProvider.class.getName(), "playlist/#/url/#",
				AUDIO_URL_ID);
	}

	private DBHelper helper;

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		helper = new DBHelper(getContext());
		return false;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		switch (sURLMatcher.match(uri)) {
		case AUDIO_ID: {
			return "vnd.android.cursor.item/audio";
		}
		case AUDIO_NAME_ID: {
			return "vnd.android.cursor.item/name";
		}
		case AUDIO_URL_ID: {
			return "vnd.android.cursor.item/url";
		}
		default:
			break;
		}
		return null;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		StringBuilder projectionBuilder = new StringBuilder();
		if (projection == null || projection.length == 0) {
			projectionBuilder.append("*");
		} else {
			projectionBuilder.append(projection[0]);
			for (int i = 1; i < projection.length; i++)
				projectionBuilder.append(", ").append(projection[i]);
		}
		StringBuilder queryBuilder = new StringBuilder().append("SELECT ")
				.append(projectionBuilder.toString()).append(" FROM playlist");
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery(queryBuilder.toString(), selectionArgs);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = helper.getWritableDatabase();
		Long id = db.insert("playlist", null, values);
		notifyChange();
		return ContentUris.withAppendedId(uri, id);
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();
		try {
			for(int i=0; i<values.length; i++) {
				db.insert("playlist", null, values[i]);
				db.yieldIfContendedSafely();
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		notifyChange();
		return values.length;
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = helper.getWritableDatabase();
		int count = db.delete("playlist", arg1, arg2);
		notifyChange();
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		notifyChange();
		return 0;
	}
	
	private void notifyChange() {
		getContext().getContentResolver().notifyChange(Constant.PROVIDER_AUDIO, null);
	}

}
