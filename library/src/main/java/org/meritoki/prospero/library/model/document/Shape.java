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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@JsonTypeInfo(use = Id.CLASS,
include = JsonTypeInfo.As.PROPERTY,
property = "type")
@JsonSubTypes({
@Type(value = Grid.class),
@Type(value = Guide.class)
})
public class Shape {

	@JsonIgnore
	private static Logger logger = LogManager.getLogger(Shape.class.getName());
	@JsonProperty
	public String uuid;
	@JsonProperty
	public ShapeType type;
	@JsonProperty
	public Position position = new Position();
	@JsonIgnore
	public BufferedImage bufferedImage;
	@JsonProperty
	public Data data = new Data();
	@JsonProperty
	public List<Text> textList = new ArrayList<>();

	public Shape() {
		this.uuid = UUID.randomUUID().toString();
		this.type = ShapeType.RECTANGLE;
	}

	/**
	 * Copy constructor
	 * 
	 * @param shape
	 */
	public Shape(Shape shape, boolean flag) {
		if(flag) {
			this.uuid = shape.uuid;
		} else {
			this.uuid = UUID.randomUUID().toString();
		}
		this.type = shape.type;
		this.position = new Position(shape.position);
		this.bufferedImage = shape.bufferedImage;
		this.data = new Data(shape.data);
		for(Text text: shape.textList) {
			this.textList.add(new Text(text));
		}
	}
	
	@JsonIgnore
	public boolean equals(Shape shape) {
		boolean flag = false;
		if(this.uuid.equals(shape.uuid)) {
			flag = true;
		}
		return flag;
	}
	
	public boolean contains(Point point) {
		boolean flag = this.position.contains(point);
//		logger.info("contains("+point+") this.position="+this.position);
//		logger.info("contains("+point+") flag="+flag);
		return flag;
	}
	
	public void setScale(double scale) {
		//logger.debug("setScale("+scale+")");
		this.position.setScale(scale);
	}
	
	public void setRelativeScale(double scale) {
		//logger.debug("setRelativeScale("+scale+")");
		this.position.setRelativeScale(scale);
	}
	
	public void setMargin(double margin) {
		//logger.debug("setMargin("+margin+")");
		this.position.setMargin(margin);
	}
	
	public void setOffset(double offset) {
		//logger.debug("setOffset("+offset+")");
		this.position.setOffset(offset);
	}

	@JsonProperty
	public void addText(Text text) {
		//logger.debug("addText(" + text.value + ")");
		this.textList.add(text);
	}

	/**
	 * Function creates a Map from the textList that shows the frequency that a text
	 * value has been input by a user
	 * 
	 * @return
	 */
	@JsonIgnore
	public Map<String, Integer> getTextMap() {
		int count = 0;
		Map<String, Integer> textMap = new HashMap<>();
		for (Text text : this.textList) {
			count = (textMap.get(text.value) != null) ? textMap.get(text.value) : 0;
			++count;
			textMap.put(text.value, count);
		}
		return textMap;
	}

	/**
	 * Function returns the text value with the highest frequency of input
	 * 
	 * @return
	 */
	@JsonIgnore
	public Text getDefaultText() {
		int max = 0;
		Text text = new Text();
		int value = 0;
		for (Map.Entry<String, Integer> entry : this.getTextMap().entrySet()) {
			value = entry.getValue().intValue();
			if (value > max) {
				max = value;
				text = new Text();
				text.value = entry.getKey();
			}
		}
		return text;
	}

	@JsonProperty
	public List<Text> getTextList() {
		List<Text> textList = new ArrayList<>();
		Text text = null;
		for (Map.Entry<String, Integer> entry : this.getTextMap().entrySet()) {
			text = new Text();
			text.value = entry.getKey();
			textList.add(text);
		}
		return textList;
	}

	@JsonProperty
	public Data getData() {
		return this.data;
	}

	@JsonProperty
	public void setData(Data data) {
		this.data = data;
	}

	@JsonIgnore
	@Override
	public String toString() {
		String string = "";
		ObjectWriter ow = new ObjectMapper().writer();//.withDefaultPrettyPrinter();
		if(logger.isDebugEnabled()) {
		try {
			string = ow.writeValueAsString(this);
		} catch (IOException ex) {
			logger.error("IOException " + ex.getMessage());
		}
		} else if(logger.isInfoEnabled()) {
			string = "{\"uuid\":"+this.uuid+", \"position\":"+position+"}";
		}
		return string;
	}
}
