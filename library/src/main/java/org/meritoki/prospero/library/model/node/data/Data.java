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
package org.meritoki.prospero.library.model.node.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.meritoki.prospero.library.model.node.data.source.AMONOAA;
import org.meritoki.prospero.library.model.node.data.source.AtmosphereWMO;
import org.meritoki.prospero.library.model.node.data.source.CityNaturalEarth;
import org.meritoki.prospero.library.model.node.data.source.CountryNaturalEarth;
import org.meritoki.prospero.library.model.node.data.source.CycloneUTNERA5Test;
import org.meritoki.prospero.library.model.node.data.source.CycloneUTNERAInterim;
import org.meritoki.prospero.library.model.node.data.source.CycloneUTNERAInterimTest;
import org.meritoki.prospero.library.model.node.data.source.ENSONOAA;
import org.meritoki.prospero.library.model.node.data.source.EarthquakeUSGSEarthquakeHazardProgram;
import org.meritoki.prospero.library.model.node.data.source.ElNino12NOAA;
import org.meritoki.prospero.library.model.node.data.source.ElNino34NOAA;
import org.meritoki.prospero.library.model.node.data.source.ElNino3NOAA;
import org.meritoki.prospero.library.model.node.data.source.ElNino4NOAA;
import org.meritoki.prospero.library.model.node.data.source.GOESNOAA;
import org.meritoki.prospero.library.model.node.data.source.GeopotentialERA5;
import org.meritoki.prospero.library.model.node.data.source.IODNOAA;
import org.meritoki.prospero.library.model.node.data.source.JetstreamERA5;
import org.meritoki.prospero.library.model.node.data.source.JetstreamERAInterim;
import org.meritoki.prospero.library.model.node.data.source.LithosphereGEBCO;
import org.meritoki.prospero.library.model.node.data.source.MagneticNOAAEMAG;
import org.meritoki.prospero.library.model.node.data.source.MagneticNOAAWMM;
import org.meritoki.prospero.library.model.node.data.source.MeanSeaLevelPressureERA5;
import org.meritoki.prospero.library.model.node.data.source.PDONOAA;
import org.meritoki.prospero.library.model.node.data.source.SAMNOAA;
import org.meritoki.prospero.library.model.node.data.source.SILSOSunspot;
import org.meritoki.prospero.library.model.node.data.source.SeaIceToth;
import org.meritoki.prospero.library.model.node.data.source.SeaSurfaceTemperatureERA5;
import org.meritoki.prospero.library.model.node.data.source.SeaSurfaceTemperatureERAInterim;
import org.meritoki.prospero.library.model.node.data.source.Source;
import org.meritoki.prospero.library.model.node.data.source.TectonicPeterBird;
import org.meritoki.prospero.library.model.node.data.source.TornadoHistoryProject;
import org.meritoki.prospero.library.model.node.data.source.UTNTrack;
import org.meritoki.prospero.library.model.node.data.source.VolcanicNOAA;
import org.meritoki.prospero.library.model.node.data.source.VorticityERA5;
import org.meritoki.prospero.library.model.node.data.source.VorticityERAInterim;
import org.meritoki.prospero.library.model.node.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meritoki.module.library.model.Node;

/**
 * Data is a class for loading and making data available via a common interface.
 * Some data must be loaded with respect to time, while other data for our
 * purposes remains constant over time.
 * 
 * Need to be able to load data passively and actively Passive loading is used
 * by the Time Dialog and uses threads to load data in the background Active
 * loading is used to have a resource available immediately when you ask for it.
 * 
 * @author jorodriguez
 *
 */
public class Data extends Node {

	static Logger logger = LoggerFactory.getLogger(Data.class.getName());
	public Map<String, Source> sourceMap = new HashMap<String, Source>();
	public String basePath;

