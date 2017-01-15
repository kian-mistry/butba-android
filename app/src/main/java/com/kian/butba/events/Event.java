package com.kian.butba.events;

import java.util.HashMap;

/**
 * Created by Kian Mistry on 07/01/17.
 */

public class Event {

	private Boolean canEnter;
	private String date;
	private String entryForm;
	private String facebookEvent;
	private String id;
	private String name;
	private String results;
	private HashMap<String, Boolean> tags;
	private String time;
	private String venue;

	public Event() {
		//Required: Empty public constructor.
	}

	public Event(Boolean canEnter, String date, String entryForm, String facebookEvent, String id, String name, String results, HashMap<String, Boolean> tags, String time, String venue) {
		this.canEnter = canEnter;
		this.date = date;
		this.entryForm = entryForm;
		this.facebookEvent = facebookEvent;
		this.id = id;
		this.name = name;
		this.results = results;
		this.tags = tags;
		this.time = time;
		this.venue = venue;
	}

	public Boolean getCanEnter() {
		return canEnter;
	}

	public String getDate() {
		return date;
	}

	public String getEntryForm() {
		return entryForm;
	}

	public String getFacebookEvent() {
		return facebookEvent;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getResults() {
		return results;
	}

	public HashMap<String, Boolean> getTags() {
		return tags;
	}

	public String getTime() {
		return time;
	}

	public String getVenue() {
		return venue;
	}
}
