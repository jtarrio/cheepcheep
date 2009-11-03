package org.tarrio.cheepcheep.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.tarrio.cheepcheep.model.Tweet;
import org.tarrio.cheepcheep.service.TwitterStatusSaverService;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class TwitterStatusSaverServiceImpl implements TwitterStatusSaverService {

	private static final String DB_NAME = "TwitterStatuses.db";
	private static final int DB_VERSION = 1;

	private TwitterDbHelper helper;

	private static class TwitterDbHelper extends SQLiteOpenHelper {

		public TwitterDbHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE statuses ("
					+ "id INTEGER PRIMARY KEY, date INTEGER, "
					+ "screenName TEXT, text TEXT, inReplyToId INTEGER, "
					+ "inReplyToScreenName TEXT)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS statuses");
			onCreate(db);
		}
	}

	public TwitterStatusSaverServiceImpl(Context context) {
		this.helper = new TwitterDbHelper(context);
	}

	@Override
	public List<Tweet> loadTimeline() {
		try {
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor cursor = db.query("statuses", null, null, null, null, null,
					"id DESC");
			List<Tweet> tweets = new ArrayList<Tweet>();
			while (cursor.moveToNext()) {
				Tweet tweet = new Tweet();
				tweet.setId(cursor.getLong(cursor.getColumnIndex("id")));
				tweet.setDateTime(new Date(cursor.getLong(cursor
						.getColumnIndex("date"))));
				tweet.setScreenName(cursor.getString(cursor
						.getColumnIndex("screenName")));
				tweet.setText(cursor.getString(cursor.getColumnIndex("text")));
				tweet.setInReplyToId(cursor.getLong(cursor
						.getColumnIndex("inReplyToId")));
				tweet.setInReplyToScreenName(cursor.getString(cursor
						.getColumnIndex("inReplyToScreenName")));
				tweets.add(tweet);
			}
			cursor.close();
			return tweets;
		} catch (SQLiteException e) {
			return new ArrayList<Tweet>();
		}
	}

	@Override
	public void appendToTimeline(List<Tweet> tweets, long minId) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();
		try {
			for (Tweet tweet : tweets) {
				saveTweet(tweet, db);
			}
			if (minId > 0) {
				cropTweetsUnderId(minId, db);
			}
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			// we deal with it in the main "try" block
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public void clear() {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.beginTransaction();
		db.delete("statuses", null, null);
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	private void cropTweetsUnderId(long minId, SQLiteDatabase db) {
		db.delete("statuses", "id < ?", new String[] { Long.toString(minId) });
	}

	private void saveTweet(Tweet tweet, SQLiteDatabase db) throws SQLException {
		ContentValues values = new ContentValues();
		values.put("id", tweet.getId());
		values.put("date", tweet.getDateTime().getTime());
		values.put("screenName", tweet.getScreenName());
		values.put("text", tweet.getText());
		values.put("inReplyToId", tweet.getInReplyToId());
		values.put("inReplyToScreenName", tweet.getInReplyToScreenName());
		if (-1 == db.insert("statuses", "text", values)) {
			throw new SQLException("Could not insert a tweet in the DB.");
		}
	}

}
