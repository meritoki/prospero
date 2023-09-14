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
package org.meritoki.prospero.desktop.view.frame;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.swing.JPanel;

import org.meritoki.prospero.desktop.model.Model;
import org.meritoki.prospero.desktop.view.dialog.AboutDialog;
import org.meritoki.prospero.desktop.view.dialog.MainDialog;
import org.meritoki.prospero.desktop.view.dialog.OpenDialog;
import org.meritoki.prospero.desktop.view.dialog.PropertyDialog;
import org.meritoki.prospero.desktop.view.dialog.SaveAsDialog;
import org.meritoki.prospero.desktop.view.dialog.copernicus.CopernicusDialog;
import org.meritoki.prospero.library.controller.node.NodeController;
import org.meritoki.prospero.library.model.node.Camera;
import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.plot.Plot;
import org.meritoki.prospero.library.model.unit.Cluster;
import org.meritoki.prospero.library.model.unit.Script;
import org.meritoki.prospero.library.model.unit.Table;
import org.meritoki.prospero.library.model.vendor.microsoft.Excel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meritoki.library.controller.model.ModelInterface;

/**
 *
 * @author jorodriguez
 */
public class MainFrame extends javax.swing.JFrame {

	private static final long serialVersionUID = -1201441426706336659L;
	static Logger logger = LoggerFactory.getLogger(MainFrame.class.getName());
	public Model model;
	public MainDialog mainDialog = new MainDialog(this, false);
	public PropertyDialog propertyDialog = new PropertyDialog(this, false);
	public CopernicusDialog copernicusDialog = new CopernicusDialog(this, false);
	public AboutDialog aboutDialog = new AboutDialog(this, false);
	public SaveAsDialog saveAsDialog = null;
	public OpenDialog openDialog = null;

	/**
	 * Creates new form MainFrame2
	 */
	public MainFrame() {
		initComponents();
		this.setTitle("Prospero Desktop Application");
	}

	public MainDialog getMainDialog() {
		return this.mainDialog;
	}

	public void setModel(Model model) {
		this.model = model;
		if (this.model.system.version != null) {
			this.setTitle("Prospero Desktop Application v" + this.model.system.version);
		}
		this.propertyDialog.setModel(this.model);
		this.copernicusDialog.setModel(this.model);
		this.mainDialog.setModel(this.model);
		this.mainDialog.setVisible(true);
		this.plotPanel.setModel(this.model);
		this.tablePanel.setModel(this.model);
		this.cameraPanel1.setModel(this.model);
		this.initIconImage();
		this.init();
	}
	
	public void initIconImage() {
		URL url = getClass().getResource("/Icon.png");
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image image = toolkit.createImage(url);
		this.setIconImage(image);
	}

	public void init() {
		logger.debug("init()");
		this.propertyDialog.init();
		this.copernicusDialog.init();
		this.mainDialog.init();
		this.cameraPanel1.init();
		this.plotPanel.init();
		this.tablePanel.init();
	}

	public void save() {
		if (this.model.system.newDocument) {
			this.saveAsDialog = new org.meritoki.prospero.desktop.view.dialog.SaveAsDialog(this, false, this.model);
		} else {
			this.model.saveDocument();
			this.init();
		}
	}

	public JPanel getGridPanel() {
		return this.cameraPanel1;
	}

	public JPanel getPlotPanel() {
		return this.plotPanel;
	}

	public void saveQuery(Query query) throws Exception {
		logger.info("saveQuery(" + query + ")");
		Date dateTime = Calendar.getInstance().getTime();
		String date = new SimpleDateFormat("yyyyMMdd").format(dateTime);
		String time = new SimpleDateFormat("HHmm").format(dateTime);
		String name = date + "-" + time + "-" + query.getName();
		String uuid = UUID.randomUUID().toString();
		String path = "output" + File.separatorChar + date + File.separatorChar + name;
		File directory = new File(path);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		Script script = new Script();
		script.queryList.add(query);
		name += "-" + uuid;
		NodeController.saveJson(path, name + ".json", script);
		this.saveCameras(path, date + "-" + time, query.getName());
		this.savePlots(path, date + "-" + time, uuid);
		this.saveTables(path, name, uuid);
	}

