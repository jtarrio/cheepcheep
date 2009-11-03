package org.tarrio.cheepcheep;

import java.util.List;

import org.tarrio.cheepcheep.R;
import org.tarrio.cheepcheep.model.Preferences;
import org.tarrio.cheepcheep.model.Tweet;
import org.tarrio.cheepcheep.model.User;
import org.tarrio.cheepcheep.service.PreferencesProvider;
import org.tarrio.cheepcheep.service.impl.PreferencesProviderImpl;
import org.tarrio.cheepcheep.task.AsyncTwitterTask;
import org.tarrio.cheepcheep.task.FollowUnfollowUserTask;
import org.tarrio.cheepcheep.task.GetUserInfoTask;
import org.tarrio.cheepcheep.task.TaskCallback;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class UserInfoActivity extends Activity implements
		ListView.OnItemClickListener {

	private PreferencesProvider preferencesProvider;
	private TweetListActions tweetListActions;
	private User user;
	private List<Tweet> tweets;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		preferencesProvider = new PreferencesProviderImpl(this);
		Preferences pref = preferencesProvider.get();

		Intent intent = getIntent();
		String username = intent.getData().getPathSegments().get(0);
		setWindowTitle(username);
		doGetUserInfo(pref, username);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (user == null)
			return super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.usermenu, menu);
		if (user.isFollowing())
			menu.removeItem(R.id.Follow);
		else
			menu.removeItem(R.id.Unfollow);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String screenName = user.getScreenName();
		Preferences prefs = preferencesProvider.get();
		switch (item.getItemId()) {
		case R.id.Follow:
			doFollowUnFollowUser(screenName, prefs, true);
			return true;
		case R.id.Unfollow:
			doFollowUnFollowUser(screenName, prefs, false);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (tweets != null)
			tweetListActions.createContextMenuForTweet(menu, menuInfo);
	}

	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		tweetListActions.createMenuForTweetAtPosition(position);
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

	public void setView(User user, List<Tweet> updates, Bitmap avatar) {
		this.user = user;

		View view = getLayoutInflater().inflate(R.layout.userinfo, null);

		setTextForView(view, R.id.ScreenName, "@" + user.getScreenName());
		if (!user.getScreenName().equals(user.getRealName()))
			setTextForViewOrMakeGone(view, R.id.RealName, user.getRealName());
		else
			makeGone(view, R.id.RealName);
		setTextForViewOrMakeGone(view, R.id.Description, user.getDescription());
		setTextForViewOrMakeGone(view, R.id.Location, user.getLocation());
		String counts = String.format(getString(R.string.counts_fmt), user
				.getNumFollowing(), user.getNumFollowers(), user
				.getNumUpdates());
		setTextForView(view, R.id.Counts, counts);
		Button homePageButton = (Button) view.findViewById(R.id.HomePageButton);
		if ("".equals(user.getHomepage()))
			homePageButton.setVisibility(View.GONE);
		else {
			final Uri homepage = Uri.parse(user.getHomepage());
			homePageButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(Intent.ACTION_VIEW, homepage);
					startActivity(i);
				}
			});
		}
		TextView protectedText = (TextView) view
				.findViewById(R.id.ProtectedUpdates);
		if (!user.isSecret())
			protectedText.setVisibility(View.GONE);

		if (updates != null) {
			tweets = updates;
			ListView tweetList = (ListView) view.findViewById(R.id.Updates);
			tweetList.setAdapter(new TweetListAdapter(this, updates));
			tweetList.setOnItemClickListener(this);
			registerForContextMenu(tweetList);
			tweetListActions = new TweetListActions(this, preferencesProvider,
					tweets, new UpdateTweetsCallback(), new ReloadTweetsCallback());
		}

		if (avatar != null) {
			ImageView avatarView = (ImageView) view
					.findViewById(R.id.UserAvatar);
			avatarView.setImageBitmap(avatar);
		}

		setContentView(view);
	}

	private void setTextForViewOrMakeGone(View view, int id, String text) {
		if ("".equals(text))
			makeGone(view, id);
		else
			setTextForView(view, id, text);
	}

	private void setTextForView(View view, int id, String text) {
		((TextView) view.findViewById(id)).setText(text);
	}

	private void makeGone(View view, int id) {
		view.findViewById(id).setVisibility(View.GONE);
	}

	/**
	 * Sets the window's title.
	 */
	private void setWindowTitle(String username) {
		String appName = getString(R.string.app_name);
		String titleFormat = getString(R.string.user_info_title);
		setTitle(String.format(titleFormat, appName, username));
	}

	private void doGetUserInfo(Preferences pref, String username) {
		TaskCallback callback = new TaskCallback() {
			@Override
			public void onSuccess(AsyncTwitterTask task) {
				GetUserInfoTask theTask = (GetUserInfoTask) task;
				setView(theTask.getUser(), theTask.getUpdates(), theTask
						.getAvatar());
			}

			@Override
			public void onFailure(int statusCode, AsyncTwitterTask task) {
				setResult(RESULT_CANCELED);
				finish();
			}
		};
		new GetUserInfoTask(this, pref, callback, username).run();
	}

	private void doFollowUnFollowUser(String screenName, Preferences prefs,
			boolean doFollow) {
		new FollowUnfollowUserTask(this, prefs, new ReloadTweetsCallback(), screenName, doFollow)
				.run();
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
		 * Exits the activity if a tweet was deleted, signalling the
		 * previous activity that it should reload its list of tweets.
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
