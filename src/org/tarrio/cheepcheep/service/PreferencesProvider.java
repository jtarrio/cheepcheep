package org.tarrio.cheepcheep.service;

import org.tarrio.cheepcheep.model.Preferences;

public interface PreferencesProvider {

	/**
	 * Retrieves the current user preferences.
	 * @return an object containing the current preferences.
	 */
	Preferences get();

	/**
	 * Saves the given preferences.
	 * @param prefs the object containing the preferences to save.
	 */
	void save(Preferences prefs);

}