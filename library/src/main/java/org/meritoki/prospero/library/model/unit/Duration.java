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

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * 20230608 Use Time Object for Start and End
 * @author jorodriguez
 *
 */
public class Duration {
	@JsonProperty
	public long hours;
	@JsonProperty
	public long minutes;
	@JsonProperty
	public long seconds;
	@JsonProperty
	public double days;
	@JsonIgnore
	public long startDays = -1;
	@JsonIgnore
	public long startHours = -1;
	@JsonIgnore
	public long startMinutes = -1;
	@JsonIgnore
	public long startSeconds = -1;
	@JsonIgnore
	public long endDays = -1;
	@JsonIgnore
	public long endHours = -1;
	@JsonIgnore
	public long endMinutes = -1;
	@JsonIgnore
	public long endSeconds = -1;

	public Duration() {
	}
	
	public Duration(Calendar startCalendar, Calendar endCalendar) {
		this(startCalendar.getTime(),endCalendar.getTime());
	}

	public Duration(Date startDate, Date endDate) {
		long milliseconds = endDate.getTime() - startDate.getTime();
		this.seconds = milliseconds / 1000;
		this.minutes = milliseconds / (60 * 1000);
		this.hours = milliseconds / (60 * 60 * 1000);
		this.days = ((milliseconds) / (1000.0 * 60.0 * 60.0 * 24.0));
	}

	@JsonIgnore
	public boolean contains(Duration duration) {
		boolean flag = false;
		if(this.startDays > -1 && this.endDays > -1 && this.startDays < duration.days && duration.days < this.endDays) {
			flag = true;
		} else if(this.startHours > -1 && this.endHours > -1 && this.startHours < duration.hours && duration.hours < this.endHours) {
			flag = true;
		}
		return flag;
	}
	
//	public String toString() {
//		return this.days+"";
//	}
	
	@JsonIgnore
	@Override
	public String toString() {
		String string = "";
		ObjectWriter ow = new ObjectMapper().writer();//.withDefaultPrettyPrinter();
		try {
			string = ow.writeValueAsString(this);
		} catch (IOException ex) {
			System.err.println("IOException " + ex.getMessage());
		}
		return string;
	}
}
