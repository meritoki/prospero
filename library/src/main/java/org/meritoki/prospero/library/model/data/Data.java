package org.meritoki.prospero.library.model.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.data.source.AtmosphereWMO;
import org.meritoki.prospero.library.model.data.source.CityNaturalEarth;
import org.meritoki.prospero.library.model.data.source.CountryNaturalEarth;
import org.meritoki.prospero.library.model.data.source.CycloneUTNERA5;
import org.meritoki.prospero.library.model.data.source.CycloneUTNERAInterim;
import org.meritoki.prospero.library.model.data.source.ENSONOAA;
import org.meritoki.prospero.library.model.data.source.EarthquakeUSGSEarthquakeHazardProgram;
import org.meritoki.prospero.library.model.data.source.LithosphereGEBCO;
import org.meritoki.prospero.library.model.data.source.MagneticNOAAEMAG;
import org.meritoki.prospero.library.model.data.source.MagneticNOAAWMM;
import org.meritoki.prospero.library.model.data.source.OceanERA5SeaLevelPressure;
import org.meritoki.prospero.library.model.data.source.OceanERA5ZeroTwoFive;
import org.meritoki.prospero.library.model.data.source.OceanERAInterim;
import org.meritoki.prospero.library.model.data.source.OceanERAInterimZeroTwoFive;
import org.meritoki.prospero.library.model.data.source.OceanSeaIce;
import org.meritoki.prospero.library.model.data.source.PDONOAA;
import org.meritoki.prospero.library.model.data.source.SILSOSunspot;
import org.meritoki.prospero.library.model.data.source.Source;
import org.meritoki.prospero.library.model.data.source.TectonicPeterBird;
import org.meritoki.prospero.library.model.data.source.TornadoHistoryProject;
import org.meritoki.prospero.library.model.data.source.VolcanicNOAA;
import org.meritoki.prospero.library.model.data.source.WindERAInterim;
import org.meritoki.prospero.library.model.data.source.WindJetstreamERA5;
import org.meritoki.prospero.library.model.query.Query;

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

	static Logger logger = LogManager.getLogger(Data.class.getName());
	public Map<String, Source> sourceMap = new HashMap<String, Source>();
	
	public Data() {
		super("Data");
		this.filter = false;
		this.sourceMap.put("2d611935-9786-4c28-9dcf-f18cf3e99a3a", new CycloneUTNERAInterim());
		this.sourceMap.put("281cbf52-7014-4229-bffd-35c8ba41bcb5", new CycloneUTNERA5());
		this.sourceMap.put("f4d6ead6-949a-42a9-9327-a8e22790e0e7", new WindERAInterim());
		this.sourceMap.put("73428541-23ea-4c4a-bc82-4fb4db5afe05", new WindJetstreamERA5());
		this.sourceMap.put("d6eb88d6-100c-4948-8fd1-5300b724ec2d", new CountryNaturalEarth());
		this.sourceMap.put("9bc2dd83-85c9-48fe-818f-f62db97c594a", new CityNaturalEarth());
		this.sourceMap.put("8edb8e7d-d0e1-4204-ac2f-12c456f0a1b1", new OceanERAInterim());
		this.sourceMap.put("f4f4b169-fdc4-44db-b93a-85f8416aec2c", new OceanERAInterimZeroTwoFive());
		this.sourceMap.put("e0538d57-044d-48c2-b3a7-b985eada81fc", new OceanERA5ZeroTwoFive());
		this.sourceMap.put("7cfb5b0a-0f8a-4e38-b0a9-5d50bf64a7b5", new EarthquakeUSGSEarthquakeHazardProgram());
		this.sourceMap.put("3580b76c-b70d-4cdd-9a80-4feb57c72c77", new MagneticNOAAEMAG());
		this.sourceMap.put("1aac29c0-e2f6-45e8-9921-c88397957795", new LithosphereGEBCO());
		this.sourceMap.put("8f6ef7b8-b8d1-452c-944a-c77d2e971db2", new TectonicPeterBird());
		this.sourceMap.put("495ae7cd-9781-4b56-a2d0-cd3f6b2e80e1", new VolcanicNOAA());
		this.sourceMap.put("ff6e87b8-b8d1-452c-944a-c77d2e971db2", new TornadoHistoryProject());
		this.sourceMap.put("2fb6b1b3-89f3-445f-9ffb-e0e5f9afcd94", new AtmosphereWMO());
		this.sourceMap.put("671b1a22-53e4-47b1-b148-5ba83420b0cd", new PDONOAA());
		this.sourceMap.put("162baa09-9ad1-4556-9a9f-a967ee37e514", new ENSONOAA());
		this.sourceMap.put("c983e688-0e94-405f-a513-7565c6e13b03", new MagneticNOAAWMM());
		this.sourceMap.put("ecb98f29-fc40-4025-ab0e-24faeaa39d5e", new SILSOSunspot());
		this.sourceMap.put("8b2215c6-945b-4109-bfb4-6c764636e390", new OceanSeaIce());
		this.sourceMap.put("25742cae-1bf4-11ed-861d-0242ac120002", new OceanERA5SeaLevelPressure());
		this.start();
	}
	
	@Override
	protected void defaultState(Object object) {
		if(object instanceof Query) {
			Query query = (Query)object;
			this.query(query);
		}
	}
	
	protected void query(Query query) {
		Object object = this.sourceMap.get(query.getSourceUUID());
		if(object instanceof Source) {
			Source source = (Source)object; 
			if(source.getStart()) {
				source.start();
			} else {
				source.interrupt();
			}
			source.add(query);
		}
	}
}
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
