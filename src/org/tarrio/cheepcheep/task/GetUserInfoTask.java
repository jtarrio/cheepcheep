package org.tarrio.cheepcheep.task;

import java.util.List;

import org.tarrio.cheepcheep.Constants;
import org.tarrio.cheepcheep.R;
import org.tarrio.cheepcheep.exceptions.AuthError;
import org.tarrio.cheepcheep.exceptions.CheepCheepException;
import org.tarrio.cheepcheep.exceptions.NetError;
import org.tarrio.cheepcheep.exceptions.ParseError;
import org.tarrio.cheepcheep.exceptions.UserNotFoundError;
import org.tarrio.cheepcheep.model.Preferences;
import org.tarrio.cheepcheep.model.Tweet;
import org.tarrio.cheepcheep.model.User;

import android.app.Activity;
import android.graphics.Bitmap;

public class GetUserInfoTask extends AsyncTwitterTask {

	private String username;
	private User user;
	private List<Tweet> updates;
	private Bitmap avatar;

	public GetUserInfoTask(Activity activity, Preferences prefs,
			TaskCallback callback, String username) {
		super(activity, prefs, callback);
		this.setUsername(username);
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void setUpdates(List<Tweet> updates) {
		this.updates = updates;
	}

	public List<Tweet> getUpdates() {
		return updates;
	}

	public void setAvatar(Bitmap avatar) {
		this.avatar = avatar;
	}

	public Bitmap getAvatar() {
		return avatar;
	}

	@Override
	protected int getProgressMessageResource() {
		return R.string.loading_user_info;
	}

	@Override
	protected void doBackground() throws NetError, ParseError, AuthError,
			UserNotFoundError {
		setUser(twitterService.getUserInfo(getUsername()));
		try {
			setUpdates(twitterService.getUserTimeline(getUsername(),
					Constants.USER_MAX_TWEETS));
		} catch (AuthError e) {
			// This exception here doesn't really mean "authentication error".
			// It means "the user has protected updates."
			user.setSecret(true);
		}
		if (!"".equals(getUser().getAvatarUrl()))
			try {
				setAvatar(twitterService.getAvatarImage(getUser()
						.getAvatarUrl()));
			} catch (CheepCheepException e) {
				// drop; we'll do without an image if we can't get it.
			}
	}

}
