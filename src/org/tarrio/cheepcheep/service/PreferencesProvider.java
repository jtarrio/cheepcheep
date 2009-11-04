package org.tarrio.cheepcheep.service;

import org.tarrio.cheepcheep.model.Preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesProvider {

	public static final String PREF_NAME = "CheepCheep";

	private Context context;

	public PreferencesProvider(Context context) {
		this.context = context;
	}

	/**
	 * Retrieves the current user preferences.
	 * 
	 * @return an object containing the current preferences.
	 */
	public Preferences get() {
		SharedPreferences sp = context.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		Preferences prefs = new Preferences();
		prefs.setUsername(sp.getString("username", ""));
		prefs.setConsumerToken(sp.getString("consumerToken", ""));
		prefs.setConsumerSecret(sp.getString("consumerSecret", ""));
		return prefs;
	}

	/**
	 * Saves the given preferences.
	 * 
	 * @param prefs
	 *            the object containing the preferences to save.
	 */
	public void save(Preferences prefs) {
		SharedPreferences sp = context.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("username", prefs.getUsername());
		editor.putString("consumerToken", prefs.getConsumerToken());
		editor.putString("consumerSecret", prefs.getConsumerSecret());
		editor.commit();
	}

}