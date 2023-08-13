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
package org.meritoki.prospero.library.model.provider.aws.s3.goes16;

import java.io.File;

import org.meritoki.prospero.library.model.unit.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Request {

	static Logger logger = LoggerFactory.getLogger(Request.class.getName());
	public static char seperator = File.separatorChar;
	@JsonProperty
	public String status = "pending";
	@JsonProperty
	public String bucket;
	@JsonProperty
	public Time time;
	@JsonProperty
	public String prefix;
	@JsonProperty
	public String key;
	@JsonProperty
	public String path;
	@JsonProperty
	public String fileName;
	
	public Request() {
	
	}
	
	@JsonIgnore
	public void setFileName(String fileName) {
		logger.info("setFileName(" + fileName + ")");
		this.fileName = fileName;
	}

	@JsonIgnore
	public String getPath() {
		return this.path + seperator;
	}

	@JsonIgnore
	public String getFilePath() {
		return this.getPath() + this.fileName;
	}
}
