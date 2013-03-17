package org.tarrio.cheepcheep;

import java.util.Date;
import java.util.List;

import org.tarrio.cheepcheep.R;
import org.tarrio.cheepcheep.dialog.NewTweetDialog;
import org.tarrio.cheepcheep.model.Preferences;
import org.tarrio.cheepcheep.model.Tweet;
import org.tarrio.cheepcheep.service.PreferencesProvider;
import org.tarrio.cheepcheep.service.TwitterStatusSaverService;
import org.tarrio.cheepcheep.task.AsyncTwitterTask;
import org.tarrio.cheepcheep.task.TaskCallback;
import org.tarrio.cheepcheep.task.UpdateTweetsTask;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class HomeTimelineActivity extends ListActivity {

	// Time interval in ms during which two updates will turn the second update
	// into a full reload.
	private static final long RELOAD_INTERVAL = 5 * 1000;

	private List<Tweet> tweets;
	private TweetListAdapter tweetListAdapter;
	private PreferencesProvider preferencesProvider;
	private TwitterStatusSaverService twitterStatusSaver;
	private TweetListActions tweetListActions;

	private long lastUpdateTimestamp;

	/**
	 * Returns the view's PreferencesProvider object.
	 * 
	 * @return a PreferencesProvider object.
	 */
	public PreferencesProvider getPreferencesProvider() {
		return preferencesProvider;
	}

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setupView();

		preferencesProvider = new PreferencesProvider(this);
		twitterStatusSaver = new TwitterStatusSaverService(this);
		tweets = twitterStatusSaver.loadTimeline();
		tweetListActions = new TweetListActions(this, preferencesProvider,
				tweets, new UpdateTweetsCallback(), new ReloadTweetsCallback());
		lastUpdateTimestamp = 0L;
		
		changeView();
		tweetListAdapter = new TweetListAdapter(this, tweets);
		setListAdapter(tweetListAdapter);
		updateTweets();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		getMenuInflater().inflate(R.menu.mainmenu, menu);
		if (hasCredentials(preferencesProvider.get())) {
			menu.removeItem(R.id.Signin);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.NewTweet:
			newTweet();
			return true;
		case R.id.Update:
			updateTweets();
			return true;
		case R.id.Signin:
			signIn();
			return true;
		case R.id.Signout:
			signOut();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		tweetListActions.createContextMenuForTweet(menu, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		Tweet tweet = tweets.get((int) info.id);
		if (tweetListActions.processContextItemSelectedForTweets(item, tweet))
			return true;
		else
			return super.onContextItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Constants.RESULT_UPDATE)
			updateTweets();
		else if (resultCode == Constants.RESULT_RELOAD)
			reloadTweets();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		tweetListActions.createMenuForTweetAtPosition(position);
	}

	/**
	 * Displays the dialog box for creating a new tweet.
	 */
	private void newTweet() {
		new NewTweetDialog(this, preferencesProvider,
				new UpdateTweetsCallback(), null, null, 0L).show();
	}

	/**
	 * Loads any new tweets, adds them to the current list of tweets, and
	 * updates the displayed tweet list. If an update was done less than
	 * RELOAD_INTERVAL milliseconds ago, it will be a full reload.
	 */
	private void updateTweets() {
		long now = new Date().getTime();
		loadMoreTweets(now - lastUpdateTimestamp <= RELOAD_INTERVAL);
	}

	/**
	 * Clears the caches and reloads all tweets.
	 */
	private void reloadTweets() {
		loadMoreTweets(true);
	}

	/**
	 * Loads tweets and updates the displayed tweet list.
	 * 
	 * @param reload
	 *            if true, it will load new tweets to append to the current list
	 *            of tweets; otherwise, it will clear the list and reload all
	 *            tweets.
	 */
	private void loadMoreTweets(final boolean reload) {
		long topId = (reload || tweets.isEmpty()) ? 0L : tweets.get(0).getId();

		TaskCallback callback = new TaskCallback() {
			@Override
			public void onSuccess(AsyncTwitterTask t) {
				UpdateTweetsTask task = (UpdateTweetsTask) t;
				long minKeepId = 0L;

				if (reload) {
					tweets.clear();
					twitterStatusSaver.clear();
					changeView();
				}

				List<Tweet> newTweets = task.getNewTweets();
				if ((newTweets != null) && (newTweets.size() > 0)) {
					tweets.addAll(0, newTweets);
					if (tweets.size() > Constants.HOME_MAX_TWEETS) {
						minKeepId = tweets.get(Constants.HOME_MAX_TWEETS - 1)
								.getId();
					}
					twitterStatusSaver.appendToTimeline(newTweets, minKeepId);
					while (tweets.size() > Constants.HOME_MAX_TWEETS)
						tweets.remove(tweets.size() - 1);
				}
				lastUpdateTimestamp = new Date().getTime();
				tweetListAdapter.notifyDataSetChanged();
			}

			@Override
			public void onFailure(int statusCode, AsyncTwitterTask task) {
				lastUpdateTimestamp = new Date().getTime();
				tweetListAdapter.notifyDataSetChanged();
			}
		};
		new UpdateTweetsTask(this, preferencesProvider.get(), callback, topId)
				.run();
	}

	private void clearTweets() {
		tweets.clear();
		twitterStatusSaver.clear();
		changeView();
		tweetListAdapter.notifyDataSetChanged();
	}

	/**
	 * Signs in to Twitter using OAuth
	 */
	private void signIn() {
		Intent i = new Intent(Constants.ACTION_OAUTH, Uri.parse(""), this,
				OAuthActivity.class);
		startActivityForResult(i, Constants.DO_OAUTH);
	}

	/**
	 * Deletes the OAuth credentials
	 */
	private void signOut() {
		Preferences pref = preferencesProvider.get();
		clearCredentials(pref);
		preferencesProvider.save(pref);
		clearTweets();
	}

	/**
	 * Returns whether the given preferences object has some (presumably valid)
	 * credentials.
	 * 
	 * @param pref
	 *            a preferences object.
	 * @return whether the given preferences object has some (presumably valid)
	 *         credentials.
	 */
	private boolean hasCredentials(Preferences pref) {
		return !"".equals(pref.getConsumerToken());
	}

	/**
	 * Clears the credentials from the given preferences object.
	 * 
	 * @param pref
	 *            the preferences object.
	 */
	private void clearCredentials(Preferences pref) {
		pref.setUsername("");
		pref.setConsumerToken("");
		pref.setConsumerSecret("");
	}

	/**
	 * Sets the activity's view up.
	 */
	private void setupView() {
		setContentView(R.layout.hometimeline);
		String appName = getString(R.string.app_name);
		String noAuthText = String.format(getString(R.string.noauth_text),
				appName);
		((TextView) findViewById(R.id.NoAuthText)).setText(noAuthText);
		registerForContextMenu(getListView());
	}

	/**
	 * Changes the view in the window depending on whether the user is
	 * authenticated or not.
	 */
	private void changeView() {
		Preferences prefs = preferencesProvider.get();
		String appName = getString(R.string.app_name);

		if (hasCredentials(prefs)) {
			String userName = prefs.getUsername();
			String titleFormat = getString(R.string.home_timeline_title);
			setTitle(String.format(titleFormat, appName, userName));
			findViewById(R.id.NoAuthTextLayout).setVisibility(View.GONE);
			findViewById(android.R.id.list).setVisibility(View.VISIBLE);
			registerForContextMenu(findViewById(android.R.id.list));
		} else {
			setTitle(appName);
			Button signinButton = (Button) findViewById(R.id.Signin);
			signinButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					signIn();
				}
			});
			findViewById(R.id.NoAuthTextLayout).setVisibility(View.VISIBLE);
			findViewById(android.R.id.list).setVisibility(View.GONE);
		}
	}

	private class UpdateTweetsCallback implements TaskCallback {
		/**
		 * Updates the tweets after posting a tweet.
		 */
		@Override
		public void onSuccess(AsyncTwitterTask task) {
			updateTweets();
		}

		/**
		 * Does nothing if posting fails.
		 */
		@Override
		public void onFailure(int statusCode, AsyncTwitterTask task) {
		}
	}

	private class ReloadTweetsCallback implements TaskCallback {
		/**
		 * Reloads all tweets after deleting a tweet.
		 */
		@Override
		public void onSuccess(AsyncTwitterTask task) {
			reloadTweets();
		}

		/**
		 * Does nothing if deleting a tweet fails.
		 */
		@Override
		public void onFailure(int statusCode, AsyncTwitterTask task) {
		}
	}

}