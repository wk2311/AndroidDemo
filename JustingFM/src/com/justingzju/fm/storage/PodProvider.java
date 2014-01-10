package com.justingzju.fm.storage;

import com.justingzju.util.LogUtil;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class PodProvider extends ContentProvider {

	private static final LogUtil mLog = new LogUtil(
			PodProvider.class.getSimpleName(), true);

	public static final String AUTHORITY = PodProvider.class.getName();

	public static final Uri CONTENT_URI_FEEDS = Uri.parse("content://"
			+ AUTHORITY + "/" + PodDBHelper.TABLE_NAME_FEEDS);
	public static final Uri CONTENT_URI_AUDIOS = Uri.parse("content://"
			+ AUTHORITY + "/" + PodDBHelper.TABLE_NAME_AUDIOS);

	private static final int MATCH_FEEDS = 0x00;
	private static final int MATCH_FEEDS_ID = 0x01;
	private static final int MATCH_AUDIOS = 0x10;
	private static final int MATCH_AUDIOS_ID = 0x11;
	private static final UriMatcher sUriMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sUriMatcher
				.addURI(AUTHORITY, PodDBHelper.TABLE_NAME_FEEDS, MATCH_FEEDS);
		sUriMatcher.addURI(AUTHORITY, PodDBHelper.TABLE_NAME_FEEDS + "/#",
				MATCH_FEEDS_ID);
		sUriMatcher.addURI(AUTHORITY, PodDBHelper.TABLE_NAME_AUDIOS,
				MATCH_AUDIOS);
		sUriMatcher.addURI(AUTHORITY, PodDBHelper.TABLE_NAME_AUDIOS + "/#",
				MATCH_AUDIOS_ID);
	}

	private Context mContext;
	private PodDBHelper mDBHelper;

	@Override
	public boolean onCreate() {
		mLog.v("onCreate");
		mContext = getContext();
		mDBHelper = new PodDBHelper(mContext);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		mLog.v("query");
		final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		Uri notificationUri;
		switch (sUriMatcher.match(uri)) {
		case MATCH_FEEDS_ID:
			qb.appendWhere(Feed._ID + "=" + ContentUris.parseId(uri));
		case MATCH_FEEDS:
			qb.setTables(PodDBHelper.TABLE_NAME_FEEDS);
			notificationUri = CONTENT_URI_FEEDS;
			break;
		case MATCH_AUDIOS_ID:
			qb.appendWhere(Audio._ID + "=" + ContentUris.parseId(uri));
		case MATCH_AUDIOS:
			qb.setTables(PodDBHelper.TABLE_NAME_AUDIOS);
			notificationUri = CONTENT_URI_AUDIOS;
			break;
		default:
			throw new IllegalArgumentException("Unkown URI: " + uri);
		}
		final SQLiteDatabase db = mDBHelper.getReadableDatabase();
		Cursor cursor = qb.query(db, projection, selection, selectionArgs,
				null, null, sortOrder);
		if (cursor != null) {
			cursor.setNotificationUri(mContext.getContentResolver(),
					notificationUri);
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
		long id = db.insert(getTable(uri), null, values);
		notifyChange(uri);
		return ContentUris.withAppendedId(uri, id);
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		mLog.v("bulkInsert");
		final SQLiteDatabase db = mDBHelper.getWritableDatabase();
		final String table = getTable(uri);
		db.beginTransaction();
		int i;
		try {
			for (i = 0; i < values.length; i++) {
				db.insert(table, null, values[i]);
				db.yieldIfContendedSafely();
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		notifyChange(uri);
		return i;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		mLog.v("delete");
		final SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int count = db.delete(getTable(uri), selection, selectionArgs);
		notifyChange(uri);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		mLog.v("update");
		final SQLiteDatabase db = mDBHelper.getWritableDatabase();
		int count = db.update(getTable(uri), values, selection, selectionArgs);
		notifyChange(uri);
		return count;
	}

	private String getTable(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case MATCH_FEEDS:
			return PodDBHelper.TABLE_NAME_FEEDS;
		case MATCH_AUDIOS:
			return PodDBHelper.TABLE_NAME_AUDIOS;
		default:
			throw new IllegalArgumentException("Unkown URI: " + uri);
		}
	}

	private void notifyChange(Uri uri) {
		mContext.getContentResolver().notifyChange(uri, null);
	}

}
