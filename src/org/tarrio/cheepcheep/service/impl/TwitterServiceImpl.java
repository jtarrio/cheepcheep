package org.tarrio.cheepcheep.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tarrio.cheepcheep.exceptions.AuthError;
import org.tarrio.cheepcheep.exceptions.NetError;
import org.tarrio.cheepcheep.exceptions.TweetNotFoundError;
import org.tarrio.cheepcheep.exceptions.UserNotFoundError;
import org.tarrio.cheepcheep.exceptions.ParseError;
import org.tarrio.cheepcheep.http.OAuthHttpClient;
import org.tarrio.cheepcheep.model.Tweet;
import org.tarrio.cheepcheep.model.User;
import org.tarrio.cheepcheep.service.TwitterJSONSerializer;
import org.tarrio.cheepcheep.service.TwitterService;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.Uri.Builder;

public class TwitterServiceImpl implements TwitterService {

	private HttpClient httpClient;
	private static final HttpHost host = new HttpHost("twitter.com");

	/**
	 * Creates a Twitter service on the given HTTP client instance.
	 * 
	 * @param client
	 *            the HTTP client instance to use.
	 */
	public TwitterServiceImpl(HttpClient client) {
		this.httpClient = client;
	}

	@Override
	public List<Tweet> getHomeTimeline(long maxCount, long minId)
			throws NetError, ParseError, AuthError {
		try {
			Uri.Builder ub = getTwitterUriBuilder("statuses/home_timeline");
			ub.appendQueryParameter("count", Long
					.toString(maxCount > 0 ? maxCount : 1));
			if (minId > 0) {
				ub.appendQueryParameter("since_id", Long.toString(minId));
			}
			HttpRequest request = new HttpGet(ub.build().toString());
			HttpResponse response = httpClient.execute(host, request);
			checkStatus(response);
			return parseTimeline(response.getEntity().getContent());
		} catch (ClientProtocolException e) {
			throw new NetError("Error loading home timeline", e);
		} catch (IOException e) {
			throw new NetError("Error loading home timeline", e);
		}
	}

