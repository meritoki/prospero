/*
 * Copyright 2016-2022 Joaquin Osvaldo Rodriguez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meritoki.prospero.library.model.unit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Frame {
	@JsonIgnore
	static Logger logger = LoggerFactory.getLogger(Frame.class.getName());
	@JsonProperty
	public String uuid;
	@JsonProperty
	public boolean flag;
//	@JsonProperty
//	public long milliseconds;
	@JsonProperty
	public Calendar calendar;
//	@JsonProperty
//	public short latitude = 90;
//	@JsonProperty
//	public short longitude = 360;
//	@JsonProperty
//	public int resolution = 1;
//	@JsonProperty
//	public List<Long> millisecondList;
	@JsonIgnore
	public boolean print = false;
	@JsonProperty 
	public List<Coordinate> coordinateList = new ArrayList<>();
//	@JsonProperty
//	public Map<String, Data> data = new HashMap<>();

	public Frame() {
		this.uuid = UUID.randomUUID().toString();
//		this.millisecondList = new ArrayList<>();
//		this.data = new HashMap<>();
	}
	
//	public List<Data> getDataList() {
//		return (List<Data>) data.values();
//	}
	
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
