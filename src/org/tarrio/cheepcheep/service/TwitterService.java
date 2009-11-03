package org.tarrio.cheepcheep.service;

import java.util.List;

import org.tarrio.cheepcheep.exceptions.AuthError;
import org.tarrio.cheepcheep.exceptions.NetError;
import org.tarrio.cheepcheep.exceptions.TweetNotFoundError;
import org.tarrio.cheepcheep.exceptions.UserNotFoundError;
import org.tarrio.cheepcheep.exceptions.ParseError;
import org.tarrio.cheepcheep.model.Tweet;
import org.tarrio.cheepcheep.model.User;

import android.graphics.Bitmap;

public interface TwitterService {

	/**
	 * Gets a list of tweets in the public timeline.
	 * 
	 * @param maxCount
	 *            the maximum number of tweets to load.
	 * @param minId
	 *            if not 0, only tweets after the given ID will be loaded.
	 * @return A list of tweets.
	 * @throws NetError
	 *             if there was a problem communicating with the Twitter server.
	 * @throws ParseError
	 *             if there was a problem parsing the response from the Twitter
	 *             server.
	 * @throws AuthError
	 *             if the authentication credentials were invalid.
	 */
	List<Tweet> getHomeTimeline(long maxCount, long minId) throws NetError,
			ParseError, AuthError;

	/**
	 * Posts a new tweet with the given text.
	 * 
	 * @param text
	 *            the text for the new tweet.
	 * @param responseToId
	 *            if not 0, the ID of the tweet this tweet is a response to.
	 * @throws NetError
	 *             if there was a problem communicating with the Twitter server.
	 * @throws AuthError
	 *             if the authentication credentials were invalid.
	 */
	void postUpdate(String text, long responseToId) throws NetError, AuthError;

	/**
	 * Deletes the tweet with the given ID.
	 * 
	 * @param id
	 *            the ID of the tweet to delete.
	 * @throws TweetNotFoundError
	 *             if the tweet could not be found.
	 * @throws NetError
	 *             if there was a problem communicating with the Twitter server.
	 * @throws AuthError
	 *             if the authentication credentials were invalid.
	 */
	void deleteUpdate(long id) throws TweetNotFoundError, NetError, AuthError;

	/**
	 * Downloads the tweet with the given ID.
	 * 
	 * @param id
	 *            the ID of the tweet to retrieve.
	 * @return a single tweet.
	 * @throws TweetNotFoundError
	 *             if the tweet could not be found.
	 * @throws NetError
	 *             if there was a problem communicating with the Twitter server.
	 * @throws AuthError
	 *             if the authentication credentials were invalid.
	 * @throws ParseError
	 *             if there was a problem parsing the response from the Twitter
	 *             server.
	 */
	Tweet getUpdate(long id) throws TweetNotFoundError, NetError, AuthError,
			ParseError;

	/**
	 * Retrieves information about a user.
	 * 
	 * @param screenName
	 *            the user's screen name.
	 * @return Information about a user.
	 * @throws UserNotFoundError
	 *             if the user could not be found.
	 * @throws NetError
	 *             if there was a problem communicating with the Twitter server.
	 * @throws AuthError
	 *             if the authentication credentials were invalid.
	 * @throws ParseError
	 *             if there was a problem parsing the response from the Twitter
	 *             server.
	 */
	User getUserInfo(String screenName) throws UserNotFoundError, NetError,
			AuthError, ParseError;

	/**
	 * Gets a list with the latest tweets by a given user.
	 * 
	 * @param screenName
	 *            the user's screen name.
	 * @param maxCount
	 *            the maximum number of tweets to load.
	 * @return A list of tweets.
	 * @throws UserNotFoundError
	 *             if the user could not be found.
	 * @throws NetError
	 *             if there was a problem communicating with the Twitter server.
	 * @throws ParseError
	 *             if there was a problem parsing the response from the Twitter
	 *             server.
	 * @throws AuthError
	 *             if the authentication credentials were invalid.
	 */
	List<Tweet> getUserTimeline(String screenName, long maxCount)
			throws UserNotFoundError, NetError, ParseError, AuthError;

	/**
	 * Downloads an image, presumably an avatar image.
	 * 
	 * @param url
	 *            the URL where the image resides.
	 * @return a Bitmap representing the image.
	 * @throws AuthError
	 *             if the authentication credentials were invalid.
	 * @throws NetError
	 *             if there was a problem communicating with the Twitter server.
	 */
	Bitmap getAvatarImage(String url) throws NetError, AuthError;

	/**
	 * Follows or unfollows a user, depending on the value of the parameter.
	 * 
	 * @param screenName
	 *            the screen name of the user to follow or unfollow.
	 * @param follow
	 *            if true, the user will be followed; if false, the user will be
	 *            unfollowed.
	 * @throws NetError
	 *             if there was a problem communicating with the Twitter server.
	 * @throws AuthError
	 *             if the authentication credentials were invalid.
	 */
	void changeFollowUser(String screenName, boolean follow)
			throws UserNotFoundError, NetError, AuthError;

	/**
	 * Checks that a user is logged-in and obtains information about the user if
	 * so.
	 * 
	 * @return information about the logged-in user.
	 * @throws AuthError
	 *             if the authentication credentials were invalid.
	 * @throws NetError
	 *             if there was a problem communicating with the Twitter server.
	 * @throws ParseError
	 *             if there was a problem parsing the response from the Twitter
	 *             server.
	 */
	User verifyCredentials() throws NetError, AuthError, ParseError;

	/**
	 * Cancels the current operation, if any.
	 */
	void cancelCurrentOperation();
}
