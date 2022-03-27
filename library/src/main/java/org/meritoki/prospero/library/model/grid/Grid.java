package org.meritoki.prospero.library.model.grid;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.color.Chroma;
import org.meritoki.prospero.library.model.color.Scheme;
import org.meritoki.prospero.library.model.node.Variable;
import org.meritoki.prospero.library.model.plot.Plot;
import org.meritoki.prospero.library.model.terra.analysis.Analysis;
import org.meritoki.prospero.library.model.terra.atmosphere.cyclone.vorticity.Vorticity;
import org.meritoki.prospero.library.model.unit.Band;
import org.meritoki.prospero.library.model.unit.Coordinate;
import org.meritoki.prospero.library.model.unit.Data;
import org.meritoki.prospero.library.model.unit.DataType;
import org.meritoki.prospero.library.model.unit.Event;
import org.meritoki.prospero.library.model.unit.Frame;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.Interval;
import org.meritoki.prospero.library.model.unit.Meter;
import org.meritoki.prospero.library.model.unit.Operator;
import org.meritoki.prospero.library.model.unit.Region;
import org.meritoki.prospero.library.model.unit.Regression;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Series;
import org.meritoki.prospero.library.model.unit.Station;
import org.meritoki.prospero.library.model.unit.Tile;
import org.meritoki.prospero.library.model.unit.Time;

public class Grid extends Variable {

