package com.meritoki.library.prospero.model.unit;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Label {
	public long seconds;
	public int interval;
	public Calendar calendar;
	
	public String toString() {
		String string = "";
		switch(this.interval) {
		case -2: {
			string += calendar.get(Calendar.YEAR);
			break;
		}
		case -1: {
			string += calendar.get(Calendar.YEAR);
			break;
		}
		case 0: {
			string += calendar.get(Calendar.YEAR);
			break;
		}
		case 1: {
			string += calendar.get(Calendar.YEAR);
			break;
		}
		case 2: {
			string += (new SimpleDateFormat("MMM").format(calendar.getTime()));
			break;
		}
	
		}
		return string;
	}

}
