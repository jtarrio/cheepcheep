package org.tarrio.cheepcheep.task;

import org.tarrio.cheepcheep.R;
import org.tarrio.cheepcheep.exceptions.AuthError;
import org.tarrio.cheepcheep.exceptions.NetError;
import org.tarrio.cheepcheep.exceptions.ParseError;
import org.tarrio.cheepcheep.exceptions.TweetNotFoundError;
import org.tarrio.cheepcheep.exceptions.UserNotFoundError;
import org.tarrio.cheepcheep.model.Preferences;
import org.tarrio.cheepcheep.model.User;

import android.app.Activity;

public class VerifyCredentialsTask extends AsyncTwitterTask {

	public VerifyCredentialsTask(Activity activity, Preferences prefs,
			TaskCallback successCallback) {
		super(activity, prefs, successCallback);
	}

	private User user;
	
	public User getUser() {
		return user;
	}
	
	@Override
	protected int getProgressMessageResource() {
		return R.string.loading_user_info;
	}

	@Override
	protected void doBackground() throws UserNotFoundError,
			TweetNotFoundError, NetError, ParseError, AuthError {
		user = twitterService.verifyCredentials();
	}

}
