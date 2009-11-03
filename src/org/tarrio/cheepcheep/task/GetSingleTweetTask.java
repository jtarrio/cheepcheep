package org.tarrio.cheepcheep.task;

import org.tarrio.cheepcheep.R;
import org.tarrio.cheepcheep.exceptions.AuthError;
import org.tarrio.cheepcheep.exceptions.NetError;
import org.tarrio.cheepcheep.exceptions.ParseError;
import org.tarrio.cheepcheep.exceptions.TweetNotFoundError;
import org.tarrio.cheepcheep.model.Preferences;
import org.tarrio.cheepcheep.model.Tweet;

import android.app.Activity;

public class GetSingleTweetTask extends AsyncTwitterTask {

	private long tweetId;
	private Tweet tweet;

	public GetSingleTweetTask(Activity activity, Preferences prefs,
			TaskCallback callback, long tweetId) {
		super(activity, prefs, callback);
		this.tweetId = tweetId;
	}

	public Tweet getTweet() {
		return tweet;
	}

	public void setTweet(Tweet tweet) {
		this.tweet = tweet;
	}

	@Override
	protected int getProgressMessageResource() {
		return R.string.loading_tweet;
	}

	@Override
	protected void doBackground() throws TweetNotFoundError,
			NetError, AuthError, ParseError {
		tweet = twitterService.getUpdate(tweetId);
	}

}
