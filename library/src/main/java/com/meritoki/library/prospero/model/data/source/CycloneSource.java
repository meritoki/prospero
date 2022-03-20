package com.meritoki.library.prospero.model.data.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.meritoki.library.prospero.model.query.Query;
import com.meritoki.library.prospero.model.terra.atmosphere.cyclone.unit.Classification;
import com.meritoki.library.prospero.model.terra.atmosphere.cyclone.unit.CycloneEvent;
import com.meritoki.library.prospero.model.unit.Count;
import com.meritoki.library.prospero.model.unit.Event;
import com.meritoki.library.prospero.model.unit.Index;
import com.meritoki.library.prospero.model.unit.Interval;
import com.meritoki.library.prospero.model.unit.Mode;
import com.meritoki.library.prospero.model.unit.Result;
import com.meritoki.library.prospero.model.unit.Time;

public class CycloneSource extends Source {

	static Logger logger = LogManager.getLogger(CycloneSource.class.getName());
	protected Map<String, List<Event>> eventMap = new HashMap<>();
	public LinkedList<String> eventQueue = new LinkedList<>();
	public List<Index> indexList = new ArrayList<>();
//	public List<Event> eventList;
	public String order = "tb";
	public Count count = new Count('>', 1);
	public int[] levelArray;
	public boolean single = false;
	public int cacheSize = 8;
	public boolean test = true;

	public CycloneSource() {
		this.calendarFlag = true;
	}

	@Override
	public void query(Query query) throws Exception {
//		logger.info("query(" + query + ")");
		this.intervalList = query.getIntervalList(this.getStartYear(), this.getEndYear());
		if (this.intervalList != null) {
			for (Interval i : this.intervalList) {
				this.load(query, i);
			}
			query.objectListAdd(new Result(Mode.COMPLETE));
			this.eventMap = null;
		}
	}

	public int[] getLevelArray() {
		return this.levelArray;
	}
	
