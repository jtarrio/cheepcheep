package org.tarrio.cheepcheep;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthException;

import org.tarrio.cheepcheep.http.HttpClientFactory;
import org.tarrio.cheepcheep.http.OAuthHttpClient;
import org.tarrio.cheepcheep.model.Preferences;
import org.tarrio.cheepcheep.service.PreferencesProvider;
import org.tarrio.cheepcheep.service.impl.PreferencesProviderImpl;
import org.tarrio.cheepcheep.task.AsyncTwitterTask;
import org.tarrio.cheepcheep.task.TaskCallback;
import org.tarrio.cheepcheep.task.VerifyCredentialsTask;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class OAuthActivity extends Activity {

	public static final String CALLBACK_URL = "cheepcheep://auth";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final PreferencesProvider preferencesProvider = new PreferencesProviderImpl(
				this);

		Intent i = getIntent();
		if (i.getAction().equals(Constants.ACTION_OAUTH)) {
			signIntoTwitterWebpage(preferencesProvider);
		} else {
			Uri uri = i.getData();
			verifyCredentialsAndReturnToHomeTimeline(preferencesProvider, uri);
		}
	}

	private void verifyCredentialsAndReturnToHomeTimeline(
			final PreferencesProvider preferencesProvider, Uri uri) {
		final Preferences pref = preferencesProvider.get();
		extractCredentials(pref, uri);
		preferencesProvider.save(pref);

		if (!"".equals(pref.getUsername()))
			returnToHomeTimeline();

		TaskCallback callback = new TaskCallback() {
			@Override
			public void onSuccess(AsyncTwitterTask task) {
				pref.setUsername(((VerifyCredentialsTask) task).getUser()
						.getScreenName());
				preferencesProvider.save(pref);

				returnToHomeTimeline();
			}

			@Override
			public void onFailure(int statusCode, AsyncTwitterTask task) {
				returnToHomeTimeline();
			}
		};
		new VerifyCredentialsTask(this, pref, callback).run();
	}

	private void returnToHomeTimeline() {
		Intent mainWindow = new Intent(this, HomeTimelineActivity.class);
		mainWindow.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(mainWindow);
	}

	public void signIntoTwitterWebpage(PreferencesProvider preferencesProvider) {
		OAuthHttpClient client = (OAuthHttpClient) HttpClientFactory
				.getClient(preferencesProvider.get());
		String authUrl;
		try {
			authUrl = client.getProvider().retrieveRequestToken(CALLBACK_URL);
		} catch (OAuthException e) {
			throw new RuntimeException(
					"Error getting the request URL for OAuth.", e);
		} catch (OAuthCommunicationException e) {
			throw new RuntimeException(
					"Error getting the request URL for OAuth.", e);
		}
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
		i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		startActivity(i);
	}

	private void extractCredentials(Preferences pref, Uri uri) {
		String verifier = uri.getQueryParameter("oauth_verifier");
		OAuthHttpClient client = (OAuthHttpClient) HttpClientFactory
				.getClient(pref);
		try {
			client.getProvider().retrieveAccessToken(verifier);
			pref.setConsumerToken(client.getConsumer().getToken());
			pref.setConsumerSecret(client.getConsumer().getTokenSecret());
		} catch (OAuthException e) {
			throw new RuntimeException(e);
		} catch (OAuthCommunicationException e) {
			throw new RuntimeException(e);
		}
	}

}
