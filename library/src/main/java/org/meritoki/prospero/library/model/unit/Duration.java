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
