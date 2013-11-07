package com.justingzju.database;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.android.providers.calendar.SQLiteContentProvider;

public class AudioProvider2 extends SQLiteContentProvider {

	@Override
	protected SQLiteOpenHelper getDatabaseHelper(Context context) {
		return new DBHelper(context);
	}

	@Override
	protected Uri insertInTransaction(Uri uri, ContentValues values) {
		long id = 0;
		id = mDb.insert("playlist", null, values);
		return ContentUris.withAppendedId(uri, id);
	}

	@Override
	protected int updateInTransaction(Uri uri, ContentValues values,
			String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int deleteInTransaction(Uri uri, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void notifyChange() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
			String arg4) {
		// TODO Auto-generated method stub
		return null;
	}

}
