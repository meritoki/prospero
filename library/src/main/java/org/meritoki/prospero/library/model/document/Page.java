package org.meritoki.prospero.library.model.document;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.unit.Event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.meritoki.library.controller.node.NodeController;

public class Page {
	
	/**
	 * Unique identifier instance of Page class
	 */
	@JsonProperty
	public String uuid;
	@JsonProperty
	public String eventFileName;
	@JsonProperty
	public String time = null;
	@JsonProperty
	public String sourceID = null;
	@JsonIgnore
	public List<Event> eventList;
	@JsonProperty
	public List<Query> queryList;
	/**
	 * Class constructor
	 */
	public Page() {
		this.uuid = UUID.randomUUID().toString();
	}
	
	public Page(String time, String sourceID) {
		this.time = time;
		this.sourceID = sourceID;
		this.eventFileName = this.time+"-"+this.sourceID+".json";
	}
	
	public List<Event> getEventList() {
		if(this.eventList == null) {
			this.eventList = (List<Event>)NodeController.openJson(new File(this.eventFileName), new TypeReference<List<Event>>() {});
		}
		return this.eventList;
	}
	
	public void setEventList(List<Event> eventList) {
		this.eventList = eventList;
	}

}
