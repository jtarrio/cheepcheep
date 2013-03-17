package org.tarrio.cheepcheep;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tarrio.cheepcheep.R;
import org.tarrio.cheepcheep.dialog.ConfirmDeleteTweetDialog;
import org.tarrio.cheepcheep.dialog.NewTweetDialog;
import org.tarrio.cheepcheep.model.Preferences;
import org.tarrio.cheepcheep.model.Tweet;
import org.tarrio.cheepcheep.service.PreferencesProvider;
import org.tarrio.cheepcheep.task.TaskCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.Uri.Builder;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class TweetListActions {

	private Activity activity;
	private PreferencesProvider preferencesProvider;
	private List<Tweet> tweets;
	private TaskCallback postCallback;
	private TaskCallback deleteCallback;

	// Regular expression that matches @nicks or (some common) http(s) URLs.
	private final Pattern atNick = Pattern
			.compile("(^|\\W)(@[a-zA-Z0-9_-]+|https?://[a-zA-Z0-9./_?&=+%-]+[a-zA-Z0-9/_])(?=$|\\W)");

	public TweetListActions(Activity activity,
			PreferencesProvider preferencesProvider, List<Tweet> tweets,
			TaskCallback postCallback, TaskCallback deleteCallback) {
		super();
		this.activity = activity;
		this.preferencesProvider = preferencesProvider;
		this.tweets = tweets;
		this.postCallback = postCallback;
		this.deleteCallback = deleteCallback;
	}

	/**
	 * Creates a context menu for tweets.
	 * 
	 * @param menu
	 *            the menu to populate.
	 * @param menuInfo
	 *            information about the currently selected element.
	 */
	public void createContextMenuForTweet(ContextMenu menu,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle(R.string.menu_actions);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		createContextMenuForTweet(menu, tweets.get((int) info.id));
	}

	/**
	 * Creates a context menu for tweets.
	 * 
	 * @param menu
	 *            the menu to populate.
	 * @param tweet
	 *            the tweet the menu belongs to.
	 */
	public void createContextMenuForTweet(Menu menu, Tweet tweet) {
		activity.getMenuInflater().inflate(R.menu.tweetctxmenu, menu);
		Preferences prefs = preferencesProvider.get();
		if (!prefs.getUsername().equals(tweet.getScreenName()))
			menu.removeItem(R.id.Delete);
	}

	/**
	 * Processes the event when someone clicks in a menu created by
	 * createContextMenuForTweets().
	 * 
	 * @param item
	 *            the item the user clicked on.
	 * @param tweet
	 *            the tweet the menu belongs to.
	 * @return whether a recognized menu item was clicked.
	 */
	public boolean processContextItemSelectedForTweets(MenuItem item,
			Tweet tweet) {
		switch (item.getItemId()) {
		case R.id.Reply:
			new NewTweetDialog(activity, preferencesProvider, postCallback, tweet
					.getScreenName(), null, tweet.getId()).show();
			return true;
		case R.id.Retweet:
			new NewTweetDialog(activity, preferencesProvider, postCallback, tweet
					.getScreenName(), tweet.getText(), 0).show();
			return true;
		case R.id.Delete:
			new ConfirmDeleteTweetDialog(activity, preferencesProvider,
					deleteCallback, tweet.getId()).show();
			return true;
		default:
			return false;
		}
	}

	/**
	 * Creates the menu that is displayed when the user selects a single tweet.
	 * 
	 * @param position
	 *            the position of the tweet in the list of tweets.
	 */
	public void createMenuForTweetAtPosition(int position) {
		Tweet tweet = tweets.get(position);
		createMenuForTweet(tweet);
	}

	/**
	 * Creates the menu that is displayed when the user selects a single tweet.
	 * 
	 * @param tweet
	 *            the tweet to create the menu for.
	 */
	public void createMenuForTweet(Tweet tweet) {
		AlertDialog.Builder adb = new AlertDialog.Builder(activity);
		adb.setTitle(R.string.menu_browse);
		List<String> destinations = getLinksFromTweet(tweet);
		adb.setItems(destinations.toArray(new String[0]),
				new TweetClickListener(activity, destinations, tweet));
		adb.create().show();
	}

	/**
	 * Extracts all links from a tweet, including the one pointing to the screen
	 * name.
	 * 
	 * @param tweet
	 *            the tweet to extract the links from.
	 * @return a list with all links.
	 */
	private List<String> getLinksFromTweet(Tweet tweet) {
		List<String> destinations = new ArrayList<String>();
		if (tweet.getInReplyToId() != 0)
			destinations.add(activity
					.getString(R.string.browse_to_original_message));
		destinations.add("@" + tweet.getScreenName());
		Matcher m = atNick.matcher(tweet.getText());
		while (m.find()) {
			String dest = m.group(2);
			if (!destinations.contains(dest))
				destinations.add(dest);
		}
		return destinations;
	}

	private class TweetClickListener implements DialogInterface.OnClickListener {

		private Context context;
		private List<String> destinations;
		private Tweet tweet;

		public TweetClickListener(Context context, List<String> destinations,
				Tweet tweet) {
			this.context = context;
			this.destinations = destinations;
			this.tweet = tweet;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			String dest = destinations.get(which);
			if ((which == 0)
					&& dest.equals(context
							.getString(R.string.browse_to_original_message))) {
				browseToTweet(tweet.getInReplyToScreenName(), tweet
						.getInReplyToId());
			} else if (dest.startsWith("@")) {
				browseToUser(dest.substring(1));
			} else {
				browseToUrl(dest);
			}
		}

		private void browseToUrl(String dest) {
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(dest));
			activity.startActivity(i);
		}

		private void browseToTweet(String userName, Long tweetId) {
			Uri uri = getActionUriBuilder("tweet").appendPath(userName)
					.appendPath(Long.toString(tweetId)).build();
			Intent i = new Intent(Intent.ACTION_VIEW, uri, context,
					SingleTweetActivity.class);
			activity.startActivityForResult(i, Constants.SHOW_TWEET);
		}

		private void browseToUser(String username) {
			Uri uri = getActionUriBuilder("user").path(username).build();
			Intent i = new Intent(Intent.ACTION_VIEW, uri, context,
					UserInfoActivity.class);
			activity.startActivityForResult(i, Constants.SHOW_USER);
		}

		private Builder getActionUriBuilder(String action) {
			return new Uri.Builder().scheme("cheepcheep").authority(action);
		}
	}

}
