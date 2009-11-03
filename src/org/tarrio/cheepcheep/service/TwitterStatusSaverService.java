package org.tarrio.cheepcheep.service;

import java.util.List;

import org.tarrio.cheepcheep.model.Tweet;

public interface TwitterStatusSaverService {
	
	void appendToTimeline(List<Tweet> tweets, long maxKeep);
	
	List<Tweet> loadTimeline();
	
	void clear();
}
