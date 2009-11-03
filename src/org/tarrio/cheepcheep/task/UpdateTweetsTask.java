package org.tarrio.cheepcheep.task;

import java.util.List;

import org.tarrio.cheepcheep.Constants;
import org.tarrio.cheepcheep.HomeTimelineActivity;
import org.tarrio.cheepcheep.R;
import org.tarrio.cheepcheep.exceptions.AuthError;
import org.tarrio.cheepcheep.exceptions.NetError;
import org.tarrio.cheepcheep.exceptions.ParseError;
import org.tarrio.cheepcheep.model.Preferences;
import org.tarrio.cheepcheep.model.Tweet;

public class UpdateTweetsTask extends AsyncTwitterTask {

	private List<Tweet> newTweets;
	private long topId;

	public UpdateTweetsTask(HomeTimelineActivity activity, Preferences prefs,
			TaskCallback callback, long topId) {
		super(activity, prefs, callback);
		this.topId = topId;
	}

	public List<Tweet> getNewTweets() {
		return newTweets;
	}

	@Override
	protected int getProgressMessageResource() {
		return R.string.loading_tweets;
	}

	@Override
	protected void doBackground() throws NetError, ParseError,
			AuthError {
		newTweets = twitterService.getHomeTimeline(Constants.HOME_MAX_TWEETS,
				topId);
	}

}
