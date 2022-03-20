package org.meritoki.prospero.library.model.vendor.microsoft;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Excel {
	public Map<String,Object[][]> sheetMap = new HashMap<>();
	
	public String getDefaultPath() {
		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = dateFormat.format(date);
		String path = "."+File.separatorChar+"output" + File.separatorChar + dateString;
		return path;
	}
	
	public void save(String path, String name) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet;
		System.out.println("Creating excel");
		for (Entry<String,Object[][]> entry: this.sheetMap.entrySet()) {
			String key = entry.getKey();
//			System.out.println(key);
			Object[][] datatypes = entry.getValue();
			sheet = workbook.createSheet(key);
			int rowNum = 0;
			for (Object[] datatype : datatypes) {
				Row row = sheet.createRow(rowNum++);
				int colNum = 0;
				for (Object field : datatype) {
					Cell cell = row.createCell(colNum++);
					if (field instanceof String) {
						cell.setCellValue((String) field);
					} else if (field instanceof Integer) {
						cell.setCellValue((Integer) field);
					} else if (field instanceof Double) {
						cell.setCellValue((Double)field);
					} else if (field instanceof Long) {
						cell.setCellValue((Long)field);
					}
				}
			}
		}
		if(path == null) {
			path = this.getDefaultPath();
			
		}
		File directory = new File(path);
		directory.mkdirs();
		File file = new File(path+File.separatorChar+name + ".xlsx");
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			FileOutputStream outputStream = new FileOutputStream(path+File.separatorChar+name + ".xlsx");
			workbook.write(outputStream);
			workbook.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Done");
	}
}
