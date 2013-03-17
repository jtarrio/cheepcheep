package org.tarrio.cheepcheep.task;

import org.tarrio.cheepcheep.R;
import org.tarrio.cheepcheep.exceptions.AuthError;
import org.tarrio.cheepcheep.exceptions.NetError;
import org.tarrio.cheepcheep.exceptions.ParseError;
import org.tarrio.cheepcheep.exceptions.TweetNotFoundError;
import org.tarrio.cheepcheep.model.Preferences;

import android.app.Activity;

public class DeleteTweetTask extends AsyncTwitterTask {

	private long tweetId;

	public DeleteTweetTask(Activity activity, Preferences prefs, TaskCallback callback,
			long tweetId) {
		super(activity, prefs, callback);
		this.tweetId = tweetId;
	}

	@Override
	protected int getProgressMessageResource() {
		return R.string.deleting_tweet;
	}

	@Override
	protected void doBackground() throws NetError, ParseError,
			AuthError, TweetNotFoundError {
		twitterService.deleteUpdate(tweetId);
	}

}
