package org.tarrio.cheepcheep.http;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.signature.SignatureMethod;

import org.apache.http.client.HttpClient;
import org.tarrio.cheepcheep.model.Preferences;

public class HttpClientFactory {

	/*
	 * The OAuth consumer key and consumer secret are in the class
	 * OAuthCredentials, which was not checked in.
	 * 
	 * To compile this project, register an application on Twitter and create a
	 * class with that name in this same package, with this content:
	 * 
	 * <code>
	 * package org.tarrio.cheepcheep.http;
	 * 
	 * class OAuthCredentials {
	 *   static String CONSUMER_KEY = "contents of consumer key";
	 *   static String CONSUMER_SECRET = "contents of consumer secret";
	 * }
	 * </code>
	 */

	private static String REQUEST_TOKEN_URL = "http://api.twitter.com/oauth/request_token";
	private static String ACCESS_TOKEN_URL = "http://api.twitter.com/oauth/access_token";
	private static String AUTHORIZE_URL = "http://api.twitter.com/oauth/authorize";

	private static HttpClient client;

	/**
	 * Returns a HTTP client which can access Twitter using OAuth.
	 * 
	 * @param prefs
	 *            the user's Preferences object.
	 * @return an authenticating HTTP client.
	 */
	public static HttpClient getClient(Preferences prefs) {
		if (client != null)
			return client;

		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(
				OAuthCredentials.CONSUMER_KEY,
				OAuthCredentials.CONSUMER_SECRET, SignatureMethod.HMAC_SHA1);
		OAuthProvider provider = new DefaultOAuthProvider(consumer,
				REQUEST_TOKEN_URL, ACCESS_TOKEN_URL, AUTHORIZE_URL);

		if (!"".equals(prefs.getConsumerToken())) {
			consumer.setTokenWithSecret(prefs.getConsumerToken(), prefs
					.getConsumerSecret());
		}

		client = new OAuthHttpClient(consumer, provider);
		return client;
	}
}
