package org.tarrio.cheepcheep.dialog;

import org.tarrio.cheepcheep.service.PreferencesProvider;
import org.tarrio.cheepcheep.task.TaskCallback;

import android.app.Activity;
import android.app.Dialog;

public class CheepCheepDialog {

	protected Activity activity;
	protected PreferencesProvider preferencesProvider;
	protected TaskCallback callback;
	protected Dialog dialog;

	public CheepCheepDialog(Activity activity,
			PreferencesProvider preferencesProvider, TaskCallback callback) {
		this.activity = activity;
		this.preferencesProvider = preferencesProvider;
		this.callback = callback;
	}

	public void show() {
		dialog.show();
	}

}