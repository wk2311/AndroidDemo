package com.justingzju.fm.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class PodDBHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "Podcast.db";
	public static final int DATABASE_VERSION = 1;

	public static final String TABLE_NAME_FEEDS = "Feeds";
	public static final String TABLE_NAME_AUDIOS = "Audios";

	static final String KEY_ID = "_id";
	private static final String TABLE_PRIMARY_KEY = KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,";

	private static final String CREATE_TABLE_FEEDS = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME_FEEDS
			+ " ("
			+ TABLE_PRIMARY_KEY
			+ Feed.TITLE
			+ " TEXT,"
			+ Feed.OWNER
			+ " TEXT,"
			+ Feed.LINK
			+ " TEXT,"
			+ Feed.IMAGE_LINK + " TEXT" + ")";

	private static final String CREATE_TABLE_AUDIOS = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME_AUDIOS
			+ " ("
			+ TABLE_PRIMARY_KEY
			+ Audio.TITLE
			+ " TEXT, "
			+ Audio.AUTHOR
			+ " TEXT, "
			+ Audio.ANNOUNCER
			+ " TEXT, "
			+ Audio.DURATION
			+ " INTEGER, "
			+ Audio.LINK
			+ " TEXT, "
			+ Audio.LOCAL_URI
			+ " TEXT, "
			+ Audio.PUB_DATE
			+ " LONG," + Audio.FEED + " LONG" + ")";

	public PodDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_FEEDS);
		db.execSQL(CREATE_TABLE_AUDIOS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE " + TABLE_NAME_FEEDS);
		db.execSQL("DROP TABLE " + TABLE_NAME_AUDIOS);
		onCreate(db);
	}

}
