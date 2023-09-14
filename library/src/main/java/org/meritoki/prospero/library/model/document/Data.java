/*
 * Copyright 2021 Joaquin Osvaldo Rodriguez
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
package org.meritoki.prospero.library.model.document;

import java.io.IOException;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;


public class Data {
	private static Logger logger = LogManager.getLogger(Data.class.getName());
	@JsonProperty
	public String uuid;
	@JsonProperty
	public Unit unit = new Unit();
	@JsonProperty
	public Text text = new Text();

	public Data() {
		this.uuid = UUID.randomUUID().toString();
	}

	public Data(Data data) {
		this.uuid = data.uuid;
		this.unit = new Unit(data.unit);
		this.text = new Text(data.text);
	}

	@JsonIgnore
	public void setUUID(String uuid) {
		this.uuid = uuid;
	}

	@JsonIgnore
	public void setText(Text text) {
		logger.info("setText(" + text + ")");
		this.text = text;
	}

	@JsonProperty
	public Text getText() {
		return this.text;
	}

	@JsonIgnore
	@Override
	public String toString() {
		String string = "";
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			string = ow.writeValueAsString(this);
		} catch (IOException ex) {
			logger.error("IOException " + ex.getMessage());
		}
		return string;
	}
}
