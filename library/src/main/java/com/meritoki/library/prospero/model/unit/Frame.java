package com.meritoki.library.prospero.model.unit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Frame {
	@JsonIgnore
	static Logger logger = LogManager.getLogger(Frame.class.getName());
	@JsonProperty
	public String uuid;
	@JsonProperty
	public boolean flag;
	@JsonProperty
	public long milliseconds;
	@JsonProperty
	public Calendar calendar;
	@JsonProperty
	public short latitude = 90;
	@JsonProperty
	public short longitude = 360;
	@JsonProperty
	public int resolution = 1;
	@JsonProperty
	public List<Long> millisecondList;
	@JsonIgnore
	public boolean print = false;
	@JsonProperty
	public Map<String, List<Data>> data;

	public Frame() {
		this.uuid = UUID.randomUUID().toString();
		this.millisecondList = new ArrayList<>();
		this.data = new HashMap<>();
	}
	
	public boolean containsCalendar(Calendar calendar) {
		
		if (this.calendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
				&& this.calendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
				&& this.calendar.get(Calendar.DATE) == calendar.get(Calendar.DATE)
				&& this.calendar.get(Calendar.HOUR_OF_DAY) == calendar.get(Calendar.HOUR_OF_DAY)
				&& this.calendar.get(Calendar.MINUTE) == calendar.get(Calendar.MINUTE)
				&& this.calendar.get(Calendar.SECOND) == calendar.get(Calendar.SECOND)) {
			if(print)System.out.println(this.calendar.getTime()+".containsCalendar("+calendar.getTime()+") true");
			return true;
		}
		if(print)System.out.println(this.calendar.getTime()+".containsCalendar("+calendar.getTime()+") false");
		return false;
	}

	@JsonIgnore
    @Override
    public String toString(){
//        String string = "";
//        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
//        try {
//            string = ow.writeValueAsString(this);
//        } catch (IOException ex) {
//        	logger.error(ex.getMessage());
//        }
		String string = uuid;
        return string;
    }
}
