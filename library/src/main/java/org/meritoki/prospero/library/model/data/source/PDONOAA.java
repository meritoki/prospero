package org.meritoki.prospero.library.model.data.source;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.meritoki.prospero.library.model.query.Query;
import org.meritoki.prospero.library.model.unit.Index;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Result;

public class PDONOAA extends Source {

	public String fileName = basePath+"prospero-data/NOAA/PDO/data.csv";
//	public List<Index> indexList;

	public static void main(String[] args) {
		PDONOAA p = new PDONOAA();
		p.read();
	}

	@Override
	public void query(Query query) throws Exception {
		logger.info("query(" + query + ")");
		Result result = new Result();
		result.map.put("indexList",this.read());
		result.mode = Mode.LOAD;
		query.outputList.add(result);
		result = new Result();
		result.mode = Mode.COMPLETE;
		query.outputList.add(result);
	}

	public List<Index> read() {
		List<Index> indexList = new ArrayList<>();
		File file = new File(this.fileName);
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			br.readLine();
			br.readLine();
			while ((line = br.readLine()) != null) {
				String[] commaArray = line.split(",");
				Date date = new SimpleDateFormat("yyyyMM").parse(commaArray[0]);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				double value = Double.parseDouble(commaArray[1]);
				Index index = new Index();
				index.startCalendar = calendar;
				index.endCalendar = (Calendar)calendar.clone();
				index.endCalendar.set(Calendar.DAY_OF_MONTH, index.endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				index.value = value;
				indexList.add(index);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return indexList;
	}
}
//@Override
//public Object get() {
//	if (this.indexList == null) {
//		this.indexList = read();
//	}
//	return this.indexList;
//}

