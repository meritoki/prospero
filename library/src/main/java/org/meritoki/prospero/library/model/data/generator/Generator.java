package org.meritoki.prospero.library.model.data.generator;

import java.io.File;
import java.util.Calendar;

public class Generator {
	public static char seperator = File.separatorChar;
	
	public int getYearMonthDays(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		int days = calendar.getActualMaximum(Calendar.DATE);
		return days;
	}
}
