package com.meritoki.library.prospero.model.unit;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Data {
	@JsonIgnore
	static Logger logger = LogManager.getLogger(Data.class.getName());
//	@JsonProperty
//	public long milliseconds;
	@JsonProperty
	public DataType type;
	@JsonProperty
	public float value;
	
	public Data() {
		
	}
	
	public Data(DataType type, float value) {
//		this.milliseconds = milliseconds;
		this.type=type;
		this.value = value;
	}
	
//	public Data(DataType type, float value, long milliseconds) {
//		this.milliseconds = milliseconds;
//		this.type=type;
//		this.value = value;
//	}
	
	@JsonIgnore
    @Override
    public String toString(){
        String string = "";
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            string = ow.writeValueAsString(this);
        } catch (IOException ex) {
        	logger.error(ex.getMessage());
        }
        return string;
    }
}