	/**
	 * Panel Paint Has Already Been Called
	 * 
	 * @param path
	 * @param dateTime
	 */
	public void saveCameras(String path, String dateTime, String name) {//, String uuid) {
		logger.info("saveCameras(" + path + ", " + dateTime + ", " + ", " + name + ")");
		for (Camera camera : this.model.cameraList) {
			if (camera != null) {
				Object object = camera.configuration.get("cluster");
				String c = "";
				if (object instanceof Cluster) {
					Cluster cluster = (Cluster) object;
					c += "-cluster-" + cluster.id;
				}
				Image image = camera.getImage();
				if (image != null) {
					String fileName = dateTime + "-grid" + c + "-"+ name + ".png";
					try {
						NodeController.savePng(path, fileName, NodeController.toBufferedImage(image));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void savePlots(String path, String name, String uuid) throws Exception {
		logger.info("savePlots(" + path + ", " + name + ", " + uuid + ")");
//		Excel excel = new Excel();
		for (Plot plot : this.model.getPlotList()) {
			if (plot != null) {
//				for (Table table : plot.tableList) {
//					// puts everything in one excel
//					excel.sheetMap.put(table.name, Table.getTableData(table.tableModel));
//				}
				Image image = plot.getImage();
				if (image != null) {
					String fileName;
					fileName = name + "-plot-" + plot.data + "-" + uuid + ".png";
					try {
						NodeController.savePng(path, fileName, NodeController.toBufferedImage(image));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

	public void saveTables(String path, String name, String uuid) throws Exception {
		logger.info("saveTables(" + path + ", " + name + ", " + uuid + ")");
		Excel excel = new Excel();
		for (Plot plot : this.model.getPlotList()) {
			if (plot != null) {
				for (Table table : plot.tableList) {
					// puts everything in one excel
					excel.sheetMap.put(table.name, Table.getTableData(table.tableModel));
				}
			}
		}
		for (Table table : this.model.getTableList()) {
			excel.sheetMap.put(table.name, Table.getTableData(table.tableModel));
		}
		excel.save(path, "table" + ((name != null) ? "-" + name : ""));
	}

	// this.setSize(1024, 512);
	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated
	// <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem1 = new javax.swing.JMenuItem();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        cameraPanel1 = new org.meritoki.prospero.desktop.view.panel.CameraPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        plotPanel = new org.meritoki.prospero.desktop.view.panel.PlotPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablePanel = new org.meritoki.prospero.desktop.view.panel.TablePanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newMenuItem = new javax.swing.JMenuItem();
        openMenuItem = new javax.swing.JMenuItem();
        recentMenu = new javax.swing.JMenu();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        exportMenu = new javax.swing.JMenu();
        windowMenu = new javax.swing.JMenu();
        dialogMenu = new javax.swing.JMenu();
        mainDialogMenuItem = new javax.swing.JMenuItem();
        copernicusMenuItem = new javax.swing.JMenuItem();
        propertyMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        jMenuItem1.setText("jMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout cameraPanel1Layout = new javax.swing.GroupLayout(cameraPanel1);
        cameraPanel1.setLayout(cameraPanel1Layout);
        cameraPanel1Layout.setHorizontalGroup(
            cameraPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 950, Short.MAX_VALUE)
        );
        cameraPanel1Layout.setVerticalGroup(
            cameraPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 417, Short.MAX_VALUE)
        );

        jScrollPane3.setViewportView(cameraPanel1);

        jTabbedPane1.addTab("Camera", jScrollPane3);

        plotPanel.setLayout(new javax.swing.BoxLayout(plotPanel, javax.swing.BoxLayout.LINE_AXIS));
        jScrollPane6.setViewportView(plotPanel);

        jTabbedPane1.addTab("Plot", jScrollPane6);

        jScrollPane1.setViewportView(tablePanel);

        jTabbedPane1.addTab("Table", jScrollPane1);

        fileMenu.setText("File");

        newMenuItem.setText("New");
        newMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(newMenuItem);

        openMenuItem.setText("Open");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openMenuItem);

        recentMenu.setText("Recent");
        fileMenu.add(recentMenu);

        saveMenuItem.setText("Save");
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setText("Save As");
        saveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveAsMenuItem);

        exportMenu.setText("Export");
        fileMenu.add(exportMenu);

        jMenuBar1.add(fileMenu);

        windowMenu.setText("Window");

        dialogMenu.setText("Dialog");

        mainDialogMenuItem.setText("Main");
        mainDialogMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainDialogMenuItemActionPerformed(evt);
            }
        });
        dialogMenu.add(mainDialogMenuItem);

        copernicusMenuItem.setText("Copernicus");
        copernicusMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copernicusMenuItemActionPerformed(evt);
            }
        });
        dialogMenu.add(copernicusMenuItem);

        propertyMenuItem.setText("Property");
        propertyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                propertyMenuItemActionPerformed(evt);
            }
        });
        dialogMenu.add(propertyMenuItem);

        windowMenu.add(dialogMenu);

        jMenuBar1.add(windowMenu);

        helpMenu.setText("Help");

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
        );

        this.setSize(1024, 1024);
    }// </editor-fold>//GEN-END:initComponents

    private void copernicusMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copernicusMenuItemActionPerformed
        this.copernicusDialog.setVisible(true);
    }//GEN-LAST:event_copernicusMenuItemActionPerformed

    private void propertyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_propertyMenuItemActionPerformed
    	 this.propertyDialog.setVisible(true);
    }//GEN-LAST:event_propertyMenuItemActionPerformed

	private void mainDialogMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mainDialogMenuItemActionPerformed
		this.mainDialog.setVisible(true);
	}// GEN-LAST:event_mainDialogMenuItemActionPerformed

	private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_aboutMenuItemActionPerformed
		this.aboutDialog.setVisible(true);
	}// GEN-LAST:event_aboutMenuItemActionPerformed

	private void newMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_newMenuItemActionPerformed
		this.model.newDocument();
		// TODO add your handling code here:
	}// GEN-LAST:event_newMenuItemActionPerformed

	private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_openMenuItemActionPerformed
		this.openDialog = new OpenDialog(this, false, this.model);
	}// GEN-LAST:event_openMenuItemActionPerformed

	private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveMenuItemActionPerformed
		this.save();
	}// GEN-LAST:event_saveMenuItemActionPerformed

	private void saveAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveAsMenuItemActionPerformed
		this.saveAsDialog = new org.meritoki.prospero.desktop.view.dialog.SaveAsDialog(this, false, this.model);
	}// GEN-LAST:event_saveAsMenuItemActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		// <editor-fold defaultstate="collapsed" desc=" Look and feel setting code
		// (optional) ">
		/*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the default
		 * look and feel. For details see
		 * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		// </editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MainFrame().setVisible(true);
			}
		});
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private org.meritoki.prospero.desktop.view.panel.CameraPanel cameraPanel1;
    private javax.swing.JMenuItem copernicusMenuItem;
    private javax.swing.JMenu dialogMenu;
    private javax.swing.JMenu exportMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JMenuItem mainDialogMenuItem;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JMenuItem openMenuItem;
    private org.meritoki.prospero.desktop.view.panel.PlotPanel plotPanel;
    private javax.swing.JMenuItem propertyMenuItem;
    private javax.swing.JMenu recentMenu;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private org.meritoki.prospero.desktop.view.panel.TablePanel tablePanel;
    private javax.swing.JMenu windowMenu;
    // End of variables declaration//GEN-END:variables
}
//Plot plot = this.plotPanel.plotList.get(i);
//NodeController.savePanel(this.plotPanel, path,"plot-"+((name !=null)?name:""));
//Excel excel = new Excel();
//List<Plot> plotList;
//try {
//	plotList = model.getPlotList();
//	for(Plot p: plotList) {
//		for(Table table: p.tableList) {
//			excel.sheetMap.put(table.name,Table.getTableData(table.tableModel));
//		}
//    }
//    excel.save(path, "table-"+((name !=null)?name:""));
//} catch (Exception e) {
//	// TODO Auto-generated catch block
//	e.printStackTrace();
//}
//public void test() {
//if(this.model.test) {
//	Variable var = this.model.getVariable("Density");
//	var.query.put("time", "djf");
//	var.query.put("average","true");
//	var.query.put("group","month");
//	var.query.put("dimension","2");
////	var.query.put("analysis", "significance");
//	var.query.put("region","-75,-180:-15,180");
//	var.query.put("source","UTN ERA INTERIM");
//	var.init();
////	var.source = "UTN ERA Interim";
//	var.start();
//	Query q = var.query;
//	var.query(q);
//}
//}