	public void load(Query query, Interval interval) throws Exception {
		List<Time> timeList = Time.getTimeList(interval);
		List<Event> bufferList = null;
		List<Event> loadList;
		for(Time time: timeList) {
			if (!Thread.interrupted()) {
				loadList = this.read(time.year, time.month);
				Result result = new Result();
				result.map.put("time", time);
				result.map.put("eventList", new ArrayList<Event>((loadList)));
				query.outputList.add(result);
			} else {
				throw new InterruptedException();
			}
		}
	}

//	/**
//	 * Original function works with or without startYear and endYear, set to -1.
//	 * This is the problem for Month and Alias queries there is nothing that says
//	 * what year to start and stop. When the data does not provide these delimeters
//	 * the query is technically not correct. Therefore the solution is to require
//	 * the startYear and endYear and fix the logic to also support month and alias
//	 * queries.
//	 * 
//	 * @param i
//	 * @return
//	 * @throws Exception
//	 */
//	public void load(Query query, Interval i) throws Exception {
//		logger.info("load(query, " + i + ")");
//		List<Event> eventList = new ArrayList<>();
//		int startYear = (i.startYear == -1) ? this.getStartYear() : i.startYear;
//		int startMonth = (i.startMonth == -1) ? 1 : i.startMonth;
//		int endYear = (i.endYear == -1) ? this.getEndYear() : i.endYear;
//		int endMonth = (i.endMonth == -1) ? 12 : i.endMonth;
//		List<Event> bufferList = null;
//		List<Event> loadList;
//		if (i.allFlag) {
//			logger.info("load(...) allFlag=" + i.allFlag);
//			List<Event> eList = new ArrayList<>();
//			for (int y = startYear; y <= endYear; y++) {
//				if (!Thread.interrupted()) {
//					for (int m = startMonth; m <= endMonth; m++) {
//						if (!Thread.interrupted()) {
//							if (this.single) {
//								loadList = this.eventMapGet(y, m);
//								if (!test) {
//									loadList = (bufferList != null) ? this.getSingleCountList(bufferList, loadList)
//											: this.getSingleCountList(eventList, loadList);
//									bufferList = loadList;
//								}
//								eList.addAll(loadList);
//							} else {
//								loadList = this.eventMapGet(y, m);
//								eList.addAll(loadList);
//							}
//						} else {
//							throw new InterruptedException();
//						}
//					}
//				} else {
//					throw new InterruptedException();
//				}
//			}
//			Result result = new Result();
//			result.map.put("eventList", eList);
//			query.objectListAdd(result);
//		} else if (i.startYear != -1 && i.endYear != -1) {
//			if (this.getStartYear() <= i.startYear && i.endYear <= this.getEndYear()) {
//				int yearDifference = endYear - startYear - 1;
//				if (yearDifference == -1) { // easiest case same year just iterate over months and done
//					// same year
//					for (int y = startYear; y <= endYear; y++) {
//						if (!Thread.interrupted()) {
//							for (int m = startMonth; m <= endMonth; m++) {
//								if (!Thread.interrupted()) {
//									if (this.single) {
//										loadList = this.eventMapGet(y, m);
//										loadList = (bufferList != null) ? this.getSingleCountList(bufferList, loadList)
//												: this.getSingleCountList(eventList, loadList);
//										// eventList.addAll(loadList);
//										Result result = new Result();
//										result.map.put("eventList", new ArrayList<Event>((loadList)));
//										query.outputList.add(result);
//										bufferList = loadList;
//									} else {
//										loadList = this.eventMapGet(y, m);
//										Result result = new Result();
//										result.map.put("eventList", new ArrayList<Event>((loadList)));
//										query.outputList.add(result);
//										// eventList.addAll();
//									}
//								} else {
//									throw new InterruptedException();
//								}
//							}
//						} else {
//							throw new InterruptedException();
//						}
//					}
//				} else {
//					// different years
//					// all range, season, month queries end up here
//					// in order for the single count method to work for DJF,
//					// we have to read DJF in order.
//					if (i.seasonFlag || i.monthFlag) {
//						if (startMonth <= endMonth) {
//							for (int y = startYear; y <= endYear; y++) {
//								if (!Thread.interrupted()) {
//									for (int m = startMonth; m <= endMonth; m++) {
//										if (!Thread.interrupted()) {
//											if (this.single) {
//												loadList = this.eventMapGet(y, m);
//												loadList = (bufferList != null)
//														? this.getSingleCountList(bufferList, loadList)
//														: this.getSingleCountList(eventList, loadList);
//												// eventList.addAll(loadList);
//												Result result = new Result();
//												result.map.put("eventList", new ArrayList<Event>((loadList)));
//												query.outputList.add(result);
//												bufferList = loadList;
//											} else {
//												// eventList.addAll(this.eventMapGet(y, m));
//												loadList = this.eventMapGet(y, m);
//												Result result = new Result();
//												result.map.put("eventList", new ArrayList<Event>((loadList)));
//												query.outputList.add(result);
//											}
//										} else {
//											throw new InterruptedException();
//										}
//
//									}
//								} else {
//									throw new InterruptedException();
//								}
//							}
//						} else {
//							for (int y = startYear; y <= endYear; y++) {
//								if (!Thread.interrupted()) {
//									for (int m = 1; m <= endMonth; m++) {
//										if (!Thread.interrupted()) {
//											if (this.single) {
//												loadList = this.eventMapGet(y, m);
//												loadList = (bufferList != null)
//														? this.getSingleCountList(bufferList, loadList)
//														: this.getSingleCountList(eventList, loadList);
//												// eventList.addAll(loadList);
//												Result result = new Result();
//												result.map.put("eventList", new ArrayList<Event>((loadList)));
//												query.outputList.add(result);
//												bufferList = loadList;
//											} else {
//												// eventList.addAll(this.eventMapGet(y, m));
//												loadList = this.eventMapGet(y, m);
//												Result result = new Result();
//												result.map.put("eventList", new ArrayList<Event>((loadList)));
//												query.outputList.add(result);
//											}
//										} else {
//											throw new InterruptedException();
//										}
//									}
//									for (int m = startMonth; m <= 12; m++) {
//										if (!Thread.interrupted()) {
//											if (this.single) {
//												loadList = this.eventMapGet(y, m);
//												loadList = (bufferList != null)
//														? this.getSingleCountList(bufferList, loadList)
//														: this.getSingleCountList(eventList, loadList);
//												// eventList.addAll(loadList);
//												Result result = new Result();
//												result.map.put("eventList", new ArrayList<Event>((loadList)));
//												query.outputList.add(result);
//												bufferList = loadList;
//											} else {
//												// eventList.addAll(this.eventMapGet(y, m));
//												loadList = this.eventMapGet(y, m);
//												Result result = new Result();
//												result.map.put("eventList", new ArrayList<Event>((loadList)));
//												query.outputList.add(result);
//											}
//										} else {
//											throw new InterruptedException();
//										}
//									}
//								} else {
//									throw new InterruptedException();
//								}
//							}
//						}
//					} else {
//						// this code does not work, it may have never worked
//						// update looks like this code works and Interval contains point is failing
//						for (int m = startMonth; m <= 12; m++) {
//							if (!Thread.interrupted()) {
//								if (this.single) {
//									loadList = this.eventMapGet(startYear, m);
//									loadList = (bufferList != null) ? this.getSingleCountList(bufferList, loadList)
//											: this.getSingleCountList(eventList, loadList);
//									// eventList.addAll(loadList);
//									Result result = new Result();
//									result.map.put("eventList", new ArrayList<Event>((loadList)));
//									query.outputList.add(result);
//									bufferList = loadList;
//								} else {
//									// eventList.addAll(this.eventMapGet(startYear, m));
//									loadList = this.eventMapGet(startYear, m);
//									Result result = new Result();
//									result.map.put("eventList", new ArrayList<Event>((loadList)));
//									query.outputList.add(result);
//								}
//							} else {
//								throw new InterruptedException();
//							}
//						}
//						for (int y = startYear + 1; y <= endYear - 1; y++) {// skipped is years are consecutive
//							if (!Thread.interrupted()) {
//								for (int m = 1; m <= 12; m++) {
//									if (!Thread.interrupted()) {
//										if (this.single) {
//											loadList = this.eventMapGet(y, m);
//											loadList = (bufferList != null)
//													? this.getSingleCountList(bufferList, loadList)
//													: this.getSingleCountList(eventList, loadList);
//											// eventList.addAll(loadList);
//											Result result = new Result();
//											result.map.put("eventList", new ArrayList<Event>((loadList)));
//											query.outputList.add(result);
//											bufferList = loadList;
//										} else {
//											// eventList.addAll(this.eventMapGet(y, m));
//											loadList = this.eventMapGet(y, m);
//											Result result = new Result();
//											result.map.put("eventList", new ArrayList<Event>((loadList)));
//											query.outputList.add(result);
//										}
//									} else {
//										throw new InterruptedException();
//									}
//								}
//							} else {
//								throw new InterruptedException();
//							}
//						}
//						for (int m = 1; m <= endMonth; m++) {
//							if (!Thread.interrupted()) {
//								if (this.single) {
//									loadList = this.eventMapGet(endYear, m);
//									loadList = (bufferList != null) ? this.getSingleCountList(bufferList, loadList)
//											: this.getSingleCountList(eventList, loadList);
//									// eventList.addAll(loadList);
//									Result result = new Result();
//									result.map.put("eventList", new ArrayList<Event>((loadList)));
//									query.outputList.add(result);
//									bufferList = loadList;
//								} else {
//									// eventList.addAll(this.eventMapGet(endYear, m));
//									loadList = this.eventMapGet(endYear, m);
//									Result result = new Result();
//									result.map.put("eventList", new ArrayList<Event>((loadList)));
//									query.outputList.add(result);
//								}
//							} else {
//								throw new InterruptedException();
//							}
//						}
//					}
//				}
//			} else {
//				throw new Exception(
//						"invalid interval, valid time between " + this.getStartYear() + " and " + this.getEndYear());
//			}
//		} else {
//			throw new Exception("invalid interval, start and end year initialized");
//		}
//		// return eventList;
//	}

//	public List<Event> filter(Query query, List<Event> eventList) throws Exception {// List<Event> eventList) {
////		logger.info("filter(" + query + ", " + eventList.size() + ") Thread.interrupted()="+Thread.interrupted());
////		logger.info("filter(..., " + eventList.size() + ") Thread.interrupted()="+Thread.interrupted());
////		logger.info("filter(..., " + eventList.size() + ") this.thread.isInterrupted()="+this.thread.isInterrupted());
//		if (!Thread.interrupted()) {
////			this.resetFlags(eventList);
////			this.durationList = query.getDurationList();
////			this.dimension = query.getDimension();
////			this.count = this.getCount(query.map.get("count"));
////			this.familyList = this.getFamilyList(query.map.get("family"));
////			this.classificationList = this.getClassificationList(query.map.get("classification"));
////			this.levelList = this.getLevelList(query.getPressure(), this.getLevelArray());
//			boolean intervalFlag = false;
//			if (eventList != null) {
//				for (Event e : eventList) {
//					for (Coordinate c : e.coordinateList) {
//						intervalFlag = false;
//						if (this.intervalList != null && this.intervalList.size() > 0) {
//							for (Interval i : this.intervalList) {
//								if (i.contains(c)) {
//									intervalFlag = true;
//									e.flag = true;
//									break;
//								}
//							}
//						} else {
//							intervalFlag = true;
//						}
//						c.flag = intervalFlag;
//					}
//				}
//				Iterator<Event> it = eventList.iterator();
//				while (it.hasNext()) {
//					Event e = it.next();
//					if (!e.flag || !e.hasCoordinate())
//						it.remove();
//				}
//			}
//			if (eventList.size() == 0) {
//				logger.warn("filter("+query + ", " + eventList.size() +") zero");
//			}
//		} else {
//			throw new InterruptedException();
//		}
//		return eventList;
//	}

//	public List<Event> filter(Query query, List<Event> eventList) throws Exception {// List<Event> eventList) {
////		logger.info("filter(" + query + ", " + eventList.size() + ") Thread.interrupted()="+Thread.interrupted());
////		logger.info("filter(..., " + eventList.size() + ") Thread.interrupted()="+Thread.interrupted());
////		logger.info("filter(..., " + eventList.size() + ") this.thread.isInterrupted()="+this.thread.isInterrupted());
//		if (!Thread.interrupted()) {
//			this.resetFlags(eventList);
//			this.regionList = query.getRegionList();
//			this.durationList = query.getDurationList();
//			this.dimension = query.getDimension();
//			this.count = this.getCount(query.map.get("count"));
//			this.familyList = this.getFamilyList(query.map.get("family"));
//			this.classificationList = this.getClassificationList(query.map.get("classification"));
//			this.levelList = this.getLevelList(query.getPressure(), this.getLevelArray());
//			boolean intervalFlag = false;
//			boolean levelFlag = false;
//			boolean regionFlag = false;
//			boolean durationFlag = false;
//			boolean familyFlag = false;
//			boolean classFlag = false;
//			if (eventList != null) {
//				for (Event e : eventList) {
//					durationFlag = false;
//					familyFlag = false;
//					classFlag = false;
//					for (Coordinate c : e.coordinateList) {
//						intervalFlag = false;
//						levelFlag = false;
//						regionFlag = false;
//						if (this.intervalList != null && this.intervalList.size() > 0) {
//							for (Interval i : this.intervalList) {
//								if (i.contains(c)) {
//									intervalFlag = true;
//									break;
//								}
//							}
//						} else {
//							intervalFlag = true;
//						}
//						if (this.levelList != null && this.levelList.size() > 0) {
//							for (Integer l : this.levelList) {
//								int level = (int) c.attribute.get("pressure");
//								if (l == level) {
//									levelFlag = true;
//								}
//							}
//						} else {
//							levelFlag = true;
//						}
//						if (this.regionList != null && this.regionList.size() > 0) {
//							for (Region r : this.regionList) {
//								if (r.contains(c)) {
//									regionFlag = true;
//								}
//							}
//						} else {
//							regionFlag = true;
//						}
//						c.flag = intervalFlag && levelFlag && regionFlag;
//					}
//					if (this.durationList != null && this.durationList.size() > 0) {
//						for (Duration d : this.durationList) {
//							if (d.contains(e.getDuration())) {
//								durationFlag = true;
//								break;
//							}
//						}
//					} else {
//						durationFlag = true;
//					}
//					if (this.familyList != null && this.familyList.size() > 0) {
//						for (Family depth : this.familyList) {
//							if (((CycloneEvent) e).family != null && depth == ((CycloneEvent) e).family) {
//								familyFlag = true;
//								break;
//							}
//						}
//					} else {
//						familyFlag = true;
//					}
//
//					if (this.classificationList != null && this.classificationList.size() > 0) {
//						for (Classification type : classificationList) {
//							if (type == ((CycloneEvent) e).classification) {
//								classFlag = true;
//								break;
//							}
//						}
//					} else {
//						classFlag = true;
//					}
//					e.flag = durationFlag && familyFlag && classFlag;
//				}
//				Iterator<Event> it = eventList.iterator();
//				while (it.hasNext()) {
//					Event e = it.next();
//					if (!e.flag || !e.hasCoordinate())
//						it.remove();
//				}
//			}
//			if (eventList.size() == 0) {
//				logger.warn("filter("+query + ", " + eventList.size() +") zero");
//			}
//		} else {
//			throw new InterruptedException();
//		}
//		return eventList;
//	}

