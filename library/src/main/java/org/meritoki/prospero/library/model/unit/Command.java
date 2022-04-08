package org.meritoki.prospero.library.model.unit;

import org.meritoki.prospero.library.model.query.Query;

import com.fasterxml.jackson.annotation.JsonProperty;

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
