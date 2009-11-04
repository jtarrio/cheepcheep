package org.tarrio.cheepcheep.task;

import org.tarrio.cheepcheep.R;
import org.tarrio.cheepcheep.exceptions.AuthError;
import org.tarrio.cheepcheep.exceptions.NetError;
import org.tarrio.cheepcheep.exceptions.ParseError;
import org.tarrio.cheepcheep.exceptions.TweetNotFoundError;
import org.tarrio.cheepcheep.exceptions.UserNotFoundError;
import org.tarrio.cheepcheep.http.HttpClientFactory;
import org.tarrio.cheepcheep.model.Preferences;
import org.tarrio.cheepcheep.service.TwitterService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public abstract class AsyncTwitterTask {

	// Thread statuses
	public static final int STATUS_FATAL_ERROR = 0;
	public static final int STATUS_OK = 1;
	public static final int STATUS_ABORTED = 2;
	public static final int STATUS_USER_NOT_FOUND_ERROR = 3;
	public static final int STATUS_TWEET_NOT_FOUND_ERROR = 4;
	public static final int STATUS_NET_ERROR = 5;
	public static final int STATUS_PARSE_ERROR = 6;
	public static final int STATUS_AUTH_INVALID = 7;

	protected Activity activity;
	protected Preferences prefs;
	protected TwitterService twitterService;
	private TaskCallback callback;
	private int status;
	private volatile boolean aborted;

	public AsyncTwitterTask(Activity activity, Preferences prefs,
			TaskCallback successCallback) {
		this.activity = activity;
		this.prefs = prefs;
		this.twitterService = new TwitterService(HttpClientFactory
				.getClient(prefs));
		this.callback = successCallback;
		this.status = STATUS_OK;
		this.aborted = false;
	}

	/**
	 * Returns the resource ID of the message to be shown in the progress
	 * dialog.
	 * 
	 * @return a message ID.
	 */
	protected abstract int getProgressMessageResource();

	/**
	 * This function is called in the asynchronous thread while the progress
	 * dialog is being displayed.
	 * 
	 * Your derived class should override this function with the desired code.
	 * 
	 * @throws UserNotFoundError
	 *             if a user could not be found.
	 * @throws TweetNotFoundError
	 *             if a tweet could not be found.
	 * @throws NetError
	 *             if there was a problem communicating with the Twitter server.
	 * @throws ParseError
	 *             if there was a problem interpreting the results provided by
	 *             Twitter.
	 * @throws AuthError
	 *             if the credentials were not valid.
	 */
	protected abstract void doBackground()
			throws UserNotFoundError, TweetNotFoundError, NetError, ParseError,
			AuthError;

	/**
	 * This function is called to abort the doBackgroundDuringDialog function.
	 * The function should be stopped as soon as possible.
	 */
	synchronized protected void abort() {
		aborted = true;
		twitterService.cancelCurrentOperation();
	}

	/**
	 * Returns whether the background operation was cancelled.
	 * 
	 * @return whether the background operation was cancelled.
	 */
	synchronized public boolean isAborted() {
		return aborted;
	}

	/**
	 * Displays a progress dialog box and, while the user sees that dialog box,
	 * it calls doBackgroundDuringDialog(). Afterwards, the progress box is
	 * dismissed and one of two things happens:
	 * 
	 * - If the background task was successful, doForegroundAfterDialog() is
	 * called in the main thread while doBackgroundAfterDialog is called in the
	 * background thread.
	 * 
	 * - If the background task threw an exception, an error dialog is shown.
	 */
	public void run() {
		if ("".equals(prefs.getConsumerToken()))
			return;

		final Dialog progressDialog = displayProgressDialog(getProgressMessageResource());
		final AsyncTwitterTask task = this;
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressDialog.dismiss();
				status = msg.getData().getInt("status");
				switch (status) {
				case STATUS_OK:
					if (callback != null)
						callback.onSuccess(task);
					break;
				case STATUS_USER_NOT_FOUND_ERROR:
					displayErrorDialog(R.string.error_title_user_not_found,
							R.string.error_user_not_found);
					break;
				case STATUS_TWEET_NOT_FOUND_ERROR:
					displayErrorDialog(R.string.error_title_tweet_not_found,
							R.string.error_tweet_not_found);
					break;
				case STATUS_NET_ERROR:
					displayErrorDialog(R.string.error_title_server,
							R.string.error_server);
					break;
				case STATUS_PARSE_ERROR:
					displayErrorDialog(
							R.string.error_title_unexpected_response,
							R.string.error_unexpected_response);
					break;
				case STATUS_AUTH_INVALID:
					displayErrorDialog(R.string.error_title_invalid_auth,
							R.string.error_invalid_auth);
					break;
				case STATUS_ABORTED:
					if (callback != null)
						callback.onFailure(status, task);
					break;
				default:
					throw new RuntimeException(String.format(
							"status: %d; error: %s", status, msg.getData()
									.getString("error")));
				}
			}
		};
		new ProcessingThread(handler).start();
	}

	/**
	 * Displays a dialog box with an error message.
	 * 
	 * @param titleId
	 *            resource ID of the dialog box's title.
	 * @param msgId
	 *            resource ID of the error message.
	 */
	private void displayErrorDialog(int titleId, int msgId) {
		final AsyncTwitterTask task = this;
		AlertDialog.Builder adb = new AlertDialog.Builder(activity).setTitle(
				titleId).setMessage(msgId).setCancelable(true);
		adb.setNeutralButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if (callback != null)
							callback.onFailure(status, task);
					}
				});
		adb.create().show();
	}

	/**
	 * Displays a progress dialog box with a message.
	 * 
	 * @param msgId
	 *            resource ID of the message to display.
	 * @return a dialog box object, ready to be displayed.
	 */
	private Dialog displayProgressDialog(int msgId) {
		ProgressDialog pd;
		pd = new ProgressDialog(activity);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setMessage(activity.getString(msgId));
		pd.setCancelable(true);
		pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				abort();
			}
		});
		pd.show();
		return pd;
	}

	private class ProcessingThread extends Thread {

		private Handler handler;

		public ProcessingThread(Handler handler) {
			this.handler = handler;
		}

		@Override
		public void run() {
			int status = STATUS_OK;
			Message msg = handler.obtainMessage();
			Bundle b = new Bundle();
			try {
				doBackground();
			} catch (UserNotFoundError e) {
				status = STATUS_USER_NOT_FOUND_ERROR;
			} catch (TweetNotFoundError e) {
				status = STATUS_TWEET_NOT_FOUND_ERROR;
			} catch (NetError e) {
				status = STATUS_NET_ERROR;
			} catch (ParseError e) {
				status = STATUS_PARSE_ERROR;
			} catch (AuthError e) {
				status = STATUS_AUTH_INVALID;
			} catch (Exception e) {
				status = STATUS_FATAL_ERROR;
				b.putString("error", e.toString());
			}
			if (isAborted())
				status = STATUS_ABORTED;

			b.putInt("status", status);
			msg.setData(b);
			handler.sendMessage(msg);
		}
	}

}