	public List<Event> eventMapGet(int y, int m) throws Exception {
//		logger.info("eventMapGet(" + y + ", " + m + ")");
		if (this.eventMap == null)
			this.eventMap = new HashMap<>();
		String key = y + "" + m;
		List<Event> eList = this.eventMap.get(key);
		if (eList == null) {
			eList = this.read(y, m);
			if (eList != null) {
				this.eventMap.put(key, eList);
				this.eventQueue.addLast(key);
				this.clearEventMap();
			} else {
				eList = new ArrayList<>();
			}
		}
		eList = new ArrayList<>(eList);
//		this.indexList.add(this.getIndex(y, m, eList));
		return eList;
	}

	public void clearEventMap() {
		while (this.eventQueue.size() > this.cacheSize) {
			String key = this.eventQueue.remove(0);
			this.eventMap.remove(key);
		}
	}

	public List<Event> read(int year, int month) throws Exception {
		return null;
	}

	public List<Event> eventMapGet(String time) {
		logger.info("eventMapGet(" + time + ")");
		if (this.eventMap == null)
			this.eventMap = new HashMap<>();
		List<Event> eList = this.eventMap.get(time);
		if (eList != null) {
			eList = new ArrayList<>(eList);
		}
		return eList;
	}

//	public void resetFlags(List<Event> eventList) {
//		for (Event e : eventList) {
//			e.flag = false;
//			for (Coordinate p : e.coordinateList) {
//				p.flag = false;
//			}
//		}
//	}

//	public List<Family> getFamilyList(String family) {
//		List<Family> familyList = new ArrayList<>();
//		if (family != null) {
//			String[] array = family.split(",");
//			for (String s : array) {
//				if (!s.isEmpty()) {
//					s = s.toUpperCase();
//					Family f = Family.valueOf(s);
//					familyList.add(f);
//				}
//			}
//		}
//		return familyList;
//	}

