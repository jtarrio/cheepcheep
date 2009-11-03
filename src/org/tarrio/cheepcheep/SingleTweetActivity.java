package org.tarrio.cheepcheep;

import java.util.ArrayList;

import org.tarrio.cheepcheep.R;
import org.tarrio.cheepcheep.model.Preferences;
import org.tarrio.cheepcheep.model.Tweet;
import org.tarrio.cheepcheep.service.PreferencesProvider;
import org.tarrio.cheepcheep.service.impl.PreferencesProviderImpl;
import org.tarrio.cheepcheep.task.AsyncTwitterTask;
import org.tarrio.cheepcheep.task.GetSingleTweetTask;
import org.tarrio.cheepcheep.task.TaskCallback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SingleTweetActivity extends Activity implements OnClickListener {

	private PreferencesProvider preferencesProvider;
	private TweetListActions tweetListActions;
	private Tweet tweet;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		preferencesProvider = new PreferencesProviderImpl(this);
		Preferences pref = preferencesProvider.get();

		Intent intent = getIntent();
		String username = intent.getData().getPathSegments().get(0);
		long tweetId = Long
				.parseLong(intent.getData().getPathSegments().get(1));
		setWindowTitle(username);
		doGetTweet(pref, tweetId);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		tweetListActions.createContextMenuForTweet(menu, tweet);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (tweetListActions.processContextItemSelectedForTweets(item, tweet))
			return true;
		else
			return super.onOptionsItemSelected(item);
	}

	public void setView(Tweet tweet) {
		View view = getLayoutInflater().inflate(R.layout.singletweet, null);
		TextView author = (TextView) view.findViewById(R.id.Author);
		TextView date = (TextView) view.findViewById(R.id.Date);
		TextView text = (TextView) view.findViewById(R.id.Text);

		author.setText(tweet.getScreenName());
		date.setText(DateUtils.getRelativeDateTimeString(this, tweet
				.getDateTime().getTime(), DateUtils.SECOND_IN_MILLIS,
				DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL));
		text.setText(tweet.getText());

		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		tweets.add(tweet);
		this.tweet = tweet;
		tweetListActions = new TweetListActions(this, preferencesProvider,
				tweets, new UpdateTweetsCallback(), new ReloadTweetsCallback());
		view.setOnClickListener(this);
		setContentView(view);
	}

	/**
	 * Sets the window's title.
	 */
	private void setWindowTitle(String username) {
		String appName = getString(R.string.app_name);
		String titleFormat = getString(R.string.single_tweet_title);
		setTitle(String.format(titleFormat, appName, username));
	}

	private void doGetTweet(Preferences pref, long tweetId) {
		TaskCallback callback = new TaskCallback() {
			@Override
			public void onSuccess(AsyncTwitterTask task) {
				GetSingleTweetTask theTask = (GetSingleTweetTask) task;
				setView(theTask.getTweet());
			}

			@Override
			public void onFailure(int statusCode, AsyncTwitterTask task) {
				setResult(RESULT_CANCELED);
				finish();
			}
		};
		new GetSingleTweetTask(this, preferencesProvider.get(), callback,
				tweetId).run();
	}

	@Override
	public void onClick(View v) {
		tweetListActions.createMenuForTweet(tweet);
	}

	private class UpdateTweetsCallback implements TaskCallback {
		/**
		 * Exits the activity if a tweet was posted, signalling the
		 * previous activity that it should update its list of tweets.
		 */
		@Override
		public void onSuccess(AsyncTwitterTask task) {
			setResult(Constants.RESULT_UPDATE);
			finish();
		}

		@Override
		public void onFailure(int statusCode, AsyncTwitterTask task) {
		}
	}

	private class ReloadTweetsCallback implements TaskCallback {
		/**
		 * Exits the activity if a tweet was posted or deleted, signalling to
		 * the previous activity that it should reload its list of tweets.
		 */
		@Override
		public void onSuccess(AsyncTwitterTask task) {
			setResult(Constants.RESULT_RELOAD);
			finish();
		}

		@Override
		public void onFailure(int statusCode, AsyncTwitterTask task) {
		}
	}

}