	public Data() {
		super("Data");
		this.filter = false;
		this.sourceMap.put("cc7b89a0-ccc4-4a76-a79b-c0e04b9dd45a", new CycloneUTNERAInterimTest());
		this.sourceMap.put("2d611935-9786-4c28-9dcf-f18cf3e99a3a", new CycloneUTNERAInterim());
		this.sourceMap.put("281cbf52-7014-4229-bffd-35c8ba41bcb5", new CycloneUTNERA5Test());
		this.sourceMap.put("f4d6ead6-949a-42a9-9327-a8e22790e0e7", new JetstreamERAInterim());
		this.sourceMap.put("73428541-23ea-4c4a-bc82-4fb4db5afe05", new JetstreamERA5());
		this.sourceMap.put("d6eb88d6-100c-4948-8fd1-5300b724ec2d", new CountryNaturalEarth());
		this.sourceMap.put("9bc2dd83-85c9-48fe-818f-f62db97c594a", new CityNaturalEarth());
		this.sourceMap.put("f4f4b169-fdc4-44db-b93a-85f8416aec2c", new SeaSurfaceTemperatureERAInterim());
		this.sourceMap.put("e0538d57-044d-48c2-b3a7-b985eada81fc", new SeaSurfaceTemperatureERA5());
		this.sourceMap.put("7cfb5b0a-0f8a-4e38-b0a9-5d50bf64a7b5", new EarthquakeUSGSEarthquakeHazardProgram());
		this.sourceMap.put("3580b76c-b70d-4cdd-9a80-4feb57c72c77", new MagneticNOAAEMAG());
		this.sourceMap.put("1aac29c0-e2f6-45e8-9921-c88397957795", new LithosphereGEBCO());
		this.sourceMap.put("8f6ef7b8-b8d1-452c-944a-c77d2e971db2", new TectonicPeterBird());
		this.sourceMap.put("495ae7cd-9781-4b56-a2d0-cd3f6b2e80e1", new VolcanicNOAA());
		this.sourceMap.put("ff6e87b8-b8d1-452c-944a-c77d2e971db2", new TornadoHistoryProject());
		this.sourceMap.put("2fb6b1b3-89f3-445f-9ffb-e0e5f9afcd94", new AtmosphereWMO());
		this.sourceMap.put("671b1a22-53e4-47b1-b148-5ba83420b0cd", new PDONOAA());
		this.sourceMap.put("162baa09-9ad1-4556-9a9f-a967ee37e514", new ENSONOAA());
		this.sourceMap.put("49c5c583-3ab3-4b71-b655-7c63f79cd19e", new SAMNOAA());
		this.sourceMap.put("c6cda394-0243-4ed5-a6db-9add2a837490", new IODNOAA());
		this.sourceMap.put("9002e9df-10c9-417a-a12e-c29da659577b", new AMONOAA());
		this.sourceMap.put("36e6219d-4867-4c94-84ba-538fd40e63e1", new ElNino12NOAA());
		this.sourceMap.put("19cf287a-5d24-4f60-9dd7-e5c2a545a1a5", new ElNino3NOAA());
		this.sourceMap.put("1973a943-de01-46c2-b376-60384645bea8", new ElNino34NOAA());
		this.sourceMap.put("8f2bc89d-7ba3-46b2-9dde-7717d992a61e", new ElNino4NOAA());
		this.sourceMap.put("c983e688-0e94-405f-a513-7565c6e13b03", new MagneticNOAAWMM());
		this.sourceMap.put("ecb98f29-fc40-4025-ab0e-24faeaa39d5e", new SILSOSunspot());
		this.sourceMap.put("8b2215c6-945b-4109-bfb4-6c764636e390", new SeaIceToth());
		this.sourceMap.put("25742cae-1bf4-11ed-861d-0242ac120002", new MeanSeaLevelPressureERA5());
		this.sourceMap.put("80607606-1671-4f9f-967b-db7f59e87b81", new GeopotentialERA5());
		this.sourceMap.put("aefbd8d1-d423-458d-90c0-7c8429f2a653", new GOESNOAA());
		this.sourceMap.put("316bab36-ac3b-4930-87ae-5a32e4cdb81c", new VorticityERAInterim());
		this.sourceMap.put("e7e20f49-2387-40ce-917f-5b592c0b8b67", new VorticityERA5());
		this.sourceMap.put("9c51699e-d185-4469-a38a-08ca02b88931", new UTNTrack());
		this.start();
	}

	public void setBasePath(String basePath) {
		logger.info("setBasePath(" + basePath + ")");
		this.basePath = basePath;
		for (Entry<String, Source> entry : this.sourceMap.entrySet()) {
			Source source = entry.getValue();
			source.setBasePath(basePath);
		}
	}

	@Override
	protected void defaultState(Object object) {
		if (object instanceof Query) {
			Query query = (Query) object;
			this.query(query);
		}
	}