	public List<Classification> getClassificationList(String classification) {
		List<Classification> classificationList = new ArrayList<>();
		if (classification != null) {
			String[] array = classification.split(",");
			for (String s : array) {
				if (!s.isEmpty()) {
					s = s.toUpperCase();
					Classification f = Classification.valueOf(s);
					classificationList.add(f);
				}
			}
		}
		return classificationList;
	}

	public List<CycloneEvent> getCountEventList(List<CycloneEvent> eventList) {
		Iterator<CycloneEvent> eventIterator = eventList.iterator();
		while (eventIterator.hasNext()) {
			CycloneEvent event = eventIterator.next();
			switch (this.count.operator) {
			case '>': {
				if (event.getPressureCount() <= this.count.value) {
					eventIterator.remove();
				}
				break;
			}
			case '<': {
				if (event.getPressureCount() >= this.count.value) {
					eventIterator.remove();
				}
				break;
			}
			case '=': {
				if (event.getPressureCount() < this.count.value || event.getPressureCount() > this.count.value) {
					eventIterator.remove();
				}
				break;
			}
			}
		}
		return eventList;
	}

	public List<Integer> getLevelList(String level, int[] levelArray) throws Exception {
		List<Integer> levelList = null;
		if (level != null && !level.isEmpty()) {
			String[] commaArray = level.split(",");
			String[] dashArray;
			boolean valid = true;
			List<Object> list = new ArrayList<>();
			if (level.contains(",")) {
				for (String c : commaArray) {
					if (c.contains("-")) {
						if (c.lastIndexOf('-') == c.indexOf('-') && c.indexOf('-') != 0
								&& c.indexOf('-') != c.length() - 1) {
							dashArray = c.split("-");
							String[] range = new String[2];
							String d = dashArray[0];
							range[0] = d;
							valid = (valid) ? this.isValidLevel(d, levelArray) : valid;
							d = dashArray[1];
							valid = (valid) ? this.isValidLevel(d, levelArray) : valid;
							range[1] = d;
							list.add(range);
						} else {
							valid = false;
						}
					} else {
						valid = (valid) ? this.isValidLevel(c, levelArray) : valid;
						list.add(c);
					}
				}
			} else if (level.contains("-")) {
				if (level.lastIndexOf('-') == level.indexOf('-') && level.indexOf('-') != 0
						&& level.indexOf('-') != level.length() - 1) {
					dashArray = level.split("-");
					String[] range = new String[2];
					String d = dashArray[0];
					range[0] = d;
					valid = (valid) ? this.isValidLevel(d, levelArray) : valid;
					d = dashArray[1];
					valid = (valid) ? this.isValidLevel(d, levelArray) : valid;
					range[1] = d;
					list.add(range);
				} else {
					valid = false;
				}
			} else {
				valid = (valid) ? this.isValidLevel(level, levelArray) : valid;
				list.add(level);
			}

			if (valid) {
				levelList = new ArrayList<>();
				for (Object o : list) {
					if (o instanceof String) {
						String s = (String) o;
						Integer integer = Integer.parseInt(s);
						levelList.add(integer);
					} else if (o instanceof String[]) {
						String[] range = (String[]) o;
						int a = Integer.parseInt(range[0]);
						int b = Integer.parseInt(range[1]);
						for (Integer l : levelArray) {
							if (a <= l && l <= b) {
								levelList.add(l);
							}
						}
					}
				}
			} else {
				throw new Exception("invalid level format: " + level);
			}
		}
		return levelList;
	}

