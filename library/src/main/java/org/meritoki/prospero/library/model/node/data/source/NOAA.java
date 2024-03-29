package org.meritoki.prospero.library.model.node.data.source;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.meritoki.prospero.library.controller.node.NodeController;
import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Result;

public class NOAA extends Source {

	public String downloadURL;

	public NOAA() {
		super();
	}

	@Override
	public void query(Query query) throws Exception {
		logger.info("query(" + query + ")");
		if (query.getDownload()) {
			this.download(query);
		}
		Result result = new Result();
		result.map.put("indexList", this.read());
		result.map.put("regionList",this.regionList);
		result.mode = Mode.LOAD;
		query.objectList.add(result);
		result = new Result();
		result.mode = Mode.COMPLETE;
		query.objectList.add(result);

	}

	public void download(Query query) throws IOException {
		logger.info("download(" + (query != null) + ")");
		File file = new File(this.getFilePath());
		if (!file.exists()) {
			logger.info("query(" + (query != null) + ") download");
			file = new File(this.getDownloadPath());
			logger.info("download(...) file="+file);
			logger.info("download(...) file.exists()="+file.exists());
			if (!file.exists()) {
				file.mkdirs();
			}
			NodeController.downloadFile(this.downloadURL, this.getDownloadPath(), this.fileName);
		}
	}

	public List<Index> read() {
		List<Index> indexList = new ArrayList<>();
		File file = new File(this.getFilePath());
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			br.readLine();
			while ((line = br.readLine()) != null) {
				String[] spaceArray = line.split(" ");
				List<String> dataList = new ArrayList<>();
				for (String s : spaceArray) {
					s = s.trim();
					if (!s.isEmpty()) {
						dataList.add(s);
					}
				}
				if (dataList.size() == 13) {
					int year = Integer.parseInt(dataList.remove(0));
					for (int i = 0; i < dataList.size(); i++) {
						double value = Double.parseDouble(dataList.get(i));
						if (value != -999) {
							Index index = new Index();
							index.value = value;
							if (i == 0) {
								int startYear = year - 1;
								int endYear = year;
								int startMonth = 11;
								int endMonth = i;
								Calendar startCalendar = new GregorianCalendar(startYear, startMonth, 1);
								Calendar endCalendar = new GregorianCalendar(endYear, endMonth, 1);
								endCalendar.set(Calendar.DAY_OF_MONTH,
										endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
//							System.out.println(startCalendar.getTime());
//							System.out.println(endCalendar.getTime());
								index.startCalendar = startCalendar;
								index.endCalendar = endCalendar;
							} else {
								int startYear = year;
								int endYear = year;
								int startMonth = i - 1;
								int endMonth = i;
								Calendar startCalendar = new GregorianCalendar(startYear, startMonth, 1);
								Calendar endCalendar = new GregorianCalendar(endYear, endMonth, 1);
								endCalendar.set(Calendar.DAY_OF_MONTH,
										endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
								index.startCalendar = startCalendar;
								index.endCalendar = endCalendar;
							}
							indexList.add(index);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return indexList;
	}
}