	static Logger logger = LogManager.getLogger(Grid.class.getName());
//	public int monthCount = 12;
	public int latitude = 90;
	public int longitude = 360;
	public int resolution = 1;
	public int[] monthArray;
	public double dimension = 2;
	public double max;
	public double min;
	public Chroma chroma = new Chroma(Scheme.VIRIDIS);
	public int[][][] coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude
			* resolution)][12];
	public float[][] dataMatrix = new float[(int) latitude][(int) longitude];
	public List<Region> regionList = new ArrayList<>();
	public List<Band> bandList = new ArrayList<>();
	public List<Tile> tileList = new ArrayList<>();
	public List<Frame> frameList = new ArrayList<>();
	public List<Coordinate> coordinateList = new ArrayList<>();
	public List<Event> eventList = new ArrayList<>();
	public List<Time> eventTimeList = new ArrayList<>();
	public HashMap<Time, List<Event>> eventMap = new HashMap<>();
	public HashMap<Region, List<Event>> regionMap = new HashMap<>();
	public List<Station> stationList = new ArrayList<>();
	public Map<String, List<Tile>> tileListMap = new TreeMap<>();
	public Map<Integer, List<Band>> bandListMap = new HashMap<>();
	public Map<Integer, int[][][]> pointMatrixMap = new HashMap<>();
	public Map<Integer, Integer> yearMap = new HashMap<>();
	public List<String> dateList = new ArrayList<>();
	public Map<String,Series> seriesMap = new TreeMap<>();
	public List<Plot> plotList = new ArrayList<>();
	public Map<String,Plot> plotMap = new TreeMap<>();
	public List<Index> indexList = new ArrayList<>();

	public int[] range;
	public String regression;
	public boolean average;
	public boolean sum;
	public String season;
	public String group;
	public boolean stackFlag;
	public boolean bandFlag;
	public boolean cubeFlag;
	public boolean monthFlag;
	public boolean yearFlag;
	private boolean print = false;
	private boolean detail = false;
	private Analysis analysis;
	protected Region region;
	protected Double[] meter;
	protected Calendar[] window;

	public Grid(String name) {
		super(name);
	}

	/**
	 * For all primary object representation, re-initialize
	 */
	public void reset() {
		super.reset();
		this.coordinateMatrix = new int[(int) (latitude * resolution)][(int) (longitude * resolution)][12];
		this.dataMatrix = new float[(int) latitude][(int) longitude];
		this.dateList = new ArrayList<>();
		this.regionList = new ArrayList<>();
		this.region = null;
		this.bandList = new ArrayList<>();
		this.tileList = new ArrayList<>();
		this.frameList = new ArrayList<>();
		this.coordinateList = new ArrayList<>();
		this.eventList = new ArrayList<>();
//		this.eventMap = new HashMap<>();
		this.stationList = new ArrayList<>();
//		this.seriesMap = new TreeMap<>();
		this.plotList = new ArrayList<>();
//		this.window = null;
	}

	@Override
	public void load(Result result) {
		super.load(result);
	}
	
	@Override
	public void complete() {
		super.complete();
	}

	/**
	 * Variables that must be reset even if the same Query result can be used
	 */
	@Override
	public void init() {
		super.init();
		try {
			this.regression = this.query.getRegression();
			this.group = this.query.getGroup();
			this.eventMap = new HashMap<>();
			this.analysis = this.query.getAnalysis();
			this.sum = this.query.getSum();
			this.average = this.query.getAverage();
			this.regionList = this.query.getRegionList();
			this.dimension = this.query.getDimension();
			this.meter = this.query.getMeter();
			this.window = this.query.getWindow();
			this.range = this.query.getRange();
			this.seriesMap = new TreeMap<>();
			this.eventTimeList = new ArrayList<>();
		} catch (Exception e) {
			logger.error("init() exception="+e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Function Set Event & Point Flag to False
	 * Flag Set to True If Event And/Or Point Satisfies Query Parameters
	 * @param eventList
	 * @return List<Event>
	 */
	public List<Event> resetFlags(List<Event> eventList) {
		for (Event e : eventList) {
			e.flag = false;
			for (Coordinate p : e.coordinateList) {
				p.flag = false;
			}
		}
		return eventList;
	}

	/**
	 * Precondition EventList truth is known. Easiest way to know is to do a reset.
	 * Truth can change for an event by changing filter parameters and reapplying filter
	 * For example, applying regions.
	 * @param eventList
	 * @return
	 * @throws Exception
	 */
	public List<Event> filter(List<Event> eventList) throws Exception {
		if (!Thread.interrupted()) {
			if (eventList != null) {
				eventList = this.resetFlags(eventList);
				boolean regionFlag = false;
				for (Event e : eventList) {
					for (Coordinate c : e.coordinateList) {
						regionFlag = false;
						if(this.region != null) {
							if (this.region.contains(c)) {
								regionFlag = true;
							}
						} else if (this.regionList != null && this.regionList.size() > 0) {
							for (Region r : this.regionList) {
								if (r.contains(c)) {
									regionFlag = true;
								}
							}
						} else {
							regionFlag = true;
						}
						c.flag = regionFlag;
					}
					e.flag = e.hasCoordinate();
				}
				if (eventList.size() == 0) {
					logger.warn("filter(" + eventList.size() + ") zero");
				}
			}

		} else {
			throw new InterruptedException();
		}
		return eventList;
	}

	@Override
	public void process() throws Exception {
		super.process();
	}

	public List<Tile> getTileList() {
		return null;
	}

	public void setFrameList(DataType dataType, List<Frame> frameList, double dimension) {
		if (frameList != null) {
			this.initDateList(frameList);
			int latitude = (int) (this.latitude);// * this.resolution);
			int longitude = (int) (this.longitude);// * this.resolution);
			List<Data> dataList;
			for (int i = 0; i < latitude; i += dimension) {
				for (int j = 0; j < longitude; j += dimension) {
					for (int a = i; a < (i + dimension); a++) {
						for (int b = j; b < (j + dimension); b++) {
							for (Frame f : frameList) {
								dataList = f.data.get(a + "," + b);
								for (Data d : dataList) {
									if (d.type == dataType) {
										dataMatrix[a][b] = d.value;
									}
								}
							}
						}
					}
				}
			}
		}
		this.tileList = new ArrayList<>();
		for (int i = 0; i < latitude; i += dimension) {
			for (int j = 0; j < longitude; j += dimension) {
				float value = dataMatrix[i][j];
				int lat = (int) ((i - this.latitude));
				int lon;
				if (j < 180) {
					lon = j;
				} else {
					lon = j - 360;
				}
				Tile tile = new Tile(lat, lon, dimension, value);
				this.tileList.add(tile);
			}
		}
		this.initTileMinMax();
	}

	public void initCoordinateMinMax(String variable, Double nullValue) {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		boolean flag = true;
		for (Coordinate c : this.coordinateList) {
			double value = (double) c.attribute.get(variable);
			if (nullValue != null && Math.abs(value) == nullValue) {
				flag = false;
			} else {
				flag = true;
			}
			if (flag) {
				if (value > max) {
					max = value;
				}
				if (value < min) {
					min = value;
				}
			}
		}
		this.min = min;
		this.max = max;
	}

	public List<Frame> getCalendarFrameList(Calendar calendar, List<Frame> frameList) {
		List<Frame> fList = new ArrayList<>();
		for (Frame f : frameList) {
			if (f.containsCalendar(calendar)) {
				fList.add(f);
			}
		}
		return fList;
	}

	public List<Coordinate> calendarCoordinateList(Calendar calendar, List<Coordinate> coordinateList) {
		List<Coordinate> cList = new ArrayList<>();
		for (Coordinate c : coordinateList) {
//			System.out.println(calendar.getTime()+":"+c.calendar.getTime());
			if (c.containsCalendar(calendar)) {
				cList.add(c);
			}
		}
		return cList;
	}

	public void setCalendarEventList(Calendar calendar, List<Event> coordinateList) {
		for (Event c : coordinateList) {
			if (c.containsCalendar(calendar)) {
				c.flag = true;
			} else {
				c.flag = false;
			}
		}
	}

	public void setCalendarCoordinateList(Calendar calendar, List<Coordinate> coordinateList) {
		for (Coordinate c : coordinateList) {
			if (c.containsCalendar(calendar)) {
				c.flag = true;
			} else {
				c.flag = false;
			}
		}
	}

	public List<Double> getTileLatitudeList(List<Tile> tileList) {
		List<Double> tileLatitudeList = new ArrayList<>();
		for (Tile t : tileList) {
			if (!tileLatitudeList.contains(t.latitude)) {
				tileLatitudeList.add(t.latitude);
			}
		}
		return tileLatitudeList;
	}

	public void initDateList(List<Frame> frameList) {
		String sample;
		for (Frame f : frameList) {
			if (f.flag) {
				for (Long milliseconds : f.millisecondList) {
					sample = this.getYear(milliseconds) + "-" + this.getMonth(milliseconds);
					if (!this.dateList.contains(sample)) {
						this.dateList.add(sample);
					}
				}
			}
		}
	}

	public int getMonth(long milliseconds) {
		Date date = new Date(milliseconds);
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH) + 1;
		return month;
	}

	/**
	 * Function converts Milliseconds to Year
	 * @param milliseconds
	 * @return
	 */
	public int getYear(long milliseconds) {
		Date date = new Date(milliseconds);
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		return year;
	}

	/**
	 * 
	 */
	public void initMonthArray() {
		this.monthArray = new int[12];
		for (String date : this.dateList) {
			if (date != null) {
				String[] array = date.split("-");
				int month = Integer.parseInt(array[1]);
				this.monthArray[month - 1]++;
			}
		}
		if (print && detail)
			logger.info("initMonthArray() this.monthArray=" + Arrays.toString(this.monthArray));
	}
	
	/**
	 * 
	 */
	public void initMonthArray(List<Time> timeList) {
		this.monthArray = new int[12];
		for (Time date : timeList) {
			if (date != null) {
				int month = date.month;
				if(month != -1) {
					this.monthArray[month - 1]++;
				}
			}
		}
		if (print && detail)
			logger.info("initMonthArray() this.monthArray=" + Arrays.toString(this.monthArray));
	}

	public int getMonthCount() {
		int monthCount = 0;
		for (int month : this.monthArray) {
			if (month > 0) {
				monthCount++;
			}
		}
		if (print && detail)
			logger.info("getMonthCount() monthCount=" + monthCount);
		return monthCount;
	}

	public Map<Integer, Integer> initYearMap() {
		this.yearMap = new HashMap<>();
		for (String sample : this.dateList) {
			if (sample != null) {
				String[] array = sample.split("-");
				int year = Integer.parseInt(array[0]);
				Integer count = this.yearMap.get(year);
				if (count == null) {
					count = 1;
				} else {
					count++;
				}
				this.yearMap.put(year, count);
			}
		}
		if (print && detail)
			logger.info("initYearMap() this.yearMap=" + yearMap);
		return this.yearMap;
	}
	
	public Map<Integer, Integer> initYearMap(List<Time> timeList) {
		this.yearMap = new HashMap<>();
		for (Time sample : timeList) {
			if (sample != null) {
//				String[] array = sample.split("-");
				int year = sample.year;//Integer.parseInt(array[0]);
				Integer count = this.yearMap.get(year);
				if (count == null) {
					count = 1;
				} else {
					count++;
				}
				this.yearMap.put(year, count);
			}
		}
		if (print && detail)
			logger.info("initYearMap() this.yearMap=" + yearMap);
		return this.yearMap;
	}

	public int getYearCount() {
		int yearCount = 0;
		for (Map.Entry<Integer, Integer> entry : this.yearMap.entrySet()) {
			if (entry.getValue() > 0) {
				yearCount++;
			}
		}
		if (print && detail)
			logger.info("getYearCount() yearCount=" + yearCount);
		return yearCount;
	}

	public void initTileMinMax() {
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		if (this.tileList != null) {
			java.util.Iterator<Tile> iterator = this.tileList.iterator();
			while (iterator.hasNext()) {
				Tile t = new Tile(iterator.next());
				if (t.value > max) {
					max = t.value;
				}
				if (t.value < min) {
					min = t.value;
				}
			}
		}
//		if (print)
//		logger.info("initTileMinMax() min=" + min+", max=" + max + ", min<max="+(min<max));
		this.max = max;
		this.min = min;
		
	}

	public double getMax() {
		if(this.meter.length >= 2) {
			return this.meter[1];
		}
		return this.max;
	}

	public double getMin() {
		if(this.meter.length >= 1) {
			return this.meter[0];
		}
		return this.min;
	}
	
	public double getIncrement() {
		if(this.meter.length == 3) {
			return this.meter[2];
		}
		return 0;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public void paint(Graphics graphics) throws Exception {
		if (this.load) {
			switch(this.analysis) {
			case SIGNIFICANCE :{ 
				if(this.regionList != null) {
					Graphics2D g2d = (Graphics2D) graphics;
//					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
					Coordinate a;
					Coordinate b;
					Coordinate c;
					Coordinate d;
					Iterator<Region> regionIterator = this.regionList.iterator();
					Chroma significanceChroma = new Chroma(Scheme.MAGMA);
					while(regionIterator.hasNext()) {
						Region region = new Region(regionIterator.next());
						if(region != null) {
							Double significance = null;
							Series series = this.seriesMap.get(region.toString());
							if(series != null) {
								List<Regression> regressionList = series.regressionMap.get("all");
								if(regressionList != null && regressionList.size() > 0) {
									significance = regressionList.get(0).map.get("significance");
								}
							}
							if (this.tileList != null && this.tileList.size() > 0) {
								java.util.Iterator<Tile> iterator = this.tileList.iterator();
								while (iterator.hasNext()) {
									Tile t = new Tile(iterator.next());
									if (t != null && region.contains(t)) {
										a = projection.getCoordinate(0, t.latitude, t.longitude);
										b = projection.getCoordinate(0, t.latitude + t.dimension, t.longitude);
										c = projection.getCoordinate(0, t.latitude + t.dimension, t.longitude + t.dimension);
										d = projection.getCoordinate(0, t.latitude, t.longitude + t.dimension);
										if (a != null && b != null && c != null && d != null) {
											int xpoints[] = { (int) (a.point.x * projection.scale),
													(int) (b.point.x * projection.scale), (int) (c.point.x * projection.scale),
													(int) (d.point.x * projection.scale) };
											int ypoints[] = { (int) (a.point.y * projection.scale),
													(int) (b.point.y * projection.scale), (int) (c.point.y * projection.scale),
													(int) (d.point.y * projection.scale) };
											int npoints = 4;
											if(significance != null) {
												g2d.setColor(significanceChroma.getColor(significance, 0, 1));
												g2d.fillPolygon(xpoints, ypoints, npoints);
											}
										}
									}
								}
							}
						}
					}
				}
				double increment = 0;
				Meter meter = new Meter(0.9, (int)(projection.xMax * projection.scale), 1, 0,null,increment);
				meter.setChroma(this.chroma);
				meter.paint(graphics);
				break;
			}
			default: {
				if (this.tileList != null && this.tileList.size() > 0) {
					logger.debug(this + ".paint(...) tileList.size() = " + this.tileList.size());
					Graphics2D g2d = (Graphics2D) graphics;
//					g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
					Coordinate a;
					Coordinate b;
					Coordinate c;
					Coordinate d;
					java.util.Iterator<Tile> iterator = this.tileList.iterator();
					while (iterator.hasNext()) {
						Tile t = new Tile(iterator.next());
						if (t != null) {
							a = projection.getCoordinate(0, t.latitude, t.longitude);
							b = projection.getCoordinate(0, t.latitude + t.dimension, t.longitude);
							c = projection.getCoordinate(0, t.latitude + t.dimension, t.longitude + t.dimension);
							d = projection.getCoordinate(0, t.latitude, t.longitude + t.dimension);
							if (a != null && b != null && c != null && d != null) {
								int xpoints[] = { (int) (a.point.x * projection.scale),
										(int) (b.point.x * projection.scale), (int) (c.point.x * projection.scale),
										(int) (d.point.x * projection.scale) };
								int ypoints[] = { (int) (a.point.y * projection.scale),
										(int) (b.point.y * projection.scale), (int) (c.point.y * projection.scale),
										(int) (d.point.y * projection.scale) };
								int npoints = 4;
								g2d.setColor(this.chroma.getColor(t.value, this.getMin(), this.getMax()));
								g2d.fillPolygon(xpoints, ypoints, npoints);
							}
						}
					}
					Meter meter = new Meter(0.9, (int)(projection.xMax * projection.scale), this.getMax(), this.getMin(),this.unit,this.getIncrement());
					meter.setChroma(this.chroma);
					meter.paint(graphics);
				} else if (this.eventList != null && this.eventList.size() > 0) {
					logger.debug(this + ".paint(...) eventList.size() = " + this.eventList.size());
					for (int i = 0; i < this.eventList.size(); i++) {
						Event event = (Event) this.eventList.get(i);
						if (event.flag) {
							logger.debug(this + ".paint(...) event=" + event.id);
							List<Coordinate> coordinateList = this.projection.getCoordinateList(0, event.coordinateList);
							if (coordinateList != null) {
								for (Coordinate c : coordinateList) {
									if (c != null && c.flag) {
										logger.debug(this + ".paint(...) coordinate=" + c);
										graphics.setColor(this.chroma.getColor(i, 0, this.eventList.size()));
										graphics.drawLine((int) ((c.point.x) * this.projection.scale),
												(int) ((c.point.y) * this.projection.scale),
												(int) ((c.point.x) * this.projection.scale),
												(int) ((c.point.y) * this.projection.scale));
									}
								}
							}
						}
					}
				}
			}
			}
		}

		List<Variable> nodeList = this.getChildren();
		for (Variable n : nodeList) {
			n.paint(graphics);
		}
	}
}
//@Override
//public void plot(Graphics graphics) throws Exception {
//	super.plot(graphics);
//	logger.info(this + ".plot(" + (graphics != null) + ", " + panel + ") this.load="+this.load);
//	logger.info(this + ".plot(" + (graphics != null) + ", " + panel + ") this.plot="+this.plot);
//	if (this.load) {// && this.plot) {
//		logger.info(this + ".plot(" + (graphics != null) + ", " + panel + ")");
//		panel.removeAll();
//		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
//		List<Plot> plotList = this.getPlotList();
//		for (Plot plot : plotList) {
//			plot.setPanelHeight(256);
//			plot.setPanelWidth(panel.getWidth());
//			plot.repaint();
//			panel.add(plot);
//			panel.revalidate();
////			panel.repaint();
//		}
//		this.plot = false;
//	}
//	List<Variable> nodeList = this.getChildren();
//	for (Variable n : nodeList) {
//		n.plot(graphics,panel);
//	}
//}
//this.min = Double.POSITIVE_INFINITY;
//this.max = Double.NEGATIVE_INFINITY;
//this.indexList = new ArrayList<>();
//this.season = this.query.getSeason();
//List<Tile> tileList = new ArrayList<>();
//Tile tile;
//// cycle through each tile
//for (int i = 0; i < coordinateMatrix.length; i += dimension) {
//	for (int j = 0; j < coordinateMatrix[i].length; j += dimension) {
//		int lat = (int) ((i - this.latitude));
//		int lon;
//		if (j < 180) {
//			lon = j;
//		} else {
//			lon = j - 360;
//		}
//		tile = new Tile(lat, lon, dimension, 0);
////		tile = new Tile((i - this.latitude) / this.resolution, (j - (this.longitude / 2)) / this.resolution, dimension, 0);
//		tileList.add(tile);
//	}
//}
//public void paint(Graphics graphics) {
//projection.cubeFlag = this.cubeFlag;
//Coordinate a;
//Coordinate b;
//Coordinate c;
//Coordinate d;
//if (this.stackFlag) {
//	int size = this.tileListMap.size();
//	double interval = 1;
//	int index = (size/2);
//	this.tileListMap = new TreeMap<Integer, List<Tile>>(this.tileListMap).descendingMap();
//	for(Entry<Integer, List<Tile>> entry:this.tileListMap.entrySet()) {
//		List<Tile> tileList = entry.getValue();
//		if(index == 0) {
//			index--;
//		}
//		double vertical = index*interval;
//		for (Tile t : tileList) {
//			a = projection.getCoordinate(vertical,t.latitude, t.longitude);
//			b = projection.getCoordinate(vertical,t.latitude + t.dimension, t.longitude);
//			c = projection.getCoordinate(vertical,t.latitude + t.dimension, t.longitude + t.dimension);
//			d = projection.getCoordinate(vertical,t.latitude, t.longitude + t.dimension);
//			if (a != null && b != null && c != null && d != null) {
//				int xpoints[] = { (int) (a.point.x * projection.scale), (int) (b.point.x * projection.scale),
//						(int) (c.point.x * projection.scale), (int) (d.point.x * projection.scale) };
//				int ypoints[] = { (int) (a.point.y * projection.scale), (int) (b.point.y * projection.scale),
//						(int) (c.point.y * projection.scale), (int) (d.point.y * projection.scale) };
//				int npoints = 4;
//				graphics.setColor(this.chroma.getColor(t.value, this.getMin(), this.getMax()));
////				graphics.setColor(t.getColor(0.8, this.getMax(), this.getMin()));
//				graphics.fillPolygon(xpoints, ypoints, npoints);
//			}
//		}
////		projection.paint(graphics, vertical);
//		index--;
//	}
//} else {
//	if(this.bandFlag) {
//		for(Band band: this.bandList) {
//			for (Tile t : band.tileList) {
//				a = projection.getCoordinate(0,t.latitude, t.longitude);
//				b = projection.getCoordinate(0,t.latitude + t.dimension, t.longitude);
//				c = projection.getCoordinate(0,t.latitude + t.dimension, t.longitude + t.dimension);
//				d = projection.getCoordinate(0,t.latitude, t.longitude + t.dimension);
//				if (a != null && b != null && c != null && d != null) {
//					int xpoints[] = { (int) (a.point.x * projection.scale), (int) (b.point.x * projection.scale),
//							(int) (c.point.x * projection.scale), (int) (d.point.x * projection.scale) };
//					int ypoints[] = { (int) (a.point.y * projection.scale), (int) (b.point.y * projection.scale),
//							(int) (c.point.y * projection.scale), (int) (d.point.y * projection.scale) };
//					int npoints = 4;
//					graphics.setColor(this.chroma.getColor(band.value, this.getMin(), this.getMax()));
//					graphics.fillPolygon(xpoints, ypoints, npoints);
//				}
//			}
//		}
//	} else {
//		for (Tile t : this.tileList) {
//			a = projection.getCoordinate(0,t.latitude, t.longitude);
//			b = projection.getCoordinate(0,t.latitude + t.dimension, t.longitude);
//			c = projection.getCoordinate(0,t.latitude + t.dimension, t.longitude + t.dimension);
//			d = projection.getCoordinate(0,t.latitude, t.longitude + t.dimension);
//			if (a != null && b != null && c != null && d != null) {
//				int xpoints[] = { (int) (a.point.x * projection.scale), (int) (b.point.x * projection.scale),
//						(int) (c.point.x * projection.scale), (int) (d.point.x * projection.scale) };
//				int ypoints[] = { (int) (a.point.y * projection.scale), (int) (b.point.y * projection.scale),
//						(int) (c.point.y * projection.scale), (int) (d.point.y * projection.scale) };
//				int npoints = 4;
//				graphics.setColor(this.chroma.getColor(t.value, this.getMin(), this.getMax()));
////					graphics.setColor(t.getColor(0.8, this.getMax(), this.getMin()));
//				graphics.fillPolygon(xpoints, ypoints, npoints);
//			}
//		}
//	}
//}
//}