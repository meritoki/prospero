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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.meritoki.prospero.library.model.Model;
import org.meritoki.prospero.library.model.plot.Plot;
import org.meritoki.prospero.library.model.unit.Table;

/**
 *
 * @author jorodriguez
 */
public class TablePanel extends javax.swing.JPanel { //implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6800902203212169286L;
	public Model model;
	public TableModel tableModel;
	public JTabbedPane tabbedPane = new JTabbedPane();
	public String tableName = null;
	public List<Table> tableList = null;

	/**
	 * Creates new form TablePanel
	 */
	public TablePanel() {
//		Thread thread = new Thread(this);
//		thread.start();
		initComponents();
	}

//	public TablePanel(TableModel tableModel) {
//		this.tableModel = tableModel;
//
//		this.table.setModel(this.tableModel);
//		this.revalidate();
//		this.repaint();
//	}

//    public void paint(Graphics g) {
//    	
//    }

	public void setModel(Model model) {
		this.model = model;
	}

//	public void test() {
//		if (this.model != null) {
//			System.out.println("init() this.model != null");
//			this.tabbedPane.removeAll();
//			javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
//			this.setLayout(layout);
//			layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//					.addComponent(this.tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 716, Short.MAX_VALUE));
//			layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//					.addComponent(this.tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE));
//			try {
//
//				TablePanel plotPanel = new TablePanel(new DefaultTableModel());
//				this.tabbedPane.addTab("test", plotPanel);
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			this.add(this.tabbedPane);
//			this.revalidate();
//			this.repaint();
//		}
//	}

