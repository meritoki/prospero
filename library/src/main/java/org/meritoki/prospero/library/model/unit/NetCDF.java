package org.meritoki.prospero.library.model.unit;

import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.nc2.Dimension;

public class NetCDF {
	public DataType type;
	public float continent;
	public Dimension xDimension;
	public Dimension yDimension;
	public ArrayFloat.D1 latArray;
	public ArrayFloat.D2 latMatrix;
	public ArrayFloat.D1 lonArray;
	public ArrayFloat.D2 lonMatrix;
	public ArrayInt.D1 timeArray;
	public ArrayFloat.D3 variableArray;
	public Object variable;
}
