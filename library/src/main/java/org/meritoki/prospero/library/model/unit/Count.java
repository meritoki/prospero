package org.meritoki.prospero.library.model.unit;

public class Count {
	public int value;
	public char operator;
	
	public Count() {}
	
	public Count(char operator, int value) {
		this.operator = operator;
		this.value = value;
	}
}
