package org.tarrio.cheepcheep;

import android.app.Activity;

public class Constants {

	// Activity request codes
	public static final int CHANGE_SETTINGS = 1;
	public static final int SHOW_USER = 2;
	public static final int SHOW_TWEET = 3;
	public static final int DO_OAUTH = 4;
	
	// Activity result codes
	public static final int RESULT_UPDATE = Activity.RESULT_FIRST_USER;
	public static final int RESULT_RELOAD = Activity.RESULT_FIRST_USER + 1;

	// Activity actions
	public static final String ACTION_OAUTH = "org.tarrio.cheepcheep.ACTION_OAUTH";
	
	// Maximum number of tweets in the home timeline screen
	public static final int HOME_MAX_TWEETS = 50;
	// Maximum number of tweets in a user's timeline screen
	public static final int USER_MAX_TWEETS = 40;
	
}