	// @Override
//	public double getDimension(String dimension) throws Exception {
//		int d = 1;
//		if (dimension != null && !dimension.isEmpty()) {
//			boolean valid = true;
//			try {
//				d = Integer.parseInt(dimension);
//			} catch (NumberFormatException e) {
//				valid = false;
//			}
//			if (d <= 0) {
//				valid = false;
//			}
//			if (!valid) {
//				throw new Exception("invalid dimension format: " + dimension);
//			}
//		}
//		return d;
//	}

	public List<Event> getSingleCountList(List<Event> referenceList, List<Event> newList) throws Exception {
		logger.debug("getSingleCountList(" + referenceList.size() + "," + newList.size() + ")");
		int count = 0;
		if (newList != null) {
			for (Event a : referenceList) {
				if (!Thread.interrupted()) {
					Iterator<Event> newIterator = newList.iterator();
					while (newIterator.hasNext()) {
						if (!Thread.interrupted()) {
							Event newEvent = newIterator.next();
							if (((CycloneEvent) a).isSimilar((CycloneEvent) newEvent, 1)) {
								newIterator.remove();
								count++;
							}
						} else {
							throw new InterruptedException();
						}
					}
				} else {
					throw new InterruptedException();
				}
			}
		}
		logger.debug("getSingleCountList(" + referenceList.size() + "," + newList.size() + ") count=" + count);
		return newList;
	}

