package org.tarrio.cheepcheep.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tarrio.cheepcheep.exceptions.ParseError;
import org.tarrio.cheepcheep.model.Tweet;
import org.tarrio.cheepcheep.model.User;

/**
 * Class for serializing and deserializing tweets and timelines from and to JSON
 * objects.
 */
public class TwitterJSONSerializer {

	private static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss ZZZZ yyyy";

	/**
	 * Converts a JSON array representing a Twitter timeline into a list of
	 * tweets.
	 * 
	 * @param statuses
	 *            the JSON array that contains the timeline.
	 * @return a list of tweets.
	 * @throws JSONException
	 *             if there was a problem parsing the JSON.
	 * @throws ParseError
	 *             if there was a problem parsing a date.
	 */
	public static List<Tweet> deserializeTimeline(JSONArray statuses)
			throws JSONException, ParseError {
		List<Tweet> tweets = new ArrayList<Tweet>();
		int numElems = statuses.length();
		for (int i = 0; i < numElems; ++i)
			tweets.add(deserializeTweet(statuses.getJSONObject(i)));
		return tweets;
	}

	/**
	 * Converts a JSON object representing a Twitter status into a tweet.
	 * 
	 * @param status
	 *            the JSON object that contains the status.
	 * @return a single tweet.
	 * @throws JSONException
	 *             if there was a problem parsing the JSON.
	 * @throws ParseError
	 *             if there was a problem parsing a date.
	 */
	public static Tweet deserializeTweet(JSONObject status)
			throws JSONException, ParseError {
		Tweet tweet = new Tweet();
		tweet.setId(status.getLong("id"));
		tweet.setInReplyToId(getLongINN(status, "in_reply_to_status_id"));
		tweet.setInReplyToScreenName(getStringINN(status,
				"in_reply_to_screen_name"));
		tweet.setDateTime(parseDate(status.getString("created_at")));
		tweet.setText(unescapeHtml(status.getString("text")));
		JSONObject user = status.getJSONObject("user");
		tweet.setScreenName(user.getString("screen_name"));
		return tweet;
	}

	/**
	 * Converts a JSON object representing a Twitter user into an User instance.
	 * 
	 * @param json
	 *            the JSON object that represents the user.
	 * @return an instance of the User class.
	 * @throws JSONException
	 *             if there was a problem parsing the JSON.
	 */
	public static User deserializeUser(JSONObject json) throws JSONException {
		User user = new User();
		user.setScreenName(json.getString("screen_name"));
		user.setRealName(getStringINN(json, "name"));
		user.setAvatarUrl(json.getString("profile_image_url"));
		user.setDescription(getStringINN(json, "description"));
		user.setLocation(getStringINN(json, "location"));
		user.setHomepage(getStringINN(json, "url"));
		user.setNumFollowing(json.getLong("friends_count"));
		user.setNumFollowers(json.getLong("followers_count"));
		user.setNumUpdates(json.getLong("statuses_count"));
		user.setFollowing(getBooleanINN(json, "following"));
		return user;
	}

	private static long getLongINN(JSONObject json, String key)
			throws JSONException {
		if (!json.isNull(key))
			return json.getLong(key);
		else
			return 0;
	}

	private static String getStringINN(JSONObject json, String key)
			throws JSONException {
		if (!json.isNull(key))
			return json.getString(key);
		else
			return "";
	}
	
	private static boolean getBooleanINN(JSONObject json, String key) throws JSONException {
		if (!json.isNull(key))
			return json.getBoolean(key);
		else
			return false;
	}

	private static Date parseDate(String date) throws ParseError {
		SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
		try {
			return df.parse(date);
		} catch (ParseException e) {
			throw new ParseError("Error parsing a date", e);
		}
	}

	private static String unescapeHtml(String text) {
		return text.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;",
				"&");
	}

}
