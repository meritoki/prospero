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
package org.meritoki.prospero.desktop.view.menu;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.meritoki.prospero.desktop.view.panel.PlotPanel;
import org.meritoki.prospero.library.controller.node.NodeController;
import org.meritoki.prospero.library.model.Model;
import org.meritoki.prospero.library.model.plot.Plot;
import org.meritoki.prospero.library.model.unit.Table;
import org.meritoki.prospero.library.model.vendor.microsoft.Excel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlotPopupMenu extends JPopupMenu {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4405713496092773322L;
	static Logger logger = LoggerFactory.getLogger(PlotPopupMenu.class.getName());
	private JMenuItem saveMenuItem;
	private JMenuItem exportMenuItem;
	private Model model;
    public PlotPopupMenu(Model model) {
    	this.model = model;
        this.saveMenuItem = new JMenuItem("Save");
        this.add(this.saveMenuItem);
        this.saveMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
            	Date dateTime = Calendar.getInstance().getTime();
            	String date = new SimpleDateFormat("yyyyMMdd").format(dateTime);
            	String time = new SimpleDateFormat("HHmm").format(dateTime);
        		String path = "output" + File.separatorChar + date + File.separatorChar + time;
        		File directory = new File(path);
        		if(!directory.exists()) {
        			directory.mkdirs();
        		}
                String uuid = UUID.randomUUID().toString();
                JPanel panel = (JPanel)getInvoker();
//                if(panel instanceof GridPanel) {
//                	NodeController.savePanel(panel, path, "grid-"+uuid);
//                } else if(panel instanceof SolarPanel) {
//                	NodeController.savePanel(panel, path, "solar-"+uuid);
//                } else                	
                if(panel instanceof PlotPanel) {
                	PlotPanel plotPanel = (PlotPanel)panel;
                	int width = plotPanel.getWidth();
                	int height = 256;
//                	Excel excel = new Excel();
                	for(Plot plot: plotPanel.plotList) {
            			if(plot != null) {
//            				for(Table table: plot.tableList) {
//            					excel.sheetMap.put(table.name,Table.getTableData(table.tableModel));
//            				}
            				plot.setPanelWidth(width);
            				plot.setPanelHeight(height);
            				Image image = plotPanel.createImage(width, height);
            				image = plot.getImage(image);
            				try {
            					NodeController.savePng(path, "plot-"+plot.data+".png", NodeController.toBufferedImage(image));
            				} catch (Exception e) {
            					e.printStackTrace();
            				}
            			}
            		}
//                	excel.save(path, "table");
                }
            }
        });
        this.exportMenuItem = new JMenuItem("Export");
        this.add(this.exportMenuItem);
        this.exportMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
            	Date dateTime = Calendar.getInstance().getTime();
            	String date = new SimpleDateFormat("yyyyMMdd").format(dateTime);
            	String time = new SimpleDateFormat("HHmm").format(dateTime);
        		String path = "output" + File.separatorChar + date + File.separatorChar + time;
                String uuid = UUID.randomUUID().toString();
                Excel excel = new Excel();
                List<Plot> plotList;
				try {
					plotList = model.getPlotList();
					for(Plot p: plotList) {
						for(Table table: p.tableList) {
							excel.sheetMap.put(table.name,Table.getTableData(table.tableModel));
						}
	                }
					List<Table> tableList = model.getTableList();
					for(Table table: tableList) {
						excel.sheetMap.put(table.name,Table.getTableData(table.tableModel));
					}
					
	                excel.save(path, "table-"+uuid);
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        });
    }
}
//File pathFile = new File(path+UUID.randomUUID().toString());
//if(!pathFile.exists()) {
//	pathFile.mkdirs();
//}
//PlotPanel plotPanel = (PlotPanel)panel;
//int width = plotPanel.getWidth();
//int height = 256;
//for (int i = 0; i < plotPanel.plotList.size();i++) {
//	Plot plot = plotPanel.plotList.get(i);
//	if(plot != null) {
//		plot.setPanelWidth(width);
//		plot.setPanelHeight(height);
//		Image image = plotPanel.createImage(width, height);
//		image = plot.getImage(image);
//		fileName = "plot-"+plot.data+".png";
//		try {
//			NodeController.savePng(path, fileName, toBufferedImage(image));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//}