	@Override
	public void postUpdate(String text, long responseToId) throws NetError,
			AuthError {
		HttpPost request;
		try {
			List<NameValuePair> query = new ArrayList<NameValuePair>();
			query.add(new BasicNameValuePair("status", text));
			if (responseToId > 0L)
				query.add(new BasicNameValuePair("in_reply_to_status_id", Long
						.toString(responseToId)));
			request = new HttpPost(getTwitterUriBuilder("statuses/update")
					.build().toString());
			// HttpClient sends "100-Continue" instead of "100-continue", so
			// Twitter returns status code 417.
			request.getParams().setBooleanParameter(
					CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(query);
			request.setEntity(entity);
			HttpResponse response = httpClient.execute(host, request);
			checkStatus(response);
		} catch (UnsupportedEncodingException e) {
			throw new NetError(
					"Error formatting the status for posting to the server", e);
		} catch (ClientProtocolException e) {
			throw new NetError("Error posting status to Twitter server", e);
		} catch (IOException e) {
			throw new NetError("Error posting status to Twitter server", e);
		}
	}

	@Override
	public void deleteUpdate(long id) throws NetError, AuthError,
			TweetNotFoundError {
		try {
			// Using POST instead of DELETE because I know that telecom
			// companies have very problematic proxies out there.
			HttpRequest request = new HttpPost(getTwitterUriBuilder(
					"/statuses/destroy/" + id).build().toString());
			HttpResponse response = httpClient.execute(host, request);
			checkStatusForTweetResponse(response);
		} catch (ClientProtocolException e) {
			throw new NetError("Error deleting a status", e);
		} catch (IOException e) {
			throw new NetError("Error deleting a status", e);
		}
	}

	@Override
	public Tweet getUpdate(long id) throws TweetNotFoundError, NetError,
			AuthError, ParseError {
		try {
			Uri.Builder ub = getTwitterUriBuilder("statuses/show/"
					+ Long.toString(id));
			HttpRequest request = new HttpGet(ub.build().toString());
			HttpResponse response = httpClient.execute(host, request);
			checkStatus(response);
			return parseTweet(response.getEntity().getContent());
		} catch (ClientProtocolException e) {
			throw new NetError("Error loading home timeline", e);
		} catch (IOException e) {
			throw new NetError("Error loading home timeline", e);
		}
	}

	@Override
	public User getUserInfo(String screenName) throws UserNotFoundError,
			NetError, AuthError, ParseError {
		try {
			Uri.Builder ub = getTwitterUriBuilder("users/show");
			ub.appendQueryParameter("screen_name", screenName);
			HttpRequest request = new HttpGet(ub.build().toString());
			HttpResponse response = httpClient.execute(host, request);
			checkStatusForUserResponse(response);
			return parseUser(response.getEntity().getContent());
		} catch (ClientProtocolException e) {
			throw new NetError("Error retrieving user info", e);
		} catch (IOException e) {
			throw new NetError("Error retrieving user info", e);
		}
	}

	@Override
	public List<Tweet> getUserTimeline(String screenName, long maxCount)
			throws UserNotFoundError, NetError, ParseError, AuthError {
		try {
			Uri.Builder ub = getTwitterUriBuilder("statuses/user_timeline");
			ub.appendQueryParameter("screen_name", screenName);
			ub.appendQueryParameter("count", Long
					.toString(maxCount > 0 ? maxCount : 1));
			HttpRequest request = new HttpGet(ub.build().toString());
			HttpResponse response = httpClient.execute(host, request);
			checkStatusForUserResponse(response);
			return parseTimeline(response.getEntity().getContent());
		} catch (ClientProtocolException e) {
			throw new NetError("Error loading home timeline", e);
		} catch (IOException e) {
			throw new NetError("Error loading home timeline", e);
		}
	}

	@Override
	public Bitmap getAvatarImage(String url) throws NetError, AuthError {
		try {
			// To do some simple normalisation on the URL.
			Uri uri = Uri.parse(url);
			HttpUriRequest request = new HttpGet(uri.toString());
			HttpResponse response = httpClient.execute(request);
			checkStatus(response);
			return BitmapFactory
					.decodeStream(response.getEntity().getContent());
		} catch (ClientProtocolException e) {
			throw new NetError("Error loading image", e);
		} catch (IOException e) {
			throw new NetError("Error loading image", e);
		}
	}

	@Override
	public void changeFollowUser(String screenName, boolean follow)
			throws UserNotFoundError, NetError, AuthError {
		try {
			Builder ub = getTwitterUriBuilder("/friendships/"
					+ (follow ? "create" : "destroy"));
			ub.appendQueryParameter("screen_name", screenName);
			HttpRequest request = new HttpPost(ub.build().toString());
			HttpResponse response = httpClient.execute(host, request);
			checkStatusForUserResponse(response);
		} catch (ClientProtocolException e) {
			throw new NetError("Error following/unfollowing a user", e);
		} catch (IOException e) {
			throw new NetError("Error following/unfollowing a user", e);
		}
	}

	@Override
	public User verifyCredentials() throws NetError, AuthError, ParseError {
		try {
			Uri.Builder ub = getTwitterUriBuilder("account/verify_credentials");
			HttpRequest request = new HttpGet(ub.build().toString());
			HttpResponse response = httpClient.execute(host, request);
			checkStatus(response);
			return parseUser(response.getEntity().getContent());
		} catch (ClientProtocolException e) {
			throw new NetError("Error verifying the credentials", e);
		} catch (IOException e) {
			throw new NetError("Error verifying the credentials", e);
		}
	}
	
	@Override
	public void cancelCurrentOperation() {
		if (httpClient instanceof OAuthHttpClient)
			((OAuthHttpClient) httpClient).abortCurrentRequest();
	}

	private void checkStatus(HttpResponse response) throws NetError,
			IOException, AuthError {
		int status = response.getStatusLine().getStatusCode();
		checkStatusCommon(status);
	}

	private void checkStatusForUserResponse(HttpResponse response)
			throws UserNotFoundError, NetError, IOException, AuthError {
		int status = response.getStatusLine().getStatusCode();
		if (status == 404)
			throw new UserNotFoundError("Not found");
		checkStatusCommon(status);
	}

	private void checkStatusForTweetResponse(HttpResponse response)
			throws TweetNotFoundError, NetError, IOException, AuthError {
		int status = response.getStatusLine().getStatusCode();
		if (status == 404)
			throw new TweetNotFoundError("Not found");
		checkStatusCommon(status);
	}

	private void checkStatusCommon(int status) throws AuthError, NetError {
		if (status == 401)
			throw new AuthError("Authentication failed");
		if ((status < 200) || (status >= 300))
			throw new NetError(
					"The Twitter server returned a bad status code: "
							+ Integer.toString(status));
	}

	private Uri.Builder getTwitterUriBuilder(String command) {
		Uri.Builder ub = new Uri.Builder().scheme("http").authority(
				"twitter.com").path(command + ".json");
		return ub;
	}

	private List<Tweet> parseTimeline(InputStream stream) throws ParseError,
			NetError {
		try {
			String jsonData = readStreamToString(stream);
			JSONArray statuses = new JSONArray(jsonData);
			return TwitterJSONSerializer.deserializeTimeline(statuses);
		} catch (JSONException e) {
			throw new ParseError(
					"Error parsing the JSON response from the Twitter server",
					e);
		} catch (IOException e) {
			throw new NetError(
					"Error reading the JSON response from the Twitter server while parsing",
					e);
		}
	}

	private Tweet parseTweet(InputStream stream) throws ParseError, NetError {
		try {
			String jsonData = readStreamToString(stream);
			JSONObject status = new JSONObject(jsonData);
			return TwitterJSONSerializer.deserializeTweet(status);
		} catch (JSONException e) {
			throw new ParseError(
					"Error parsing the JSON response from the Twitter server",
					e);
		} catch (IOException e) {
			throw new NetError(
					"Error reading the JSON response from the Twitter server while parsing",
					e);
		}
	}

	private User parseUser(InputStream stream) throws ParseError, NetError {
		String jsonData;
		try {
			jsonData = readStreamToString(stream);
			JSONObject json = new JSONObject(jsonData);
			return TwitterJSONSerializer.deserializeUser(json);
		} catch (JSONException e) {
			throw new ParseError(
					"Error parsing the JSON response from the Twitter server",
					e);
		} catch (IOException e) {
			throw new NetError(
					"Error reading the JSON response from the Twitter server while parsing",
					e);
		}
	}

	private String readStreamToString(InputStream stream) throws IOException {
		char[] buffer = new char[1024];
		StringBuilder jsonDataBuilder = new StringBuilder();
		InputStreamReader isr = new InputStreamReader(stream);
		int len = isr.read(buffer);
		while (len != -1) {
			jsonDataBuilder.append(buffer, 0, len);
			len = isr.read(buffer);
		}
		String jsonData = jsonDataBuilder.toString();
		return jsonData;
	}

}
