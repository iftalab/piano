package com.adorgolap.easypiano;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class PianoDB extends SQLiteAssetHelper {
	private static final String DATABASE_NAME = "easypiano.db";
	private static final int DATABASE_VERSION = 1;
	Context context;

	public PianoDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	public Cursor getSongNames() {
		SQLiteDatabase db = getReadableDatabase();
		String q = "SELECT songname, artist FROM SONG ORDER BY _id";
		Cursor c = db.rawQuery(q, null);
		c.moveToFirst();
		db.close();
		return c;
	}

	public String getSongNameAndArtistById(int id) {
		if (id == 0) {
			return "Free Play";
		}
		SQLiteDatabase db = getReadableDatabase();
		String q = "SELECT * FROM SONG where _id = " + id;
		Cursor c = db.rawQuery(q, null);

		if (c.moveToFirst()) {
			String songName = c.getString(c.getColumnIndex("songname"));
			String artist = c.getString(c.getColumnIndex("artist"));
			if(artist == null)
			{
				return songName;
			}
			if (artist.equals("")) {
				return songName;
			}
			return (songName + " by " + artist);
		} else {
			return "Error";
		}
	}

	public Cursor getSongDataById(int id) {
		SQLiteDatabase db = getReadableDatabase();
		String q = "SELECT * FROM SONGDATA where _id = " + id
				+ " ORDER BY sequence";
		Cursor c = db.rawQuery(q, null);
		return c;
	}

	public int insertNewSong(String songName, String artistName) {
		int newId = -1;
		SQLiteDatabase db = getReadableDatabase();
		String q = "SELECT MAX(_id) FROM SONG";
		Cursor c = db.rawQuery(q, null);
		if (c.moveToFirst()) {
			newId = c.getInt(0) + 1;
			ContentValues values = new ContentValues();
			values.put("_id", newId);
			values.put("songname", songName);
			values.put("artist", artistName);
			long x = db.insert("SONG", null, values);
			if (x == -1) {
				return -1;
			}
			return newId;
		}
		return -1;

	}

	public boolean insertOrUpdateSongInfo(int id, int sequence, String notes,
			String word, int offset) {
		ContentValues values = new ContentValues();
		values.put("_id", id);
		values.put("sequence", sequence);
		values.put("note", notes);
		values.put("word", word);
		values.put("offset", offset);
		
		SQLiteDatabase db = getReadableDatabase();
		String q = "SELECT * FROM SONGDATA WHERE _id = "+id+" AND sequence = "+sequence;
		Cursor c = db.rawQuery(q, null);
		if(c.moveToFirst())
		{
			String temp[] = {""+id,""+sequence};
			long x = db.update("SONGDATA", values, "_id = ? AND sequence = ?", temp);
			if (x != -1) {
				return true;
			}
			return false;
		}
		else
		{
			long x = db.insert("SONGDATA", null, values);
			if (x != -1) {
				return true;
			}
			return false;
		}
		
	}

	public String[] getSequenceData(int id, int seq) {
		SQLiteDatabase db = getReadableDatabase();
		String q = "SELECT * FROM SONGDATA WHERE _id = " + id
				+ " AND sequence = " + seq;
		Cursor c = db.rawQuery(q, null);
		if (c.moveToFirst()) {
			String note = c.getString(c.getColumnIndex("note"));
			String word = c.getString(c.getColumnIndex("word"));
			int rate = c.getInt(c.getColumnIndex("offset"));
			String[] x = { note, word, "" + rate };
			return x;
		}
		return null;
	}

	public void deleteSong(long selectedViewId) {
		SQLiteDatabase db = getReadableDatabase();
		String[] temp = {""+selectedViewId};
		//db.delete("SONG", "_id = ?", whereArgs)
	}

	public ArrayList<Integer> getSongLoadbyID(int id) {
		SQLiteDatabase db = getReadableDatabase();
		String q = "SELECT picks FROM PICKUP WHERE _id = " + id;
		Cursor x = db.rawQuery(q, null);
		ArrayList<Integer> tempPicks= new ArrayList<Integer>();
		if(x.moveToFirst())
		{
			do{
				tempPicks.add(x.getInt(x.getColumnIndex("picks")));
			}while(x.moveToNext());
		}
		return tempPicks;
	}
}
