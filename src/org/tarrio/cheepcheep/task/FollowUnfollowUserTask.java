package org.tarrio.cheepcheep.task;

import org.tarrio.cheepcheep.R;
import org.tarrio.cheepcheep.exceptions.AuthError;
import org.tarrio.cheepcheep.exceptions.NetError;
import org.tarrio.cheepcheep.exceptions.ParseError;
import org.tarrio.cheepcheep.exceptions.UserNotFoundError;
import org.tarrio.cheepcheep.model.Preferences;

import android.app.Activity;

public class FollowUnfollowUserTask extends AsyncTwitterTask {

	private String screenName;
	private boolean follow;
	
	public FollowUnfollowUserTask(Activity activity, Preferences prefs, TaskCallback callback, String screenName, boolean follow) {
		super(activity, prefs, callback);
		this.screenName = screenName;
		this.follow = follow;
	}

	@Override
	protected int getProgressMessageResource() {
		if (follow)
			return R.string.following_user;
		else
			return R.string.unfollowing_user;
	}

	@Override
	protected void doBackground() throws NetError, ParseError,
			AuthError, UserNotFoundError {
		twitterService.changeFollowUser(screenName, follow);
	}

}
