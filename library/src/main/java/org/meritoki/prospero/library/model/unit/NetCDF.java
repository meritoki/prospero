/*
 * Copyright 2016-2022 Joaquin Osvaldo Rodriguez
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
package org.meritoki.prospero.library.model.unit;

import ucar.ma2.ArrayDouble;
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
	public ArrayDouble.D1 timeDoubleArray;
	public ArrayFloat.D3 variableCube;
	public ArrayFloat.D2 variableMatrix;
	public ArrayFloat.D1 variableArray;
	public Object variable;
	public Integer pressure;
}
