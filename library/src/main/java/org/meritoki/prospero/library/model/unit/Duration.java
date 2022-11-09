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

import java.util.Calendar;
import java.util.Date;

public class Duration {
	public long hours;
	public long minutes;
	public long seconds;
	public double days;
	public long startDays = -1;
	public long startHours = -1;
	public long startMinutes = -1;
	public long startSeconds = -1;
	public long endDays = -1;
	public long endHours = -1;
	public long endMinutes = -1;
	public long endSeconds = -1;

	public Duration() {
	}
	
	public Duration(Calendar startCalendar, Calendar endCalendar) {
		this(startCalendar.getTime(),endCalendar.getTime());
	}

	public Duration(Date startDate, Date endDate) {
		long diff = endDate.getTime() - startDate.getTime();
		this.seconds = diff / 1000;
		this.minutes = diff / (60 * 1000);
		this.hours = diff / (60 * 60 * 1000);
		this.days = ((diff) / (1000.0 * 60.0 * 60.0 * 24.0));
	}

	public boolean contains(Duration duration) {
		boolean flag = false;
		if(this.startDays > -1 && this.endDays > -1 && this.startDays < duration.days && duration.days < this.endDays) {
			flag = true;
		} else if(this.startHours > -1 && this.endHours > -1 && this.startHours < duration.hours && duration.hours < this.endHours) {
			flag = true;
		}
		return flag;
	}
	
	public String toString() {
		return this.days+"";
	}
}
