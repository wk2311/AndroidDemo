package com.justingzju.fm.storage;

import com.justingzju.util.LogUtil;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class AudioProvider extends ContentProvider {

	public static final Uri CONTENT_URI = Uri.parse("content://" + AudioProvider.class.getName());
	
	private static final LogUtil mLog = new LogUtil(AudioProvider.class.getSimpleName(), true);

	private Context mContext;
	private AudioDBHelper mDBHelper;

	@Override
	public boolean onCreate() {
		mLog.v("onCreate");
		mContext = getContext();
		mDBHelper = new AudioDBHelper(mContext);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		mLog.v("query");
		final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(AudioDBHelper.TABLE_NAME);
		final SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		if (cursor != null) {
			cursor.setNotificationUri(mContext.getContentResolver(), CONTENT_URI);
		}
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		mLog.v("insert");
		final SQLiteDatabase db = mDBHelper.getReadableDatabase();
		long id = db.insert(AudioDBHelper.TABLE_NAME, null, values);
		notifyChange();
		return ContentUris.withAppendedId(uri, id);
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		mLog.v("bulkInsert");
		final SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.beginTransaction();
		int i;
		try {
			for(i=0; i<values.length; i++) {
				db.insert(AudioDBHelper.TABLE_NAME, null, values[i]);
				db.yieldIfContendedSafely();
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		notifyChange();
		return i;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		mLog.v("delete");
		final SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int count = db.delete(AudioDBHelper.TABLE_NAME, selection, selectionArgs);
		notifyChange();
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		mLog.v("update");
		final SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int count = db.update(AudioDBHelper.TABLE_NAME, values, selection, selectionArgs);
		notifyChange();
		return count;
	}

	private void notifyChange() {
		mContext.getContentResolver().notifyChange(CONTENT_URI, null);
	}

}
