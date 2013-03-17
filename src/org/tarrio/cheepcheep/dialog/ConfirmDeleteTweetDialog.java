package org.tarrio.cheepcheep.dialog;

import org.tarrio.cheepcheep.R;
import org.tarrio.cheepcheep.service.PreferencesProvider;
import org.tarrio.cheepcheep.task.DeleteTweetTask;
import org.tarrio.cheepcheep.task.TaskCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class ConfirmDeleteTweetDialog extends CheepCheepDialog {

	public ConfirmDeleteTweetDialog(Activity activity,
			PreferencesProvider preferencesProvider, TaskCallback callback, long tweetId) {
		super(activity, preferencesProvider, callback);
		createDialog(tweetId);
	}

	private void createDialog(long tweetId) {
		final long id = tweetId;
		AlertDialog.Builder adb;
		adb = new AlertDialog.Builder(activity);
		adb.setMessage(R.string.confirm_delete);
		adb.setCancelable(true);
		adb.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						new DeleteTweetTask(activity, preferencesProvider.get(), callback,
								id).run();
					}
				});
		adb.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		dialog = adb.create();
	}
}
