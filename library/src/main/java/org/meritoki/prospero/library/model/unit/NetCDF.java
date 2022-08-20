package org.meritoki.prospero.library.model.unit;

import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;

public class NetCDF {
	public DataType type;
	public float continent;
	public ArrayFloat.D1 latArray;
	public ArrayFloat.D1 lonArray;
	public ArrayInt.D1 timeArray;
	public ArrayFloat.D3 variableArray;
	public Object variable;
}
