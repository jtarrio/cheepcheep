package org.tarrio.cheepcheep.service.impl;

import org.tarrio.cheepcheep.model.Preferences;
import org.tarrio.cheepcheep.service.PreferencesProvider;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesProviderImpl implements PreferencesProvider {

	public static final String PREF_NAME = "CheepCheep";
	
	private Context context;
	
	public PreferencesProviderImpl(Context context) {
		this.context = context;
	}
	
	@Override
	public Preferences get() {
		SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		Preferences prefs = new Preferences();
		prefs.setUsername(sp.getString("username", ""));
		prefs.setConsumerToken(sp.getString("consumerToken", ""));
		prefs.setConsumerSecret(sp.getString("consumerSecret", ""));
		return prefs;
	}
	
	@Override
	public void save(Preferences prefs) {
		SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("username", prefs.getUsername());
		editor.putString("consumerToken", prefs.getConsumerToken());
		editor.putString("consumerSecret", prefs.getConsumerSecret());
		editor.commit();
	}
}
