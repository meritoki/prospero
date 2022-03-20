package com.meritoki.library.prospero.model.unit;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Pair {

	public double a;
	public double b;
	
	public Pair(double a, double b) {
//		System.out.println("Pair("+a+","+b+")");
		this.a = a;
		this.b = b;
	}
	
	@Override
	public boolean equals(Object object) {
		Pair pair = (Pair)object;
		return this.a == pair.a && this.b == pair.b || this.b == pair.a && this.a == pair.b;
	}
	
	@JsonIgnore
	@Override
	public String toString() {
		String string = "";
		ObjectWriter ow = new ObjectMapper().writer();
		try {
			string = ow.writeValueAsString(this);
		} catch (IOException ex) {
			System.err.println("IOException " + ex.getMessage());
		}
		return string;
	}
}
