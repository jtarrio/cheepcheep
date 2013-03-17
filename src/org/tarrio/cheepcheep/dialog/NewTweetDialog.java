package org.tarrio.cheepcheep.dialog;

import org.tarrio.cheepcheep.R;
import org.tarrio.cheepcheep.service.PreferencesProvider;
import org.tarrio.cheepcheep.task.AsyncTwitterTask;
import org.tarrio.cheepcheep.task.CreateNewTweetTask;
import org.tarrio.cheepcheep.task.TaskCallback;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NewTweetDialog extends CheepCheepDialog {

	public NewTweetDialog(Activity activity,
			PreferencesProvider preferencesProvider, TaskCallback callback,
			String prefillScreenName, String prefillText, long responseToId) {
		super(activity, preferencesProvider, callback);
		createDialog(prefillScreenName, prefillText, responseToId);
	}

	private void createDialog(String prefillScreenName, String prefillText,
			long responseToId) {
		dialog = new Dialog(activity);
		View view = activity.getLayoutInflater().inflate(R.layout.newtweet,
				null);
		dialog.setContentView(view);
		NewTweetDialogListener listener = new NewTweetDialogListener(activity,
				dialog, view, responseToId);
		((Button) view.findViewById(R.id.NewTweetPostButton))
				.setOnClickListener(listener);
		EditText editText = (EditText) view.findViewById(R.id.NewTweetEdit);
		editText.addTextChangedListener(listener);
		dialog.setTitle(R.string.new_tweet_title);
		StringBuilder prefill = new StringBuilder();
		if ((prefillScreenName != null) && !"".equals(prefillScreenName)) {
			if (responseToId <= 0)
				prefill.append("RT ");
			prefill.append("@");
			prefill.append(prefillScreenName);
			prefill.append(" ");
		}
		if ((prefillText != null) && !"".equals(prefillText))
			prefill.append(prefillText);
		editText.setText(prefill.toString());
	}

	/**
	 * Code that manages the "New Tweet" dialog.
	 */
	private class NewTweetDialogListener implements View.OnClickListener,
			TextWatcher {

		private Context context;
		private Dialog dialog;
		private View view;
		private long responseToId;

		public NewTweetDialogListener(Context context, Dialog dialog,
				View view, long responseToId) {
			this.context = context;
			this.dialog = dialog;
			this.view = view;
			this.responseToId = responseToId;
		}

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.NewTweetPostButton) {
				EditText tweetEdit = (EditText) view
						.findViewById(R.id.NewTweetEdit);
				final String text = tweetEdit.getText().toString();
				TaskCallback repeatDialogCallback = new TaskCallback() {
					@Override
					public void onSuccess(AsyncTwitterTask task) {
						callback.onSuccess(task);
					}

					@Override
					public void onFailure(int statusCode, AsyncTwitterTask task) {
						callback.onFailure(statusCode, task);
						new NewTweetDialog(activity, preferencesProvider,
								callback, null, text, responseToId).show();
					}
				};
				new CreateNewTweetTask(activity, preferencesProvider.get(),
						repeatDialogCallback, text, responseToId).run();
				dialog.dismiss();
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			int textLength = s.length();
			int charsLeft = 140 - textLength;
			String countText = String.format(context
					.getString(R.string.character_count_fmt), charsLeft);
			((TextView) view.findViewById(R.id.NewTweetNumCharsLabel))
					.setText(countText);
			Button postButton = (Button) view
					.findViewById(R.id.NewTweetPostButton);
			postButton.setEnabled((charsLeft >= 0) && (textLength > 0));
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// nothing, but we have to define this member function.
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// nothing, but we have to define this member function.
		}
	}
}
