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
package org.meritoki.prospero.desktop.view.panel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.desktop.view.dialog.LoadDialog;
import org.meritoki.prospero.desktop.view.frame.MainFrame;
import org.meritoki.prospero.library.model.Model;
import org.meritoki.prospero.library.model.node.Camera;
import org.meritoki.prospero.library.model.node.Grid;
import org.meritoki.prospero.library.model.node.Variable;
import org.meritoki.prospero.library.model.node.cartography.AzimuthalSouth;
import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.terra.Terra;
import org.meritoki.prospero.library.model.terra.biosphere.country.Country;
import org.meritoki.prospero.library.model.unit.Analysis;
import org.meritoki.prospero.library.model.unit.Cluster;
import org.meritoki.prospero.library.model.unit.Script;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meritoki.library.controller.memory.MemoryController;
import com.meritoki.library.controller.time.TimeController;

/**
 *
 * @author jorodriguez
 */
public class ScriptPanel extends javax.swing.JPanel {

	private static final long serialVersionUID = 1L;
	static Logger logger = LogManager.getLogger(ScriptPanel.class.getName());
	private Model model;
	private MainFrame mainFrame;
	public LoadDialog loadDialog;
	public ObjectMapper objectMapper = new ObjectMapper();
	public Thread thread;
	public Runnable runnable;
	public List<Script> scriptList = new ArrayList<>();
    /**
     * Creates new form ScriptPanel
     */
    public ScriptPanel() {
        initComponents();
        
    }
    
    public void setModel(Model model) {
    	this.model = model;
		this.query();
    	this.init();
    }
    
    public void init() {
    	this.initTextArea();
    	this.scriptList = this.model.scriptList;
    }
    
    public void initTextArea() {
    	if(this.model != null) {
    		Variable node = this.model.getCamera().node;
    		if(node != null) {
    			Script script = node.script;
    			if(script != null) {
    				try {
    					String json = script.getJson();
    					this.scriptTextArea.setText(json);
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    			}
    		}
    	}
    }
    
    public void setMainFrame(MainFrame mainFrame) {
    	this.mainFrame = mainFrame;
    	this.loadDialog = new LoadDialog(this.mainFrame, true);
    }
    
	private void showLoad() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				loadDialog.setVisible(true);
			}
		});
	}

	private void hideLoad() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				loadDialog.setVisible(false);
			}
		});
	}
    
    public void query() {
		this.runnable = new Runnable() {
			public void run() {
				Thread.currentThread().setName("Script");
				if(model.scriptList.size() > 0) {
					Terra terra = (Terra)model.getVariable("Terra");
					terra.setSelectedProjection(new AzimuthalSouth());
					Country country = (Country)terra.getVariable("Country");
					country.start();
					country.query.map.put("source","Natural Earth");
					country.query(country.query);
					Iterator<Script> iterator = scriptList.iterator();
					while (iterator.hasNext()) {
						Script script = iterator.next();
						if (script != null) {
							for (Query query : script.queryList) {
								if (!Thread.currentThread().isInterrupted()) {
									consoleTextArea.append("query " + query + "\n");
									TimeController.start();
									String variable = query.getVariable();
									Variable node = model.getVariable(variable);
									if (node != null) {
										logger.info("query() node="+node);
										model.getCamera().setNode(terra);
										node.start();//can be called more than once, no problem
										try {
											node.query(query);//discrete finite task that sets a new query, includes process
											while (!node.isComplete() && !Thread.interrupted()) {
												Thread.sleep(4000);
											}
											logger.info("query() node.isComplete()="+node.isComplete());
											if(((Grid)node).analysis == Analysis.CLUSTER) {
												List<Cluster> clusterList = ((Grid)node).clusterList;
												for(Cluster cluster:clusterList) {
													Camera camera = new Camera(terra);
													camera.configuration.put("node", node);
													camera.configuration.put("cluster",cluster);
													model.addCamera(camera);
												}
											}
											if (!Thread.currentThread().isInterrupted()) {
												mainFrame.init();
												mainFrame.saveQuery(query);
												MemoryController.log();
												TimeController.stop();
												model.removeCameras();
												model.addCamera(new Camera(terra));
												consoleTextArea.append("query finished...\n");
											} else {
												consoleTextArea.append("script interrupt handled...\n");
		
												break;
											}
											node.stop();
										} catch (Exception qe) {
											consoleTextArea.append(qe.getMessage() + "\n");
											node.stop();
										}
									} else {
										logger.warn("query() node == null");
										consoleTextArea.append("query failed...\n");
									}
								} else {
									consoleTextArea.append("script interrupt handled...\n");
									break;
								}
							}
							consoleTextArea.append("script finished...\n");
						}
						iterator.remove();
					}
//					country.stop();
				}
			}
			
		};
		this.thread = new Thread(runnable);
		this.thread.start();
	}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        executeButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        scriptTextArea = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        consoleTextArea = new javax.swing.JTextArea();

        executeButton.setText("Execute");
        executeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                executeButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        scriptTextArea.setColumns(20);
        scriptTextArea.setRows(5);
        jScrollPane2.setViewportView(scriptTextArea);

        consoleTextArea.setColumns(20);
        consoleTextArea.setRows(5);
        jScrollPane1.setViewportView(consoleTextArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(executeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(executeButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void executeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_executeButtonActionPerformed
		this.consoleTextArea.setText(null);
		String value = this.scriptTextArea.getText();
		try {
			Script script = this.objectMapper.readValue(value, Script.class);
			this.scriptList.add(script);
			this.consoleTextArea.append("script started...\n");
			this.query();
		} catch (JsonMappingException e) {
			consoleTextArea.append(e.getMessage() + "\n");
			logger.error(e.getMessage());
		} catch (JsonProcessingException e) {
			consoleTextArea.append(e.getMessage() + "\n");
			logger.error(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }//GEN-LAST:event_executeButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    	if (this.thread != null) {
			this.thread.interrupt();
			this.consoleTextArea.append("script interrupted...\n");
		}
    }//GEN-LAST:event_cancelButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextArea consoleTextArea;
    private javax.swing.JButton executeButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea scriptTextArea;
    // End of variables declaration//GEN-END:variables
}
