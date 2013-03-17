package org.tarrio.cheepcheep.task;

import org.tarrio.cheepcheep.R;
import org.tarrio.cheepcheep.exceptions.AuthError;
import org.tarrio.cheepcheep.exceptions.NetError;
import org.tarrio.cheepcheep.exceptions.ParseError;
import org.tarrio.cheepcheep.model.Preferences;

import android.app.Activity;

public class CreateNewTweetTask extends AsyncTwitterTask {

	private String text;
	private long responseToId;

	public CreateNewTweetTask(Activity activity, Preferences prefs, TaskCallback callback,
			String text, long responseToId) {
		super(activity, prefs, callback);
		this.text = text;
		this.responseToId = responseToId;
	}

	@Override
	protected int getProgressMessageResource() {
		return R.string.posting_tweet;
	}

	@Override
	protected void doBackground() throws NetError, ParseError,
			AuthError {
		twitterService.postUpdate(text, responseToId);
	}

}