	protected void query(Query query) {
		Object object = this.sourceMap.get(query.getSourceUUID());
		if (object instanceof Source) {
			Source source = (Source) object;
			if (source.getStart()) {
				source.start();
			} else {
				source.interrupt();
			}
			source.add(query);
		}
	}
}
//this.sourceMap.put("8edb8e7d-d0e1-4204-ac2f-12c456f0a1b1", new OceanERAInterim());
//public static void main(String[] args) {
////List<Object> queue = new ArrayList<>();
////Data data = new Data();
////Query query = new Query();
////query.outputList = queue;
////query.map.put("time","2001");
////query.sourceUUID = "281cbf52-7014-4229-bffd-35c8ba41bcb5";
////data.add(query);
////data.setDelay(data.newDelay(10));
////boolean flag = true;
////while(flag) {
////Object o = (queue.size()>0)?queue.remove(0):null;
////if(data.delayExpired()) {
////	query = new Query();
////	query.outputList = queue;
////	query.map.put("time","2005");
////	query.sourceUUID = "281cbf52-7014-4229-bffd-35c8ba41bcb5";
////	data.add(query);
////	flag = false;
////}
////}
////System.out.println("Complete");
//}
//public void interrupt() {
//logger.warn("interrupt()");
//this.thread.interrupt();
//synchronized (this.source) {
//this.source.notify();
//}
//logger.info(this.thread.isInterrupted());
//}
//source.stop();
//source.start();
//try {
//
//} catch (InterruptedException e) {
//logger.warn("InterruptedException e="+e.getMessage());
//} catch (Exception e) {
//// TODO Auto-generated catch block
//e.printStackTrace();
//}
//public synchronized void load(String uuid) {
//if (!this.sourceList.contains(uuid)) {
//	System.out.println("load(" + uuid + ")");
//	this.sourceList.add(uuid);
//}
//}

//public synchronized void unload(String uuid) {
//Iterator<String> iterator = this.sourceList.iterator();
//while (iterator.hasNext()) {
//	String load = iterator.next();
//	if (load.equals(uuid)) {
//		iterator.remove();
//	}
//}
//}

//public void setCalendar(Calendar calendar) {
//this.calendar = calendar;
//}

//public void query(Query query) throws Exception {
//this.add(query);
//}
//
//public Object get(String uuid, Calendar calendar) throws Exception {
//logger.info("get("+uuid+","+calendar+")");
//Source source = (Source) this.sourceMap.get(uuid);
//return (source != null) ? source.get(calendar) : null;
//}

//public void load(String key, Calendar calendar) {
//if (key != null) {
//	Source source = (Source) this.sourceMap.get(key);
//	if (calendar != null && source.calendarFlag) {
//		Object object = source.get(calendar);
////		this.map.put(key, object);
//		if (this.forward) {
//			Calendar c = (Calendar) calendar.clone();
//			c.add(Calendar.MONTH, 1);
//			source.get(c);
//		} else if (this.backward) {
//			Calendar c = (Calendar) calendar.clone();
//			c.add(Calendar.MONTH, -1);
//			source.get(c);
//		}
//	} 
////	else {
////		Object object = this.map.get(key);
////		if (object == null) {
////			object = source.get();
////			if (object != null)
////				this.map.put(key, object);
////		}
////	}
//}
//}

//public void passive() {
//Iterator<String> iterator = new ArrayList<>(this.sourceList).iterator();
//while (iterator.hasNext()) {
//	String key = iterator.next();
//	this.load(key, this.calendar);
//}
//}

//@Override
//public void run() {
//while (true) {
//	this.passive();
//	try {
//		Thread.sleep(this.delay);
//	} catch (Exception e) {
//		e.printStackTrace();
//	}
//}
//}
//public Map<String, Object> map = new HashMap<String, Object>();// does not create a new instance, holds a reference,
// this is acceptable
//public Calendar calendar;
//public boolean forward;
//public boolean backward;
//public List<String> sourceList = Collections.synchronizedList(new ArrayList<>());
//public Thread thread;
//public int delay = 500;
//return (source != null) ? source.get(query, this.map.get(uuid)) : null;
//if(key != null) {
//Source source = (Source)this.sourceMap.get(key);
//if(source.calendarFlag) {
//	source.load(this.calendar);
//	Object object = source.get(this.calendar);
//	this.map.put(key,object);
//	if(this.forward) {
//		Calendar calendar = (Calendar)this.calendar.clone();
//		calendar.add(Calendar.MONTH, 1);
//		source.load(calendar);
//	} else if(this.backward) {
//		Calendar calendar = (Calendar)this.calendar.clone();
//		calendar.add(Calendar.MONTH, -1);
//		source.load(calendar);
//	}
//} else {
//	if(!source.loaded) {
//		source.load();
//	}
//	Object object = source.get();
//	if(object != null)
//		this.map.put(key,object);
//	try {
//		Thread.sleep(this.delay);
//	} catch(Exception e) {
//		e.printStackTrace();
//	}
//}
//}