	public void init() {
		if (this.model != null) {
			List<String> nameList = new ArrayList<>();
			this.tableList = new ArrayList<>();
			try {
				List<Plot> plotList = this.model.getPlotList();
				for (Plot plot : plotList) {
					for (Table table : plot.tableList) {
						this.tableList.add(table);
						nameList.add(table.name);
					}
				}
				List<Table> tableList = this.model.getTableList();
				for (Table table : tableList) {
					this.tableList.add(table);
					nameList.add(table.name);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			this.tableNameComboBox.removeAllItems();
			for (String name : nameList) {
				this.tableNameComboBox.addItem(name);
			}

//			String name = (String) this.tableNameComboBox.getSelectedItem();
			if (this.tableName == null) {
				if(this.tableNameComboBox.getItemCount() > 0) {
					this.tableNameComboBox.setSelectedIndex(0);
					this.tableName = (String) this.tableNameComboBox.getSelectedItem();
				}
			} else {
				this.tableNameComboBox.setSelectedItem(this.tableName);
			}

			if (this.tableList.size() > 0) {
				for (Table t : this.tableList) {
					if (t.name.equals(this.tableName)) {
						this.table.setModel(t.tableModel);
						this.revalidate();
						this.repaint();
						break;
					}
				}
			} else {
				this.table.setModel(new DefaultTableModel());
			}

//                    if(this.tableNameComboBox.getSelectedItem() != this.tableName) {
//                        this.tableName = this.
//                    }
//			this.tabbedPane.removeAll();
//	        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
//	        this.setLayout(layout);
//	        layout.setHorizontalGroup(
//	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//	            .addComponent(this.tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 716, Short.MAX_VALUE)
//	        );
//	        layout.setVerticalGroup(
//	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//	            .addComponent(this.tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
//	        );
//			List<Plot> plotList;
//			try {
//				plotList = this.model.getPlotList();
//				for (Plot plot : plotList) {
//					for(Table table: plot.tableList) {
//						TablePanel tablePanel = new TablePanel(table.tableModel);
//						this.tabbedPane.addTab(table.name,tablePanel);
//					}
//				}
//				List<Table> tableList = this.model.getTableList();
//				for(Table table: tableList) {
//					TablePanel tablePanel = new TablePanel(table.tableModel);
//					this.tabbedPane.addTab(table.name,tablePanel);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			this.add(this.tabbedPane);
//			this.revalidate();
//			this.repaint();
		}
	}

//	@Override
//	public void run() {
//		while (true) {
//			try {
//				this.init();
//				Thread.sleep(10000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jScrollPane1 = new javax.swing.JScrollPane();
		table = new javax.swing.JTable();
		tableNameComboBox = new javax.swing.JComboBox<>();
		nameLabel = new javax.swing.JLabel();
		setButton = new javax.swing.JButton();
		previousButton = new javax.swing.JButton();
		nextButton = new javax.swing.JButton();

		table.setModel(
				new javax.swing.table.DefaultTableModel(
						new Object[][] { { null, null, null, null }, { null, null, null, null },
								{ null, null, null, null }, { null, null, null, null } },
						new String[] { "Title 1", "Title 2", "Title 3", "Title 4" }));
		jScrollPane1.setViewportView(table);

		tableNameComboBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				tableNameComboBoxActionPerformed(evt);
			}
		});

		nameLabel.setText("Name:");

		setButton.setText("Set");
		setButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setButtonActionPerformed(evt);
			}
		});

		previousButton.setText("Previous");
		previousButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				previousButtonActionPerformed(evt);
			}
		});

		nextButton.setText("Next");
		nextButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				nextButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 922, Short.MAX_VALUE)
				.addGroup(layout.createSequentialGroup().addContainerGap().addComponent(nameLabel)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(tableNameComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 322,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(setButton)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(previousButton, javax.swing.GroupLayout.PREFERRED_SIZE, 110,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(nextButton,
								javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(tableNameComboBox, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(nameLabel).addComponent(setButton).addComponent(previousButton)
								.addComponent(nextButton))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)));
	}// </editor-fold>//GEN-END:initComponents

	private void setButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_setButtonActionPerformed
		this.tableName = (String) this.tableNameComboBox.getSelectedItem();
		for (Table table : this.tableList) {
			if (table.name.equals(this.tableName)) {
				this.table.setModel(table.tableModel);
				this.revalidate();
				this.repaint();
				break;
			}
		}
	}// GEN-LAST:event_setButtonActionPerformed

	private void previousButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_previousButtonActionPerformed
		int index = this.tableNameComboBox.getSelectedIndex();
		if (index > -1) {
			System.out.println("previous: " + index);
			index = (index > 0) ? index - 1 : 0;
			this.tableNameComboBox.setSelectedIndex(index);
			this.tableName = (String) this.tableNameComboBox.getSelectedItem();
			if (this.tableList != null) {
				for (Table table : this.tableList) {
					if (table.name.equals(this.tableName)) {
						this.table.setModel(table.tableModel);
						this.revalidate();
						this.repaint();
						break;
					}
				}
			}
		}
	}// GEN-LAST:event_previousButtonActionPerformed

	private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_nextButtonActionPerformed
		int index = this.tableNameComboBox.getSelectedIndex();
		if (index > -1) {
			System.out.println("next: " + index + " count: " + this.tableNameComboBox.getItemCount());
			index = (index < this.tableNameComboBox.getItemCount() - 1) ? index + 1
					: this.tableNameComboBox.getItemCount() - 1;
			System.out.println("next-real: " + index);
			this.tableNameComboBox.setSelectedIndex(index);
			this.tableName = (String) this.tableNameComboBox.getSelectedItem();
			if (this.tableList != null) {
				for (Table table : this.tableList) {
					if (table.name.equals(this.tableName)) {
						this.table.setModel(table.tableModel);
						this.revalidate();
						this.repaint();
						break;
					}
				}
			}
		}
	}// GEN-LAST:event_nextButtonActionPerformed

	private void tableNameComboBoxActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tableNameComboBoxActionPerformed
		// TODO add your handling code here:
	}// GEN-LAST:event_tableNameComboBoxActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JLabel nameLabel;
	private javax.swing.JButton nextButton;
	private javax.swing.JButton previousButton;
	private javax.swing.JButton setButton;
	private javax.swing.JTable table;
	private javax.swing.JComboBox<String> tableNameComboBox;
	// End of variables declaration//GEN-END:variables
}
