package org.meritoki.prospero.library.model.unit;

import java.util.ArrayList;
import java.util.List;

public class Station {
	public String name;
	public String country;
	public List<Coordinate> coordinateList = new ArrayList<>();
	public boolean flag;
}
