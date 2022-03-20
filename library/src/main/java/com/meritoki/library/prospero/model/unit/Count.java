package com.meritoki.library.prospero.model.unit;

public class Count {
	public int value;
	public char operator;
	
	public Count() {}
	
	public Count(char operator, int value) {
		this.operator = operator;
		this.value = value;
	}
}