	public List<CycloneEvent> getSingleCountEventList(List<CycloneEvent> eventList) throws Exception {
		logger.info("getSingleCountList(" + eventList + ")");
		if (eventList != null) {
			List<CycloneEvent> copyEventList = new ArrayList<>(eventList);// eventList.stream().collect(Collectors.toList());
			for (CycloneEvent a : copyEventList) {
				if (!Thread.interrupted()) {
					Iterator<CycloneEvent> bIterator = eventList.iterator();
					while (bIterator.hasNext()) {
						CycloneEvent b = bIterator.next();
						if (!a.equals(b) && a.isSimilar(b, 1)) {
							bIterator.remove();
						}
					}
				} else {
					throw new InterruptedException();
				}
			}
		}
//	logger.info("getSingleCountList("+listA+","+listB+") listB="+listB);
		return eventList;
	}

	public boolean isValidLevel(String value, int[] levelArray) {
		boolean flag = false;
		if (value != null) {
			try {
				int level = Integer.parseInt(value);
				for (Integer i : levelArray) {
					if (i == level) {
						flag = true;
						break;
					}
				}
			} catch (NumberFormatException e) {
				flag = false;
			}
		}
		return flag;
	}

}
//else if (object != null) {
//if(object instanceof Object[]) {
//	Object[] objectArray = (Object[])object;
//	this.eventList = (List<Event>) objectArray[0];
//	this.resetFlags(this.eventList);
//} else {
//	this.eventList = (List<Event>) object;
//	this.resetFlags(this.eventList);
//}
//}
//this.eventList = this.eventMapGet(query.getTime());
//if (this.eventList == null) {
//	this.eventList = new ArrayList<>();
//	
//	this.eventList = this.eventList.stream().distinct().collect(Collectors.toList());
//	this.eventMap.put(query.getTime(), eventList);
//}
//this.resetFlags(this.eventList);
//public Index getIndex(int year, int month, List<Event> eventList) {
//Index index =null;
//if(eventList != null) {
//	index = new Index();
//	index.startCalendar = new GregorianCalendar(year,month-1,1,0,0,0);
//	double sum = 0;
//	for(Event e: eventList) {
//		sum += ((CycloneEvent)e).getSpeed();
//	}
//	index.value = sum/eventList.size();
//}
//return index;
//}

