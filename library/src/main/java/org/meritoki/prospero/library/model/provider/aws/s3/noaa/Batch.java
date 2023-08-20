/*
 * Copyright 2019-2023 Joaquin Osvaldo Rodriguez
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
package org.meritoki.prospero.library.model.provider.aws.s3.noaa;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.meritoki.prospero.library.model.unit.Time;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Batch {

	@JsonProperty
	public String uuid;
	@JsonProperty
	public List<Request> requestList = null;
	@JsonProperty
	public Form form;

	public Batch() {
		this.uuid = UUID.randomUUID().toString();
	}

	public Batch(Form form) {
		this.uuid = UUID.randomUUID().toString();
		this.form = form;
		this.requestList = this.getRequestList();
	}

	@JsonIgnore
	public List<Request> getRequestList() {
		List<Request> requestList = new ArrayList<Request>();
		Request request = null;
//		String fileName = null;
		for(Time time: this.form.timeList) {
//			for(int t=0; t<24;t+=6) {
				request = new Request();
//				time.hour = t;
				request.bucket = this.form.bucket;
				request.path = this.form.outputPath;
				request.time = new Time(time);
				request.prefix = this.form.prefix;
				requestList.add(request);
//			}
		}
		return requestList;
	}

	public static String getDate(String year, String month, List<String> dayList) {
		String date = "";
		int size = dayList.size();
		String startDay = dayList.get(0);
		String endDay = dayList.get(size - 1);
		int monthInteger = Integer.parseInt(month) - 1;
		int startDate = Integer.parseInt(startDay);
		int endDate = Integer.parseInt(endDay);
		int maxDays = getMaxDays(Integer.parseInt(year), monthInteger, 1);
		if (endDate > maxDays) {
			endDate = maxDays;
		}
		DecimalFormat df = new DecimalFormat("00");
		date = year + (month) + df.format(startDate) + "-" + year + (month) + df.format(endDate);
		return date;
	}

	public static int getMaxDays(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		return maxDay;
	}
	
	public String getSlashString(List<String> param) {
		String value = "";
		for (int i = 0; i < param.size(); i++) {
			if (i != param.size() - 1) {
				value += param.get(i) + "/";
			} else {
				value += param.get(i);
			}
		}
		return value;
	}

}
