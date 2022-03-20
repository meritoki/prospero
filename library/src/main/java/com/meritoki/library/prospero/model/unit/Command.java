package com.meritoki.library.prospero.model.unit;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meritoki.library.prospero.model.query.Query;

public class Command {
	@JsonProperty
	public String variable;
	@JsonProperty
	public String source;
	@JsonProperty
	public String name;
	@JsonProperty
	public Query query;
}