//	@Override
//	public double getDimension(String dimension) throws Exception {
//		int d = 1;
//		if (dimension != null && !dimension.isEmpty()) {
//			boolean valid = true;
//			try {
//				d = Integer.parseInt(dimension);
//			} catch (NumberFormatException e) {
//				valid = false;
//			}
//			if (d <= 0) {
//				valid = false;
//			}
//			if (!valid) {
//				throw new Exception("invalid dimension format: " + dimension);
//			}
//		}
//		return d;
//	}
//public List<CycloneEvent> getEventList(Interval i) throws Exception {
//List<CycloneEvent> eventList = new ArrayList<>();
//int startYear = (i.startYear == -1) ? this.getStartYear() : i.startYear;
//int startMonth = (i.startMonth == -1) ? 1 : i.startMonth;
//int endYear = (i.endYear == -1) ? this.getEndYear() : i.endYear;
//int endMonth = (i.endMonth == -1) ? 12 : i.endMonth;
//if (i.startYear != -1 && i.endYear != -1) {
//	if (this.getStartYear() <= i.startYear && i.endYear <= this.getEndYear()) {
//		int yearDifference = endYear - startYear - 1;
//		if (yearDifference == -1) { // easiest case same year just iterate over months and done
//			// same year
//			for (int y = startYear; y <= endYear; y++) {
//				for (int m = startMonth; m <= endMonth; m++) {
//					eventList.addAll(this.eventMapGet(y, m));
//				}
//			}
//		} else {
//			// different years
//			// all range, season, month queries end up here
//			if (i.seasonFlag || i.monthFlag) {
//				if (startMonth <= endMonth) {
//					for (int y = startYear; y <= endYear; y++) {
//						for (int m = startMonth; m <= endMonth; m++) {
//							eventList.addAll(this.eventMapGet(y, m));
//						}
//					}
//				} else {
//					for (int y = startYear; y <= endYear; y++) {
//						for (int m = 1; m <= endMonth; m++) {
//							eventList.addAll(this.eventMapGet(y, m));
//						}
//						for (int m = startMonth; m <= 12; m++) {
//							eventList.addAll(this.eventMapGet(y, m));
//						}
//					}
//				}
//			} else {
//
//				// this code does not work, it may have never worked
//				// update looks like this code works and Interval contains point is failing
//				for (int m = startMonth; m <= 12; m++) {
//					eventList.addAll(this.eventMapGet(startYear, m));
//				}
//				for (int y = startYear + 1; y <= endYear - 1; y++) {// skipped is years are consecutive
//					for (int m = 1; m <= 12; m++) {
//						eventList.addAll(this.eventMapGet(y, m));
//					}
//				}
//				for (int m = 1; m <= endMonth; m++) {
//					eventList.addAll(this.eventMapGet(endYear, m));
//				}
//			}
//		}
//	} else {
//		throw new Exception(
//				"invalid interval, valid time between " + this.getStartYear() + " and " + this.getEndYear());
//	}
//} else {
//	throw new Exception("invalid interval, start and end year initialized");
//}
//return eventList;
//}
//@Override
//public void reset() {
//	super.reset();
//	this.eventList = null;
//	this.durationList = null;
//	this.familyList = null;
//	this.classList = null;
//}
//
//if (intervalList != null) {
//logger.info("DEFECT");
//this.eventList = new ArrayList<>();
//for (Interval i : this.intervalList) {
//	this.eventList.addAll(this.getEventList(i));
//}
//this.eventList = this.eventList.stream().distinct().collect(Collectors.toList());
//this.resetFlags(this.eventList);
//} else if (object != null) {
//if(object instanceof Object[]) {
//Object[] objectArray = (Object[])object;
//this.eventList = (List<Event>) objectArray[0];
//this.resetFlags(this.eventList);
//} else {
//	this.eventList = (List<Event>) object;
//	this.resetFlags(this.eventList);
//}
//}