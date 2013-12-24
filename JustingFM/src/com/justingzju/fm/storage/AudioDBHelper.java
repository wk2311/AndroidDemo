package com.justingzju.fm.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AudioDBHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME = "Audio.db";
	public static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME = "AudioList";

	public AudioDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				Audio._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				Audio.TITLE + " VARCHAR, " +
				Audio.AUTHOR + " VARCHAR, " +
				Audio.BROADCASTER + " VARCHAR, " +
				Audio.DURATION + " VARCHAR, " +
				Audio.AUDIO_URL + " VARCHAR, " +
				Audio.AUDIO_URI + " VARCHAR, " + 
				Audio.PUB_DATE + " VARCHAR" + ")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE " + TABLE_NAME);
		onCreate(db);
	}

}
