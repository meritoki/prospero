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
package org.meritoki.prospero.library.model.node.data.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.unit.Event;
import org.meritoki.prospero.library.model.unit.NetCDF;
import org.meritoki.prospero.library.model.unit.Interval;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Time;

import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayShort;

public class OceanSource extends Source {
	
	protected Map<String, List<NetCDF>> netCDFMap = new HashMap<>();
	public float earthRadius = 6371;
	
	public OceanSource() {
		super();
		this.calendarFlag = true;
	}
	
	@Override
	public void query(Query query) throws Exception {
		this.intervalList = query.getIntervalList(this.getStartYear(), this.getEndYear());
		if (this.intervalList != null) {
			for (Interval i : this.intervalList) {
				this.load(query, i);
			}
			query.objectListAdd(new Result(Mode.COMPLETE));
		}
	}


	
	public void load(Query query, Interval interval) throws Exception {
		List<Time> timeList = Time.getTimeList(interval);
		List<NetCDF> loadList;
		for(Time time: timeList) {
			if (!Thread.interrupted()) {
				loadList = this.read(time.year, time.month);
				Result result = new Result();
				result.map.put("time", time);
				result.map.put("netCDFList", new ArrayList<NetCDF>((loadList)));
				query.objectList.add(result);
			} else {
				throw new InterruptedException();
			}
		}
	}
	
	public List<NetCDF> netCDFMapGet(int y, int m) throws Exception {
		if (this.netCDFMap == null)
			this.netCDFMap = new HashMap<>();
		List<NetCDF> eList = this.netCDFMap.get(y + "" + m);
		if (eList == null) {
			eList = this.read(y, m);
			if (eList != null) {
				this.netCDFMap.put(y + "" + m, eList);
			} else {
				eList = new ArrayList<>();
			}
		}
		eList = new ArrayList<>(eList);
		return eList;
	}
	
	public List<NetCDF> read(int year, int month) throws Exception {
		return null;
	}

	public double getModulus(double radius, double phi, double theta, double dRadius, double dPhi, double dTheta) {
		Vector<Double> sumVector = new Vector<>();
		sumVector.add(0.0);
		sumVector.add(0.0);
		sumVector.add(0.0);
		Vector<Double> phiVector = this.getPhiVector(phi, theta);
		Vector<Double> thetaVector = this.getThetaVector(theta);
		for (int i = 0; i < phiVector.size(); i++) {
			double d = phiVector.get(i);
			d *= dPhi;
			d *= (1 / radius);
			phiVector.set(i, d);
		}
		for (int i = 0; i < thetaVector.size(); i++) {
			double d = thetaVector.get(i);
			d *= dTheta;
			d *= (1 / (radius * Math.sin(phi)));
			thetaVector.set(i, d);
		}
		sumVector.set(0, phiVector.get(0) + thetaVector.get(0));
		sumVector.set(1, phiVector.get(1) + thetaVector.get(1));
		sumVector.set(2, phiVector.get(2) + thetaVector.get(2));
		double modulus = Math
				.sqrt(Math.pow(sumVector.get(0), 2) + Math.pow(sumVector.get(1), 2) + Math.pow(sumVector.get(2), 2));
		return modulus;
	}

	public Vector<Double> getRadiusVector(double phi, double theta) {
		Vector<Double> radiusVector = new Vector<>();
		radiusVector.add(Math.cos(theta) * Math.sin(phi));
		radiusVector.add(Math.sin(theta) * Math.sin(phi));
		radiusVector.add(Math.cos(phi));
		return radiusVector;
	}

	public Vector<Double> getThetaVector(double theta) {
		Vector<Double> thetaVector = new Vector<>();
		thetaVector.add(-Math.sin(theta));
		thetaVector.add(Math.cos(theta));
		thetaVector.add(0.0);
		return thetaVector;
	}

	public Vector<Double> getPhiVector(double phi, double theta) {
		Vector<Double> phiVector = new Vector<>();
		phiVector.add(Math.cos(theta) * Math.cos(phi));
		phiVector.add(Math.sin(theta) * Math.cos(phi));
		phiVector.add(-Math.sin(phi));
		return phiVector;
	}

	public double getDerivative(String type, double uA, double uB, double variableA, double variableB) {
		double derivative = 0;
		switch (type) {
		case "central": {
			derivative = ((uB - uA) / (2 * (variableB - variableA)));
			break;
		}
		case "forward": {
			derivative = ((uB - uA) / ((variableB - variableA)));
			break;
		}
		case "backward": {
			derivative = ((uB - uA) / ((variableB - variableA)));
			break;
		}
		}
		return derivative;
	}
	
	public float kelvinToCelsius(float kelvin) {
		return (float) (kelvin - 273.15);
	}
	
	public ArrayFloat.D3 getSSTArray(ArrayShort.D3 sstArray, int timeCount, int latitudeCount, int longitudeCount,
			double scaleFactor, double addOffset) {
		ArrayFloat.D3 newSSTArray = new ArrayFloat.D3(timeCount, latitudeCount, longitudeCount);
		for (int t = 0; t < timeCount; t++) {
			for (int lat = 0; lat < latitudeCount; lat++) {
				for (int lon = 0; lon < longitudeCount; lon++) {
					float sst = sstArray.get(t, lat, lon);
					sst *= scaleFactor;
					sst += addOffset;
					sst = this.kelvinToCelsius(sst);
					newSSTArray.set(t, lat, lon, sst);
				}
			}
		}
		return newSSTArray;
	}
	
	public ArrayFloat.D3 getMSLArray(ArrayShort.D3 mslArray, int timeCount, int latitudeCount, int longitudeCount,
			double scaleFactor, double addOffset) {
		ArrayFloat.D3 newMSLArray = new ArrayFloat.D3(timeCount, latitudeCount, longitudeCount);
		for (int t = 0; t < timeCount; t++) {
			for (int lat = 0; lat < latitudeCount; lat++) {
				for (int lon = 0; lon < longitudeCount; lon++) {
					float msl = mslArray.get(t, lat, lon);
					msl *= scaleFactor;
					msl += addOffset;
					newMSLArray.set(t, lat, lon, msl);
				}
			}
		}
		return newMSLArray;
	}

	public float getContinent(double scaleFactor, double addOffset) {
		float continent = -32767;
		continent *= scaleFactor;
		continent += addOffset;
		continent = this.kelvinToCelsius(continent);
		return continent;
	}
}
