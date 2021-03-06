package com.justingzju.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.justingzju.Audio.*;

public class DBHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "Audio";
	private static final int DATABASE_VERSION = 1;  
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS playlist " +
				"(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				TITLE+" VARCHAR, " +
				AUTHOR+" VARCHAR, " +
				BROADCASTER+" VARCHAR, " +
				AUDIO_URL+" VARCHAR, " +
				IMAGE_URL+" VARCHAR)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		System.out.println(DATABASE_NAME + "version onUpgrade");
	}
	
	public static DBHelper helper;

}
