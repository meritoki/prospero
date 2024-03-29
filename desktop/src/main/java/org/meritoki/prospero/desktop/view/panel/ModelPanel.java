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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTabbedPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.meritoki.prospero.desktop.view.frame.MainFrame;
import org.meritoki.prospero.desktop.view.menu.VariableMenu;
import org.meritoki.prospero.library.controller.node.NodeController;
import org.meritoki.prospero.library.model.Model;
import org.meritoki.prospero.library.model.node.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jorodriguez
 */
public class ModelPanel extends javax.swing.JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6776181167285776504L;
	static Logger logger = LoggerFactory.getLogger(ModelPanel.class.getName());
	private Model model;
	private MainFrame mainFrame;
	private VariableMenu nodeMenu;

	/**
	 * Creates new form ModelPanel
	 */
	public ModelPanel() {
		initComponents();

		this.initDataTreeMouseListener();
	}

	public ScriptPanel getScriptPanel() {
		return this.scriptPanel1;
	}

	public JTabbedPane getTabbedPane() {
		return this.jTabbedPane1;
	}

	public void setModel(Model model) {
		this.model = model;
		this.timePanel1.setModel(this.model);
		this.queryPanel1.setModel(this.model);
		this.scriptPanel1.setModel(this.model);
		this.initTree();
	}

	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		this.timePanel1.setMainFrame(this.mainFrame);
		this.queryPanel1.setMainFrame(this.mainFrame);
		this.scriptPanel1.setMainFrame(this.mainFrame);
	}

	public void init() {
		this.timePanel1.init();
		this.queryPanel1.init();
		this.scriptPanel1.init();
	}

	public void initTree() {
		this.jTree1.setModel(new DefaultTreeModel(this.model.getTree()));
		this.jTree1
				.setSelectionPath(new TreePath(((DefaultMutableTreeNode) this.jTree1.getModel().getRoot()).getPath()));
		this.jTree1.setToggleClickCount(1);
		this.jTree1.setRootVisible(false);
	}

	public void addDataTreeSelectionListener() {
		this.jTree1.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();
				if (selectedNode != null && !selectedNode.equals("null")) {
					model.getCamera().setNode(model.getVariable(selectedNode.toString()));
					mainFrame.init();
				}
			}
		});
	}

	public void initDataTreeMouseListener() {
		MouseListener mouseListener = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger() && (NodeController.isLinux() || NodeController.isMac())) {
					TreePath treePath = jTree1.getPathForLocation(e.getX(), e.getY());
					if (treePath != null) {
						Object lastPathComponent = (Object) treePath.getLastPathComponent();
						if (e.getClickCount() == 1) {
							if (model != null) {
								Variable node = model.getVariable(lastPathComponent.toString());
								if (node != null) {
									model.getCamera().setNode(node);
									nodeMenu = new VariableMenu(model, mainFrame);
									nodeMenu.show(e.getComponent(), e.getX(), e.getY());
								}
							}
						}
					}
				} else {
					TreePath treePath = jTree1.getPathForLocation(e.getX(), e.getY());
					if (treePath != null) {
						Object lastPathComponent = (Object) treePath.getLastPathComponent();
						if (e.getClickCount() == 1) {
							if (model != null) {
								Variable node = model.getVariable(lastPathComponent.toString());
								if (node != null) {
									model.getCamera().setNode(node);
									mainFrame.init();
								}
							}
						}
					}
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger() && NodeController.isWindows()) {
					TreePath treePath = jTree1.getPathForLocation(e.getX(), e.getY());
					if (treePath != null) {
						Object lastPathComponent = (Object) treePath.getLastPathComponent();
						if (e.getClickCount() == 1) {
							if (model != null) {
								Variable node = model.getVariable(lastPathComponent.toString());
								if (node != null) {
									model.getCamera().setNode(node);
									nodeMenu = new VariableMenu(model, mainFrame);
									nodeMenu.show(e.getComponent(), e.getX(), e.getY());
								}
							}
						}
					}
				}
			}

		};
		jTree1.addMouseListener(mouseListener);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jScrollPane3 = new javax.swing.JScrollPane();
		jTree1 = new javax.swing.JTree();
		jTabbedPane1 = new javax.swing.JTabbedPane();
		timePanel1 = new org.meritoki.prospero.desktop.view.panel.TimePanel();
		queryPanel1 = new org.meritoki.prospero.desktop.view.panel.QueryPanel();
		scriptPanel1 = new org.meritoki.prospero.desktop.view.panel.ScriptPanel();

		jScrollPane3.setViewportView(jTree1);

		jTabbedPane1.addTab("Time", timePanel1);
		jTabbedPane1.addTab("Query", queryPanel1);
		jTabbedPane1.addTab("Script", scriptPanel1);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout.createSequentialGroup().addGap(14, 14, 14)
										.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 306,
														Short.MAX_VALUE)
												.addComponent(jScrollPane3))
										.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 256,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
						.addGap(14, 14, 14)));
	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JTabbedPane jTabbedPane1;
	private javax.swing.JTree jTree1;
	private org.meritoki.prospero.desktop.view.panel.QueryPanel queryPanel1;
	private org.meritoki.prospero.desktop.view.panel.ScriptPanel scriptPanel1;
	private org.meritoki.prospero.desktop.view.panel.TimePanel timePanel1;
	// End of variables declaration//GEN-END:variables
}
//this.addDataTreeSelectionListener();
//else {
//TreePath treePath = jTree1.getPathForLocation(e.getX(), e.getY());
//if (treePath != null) {
//	Object lastPathComponent = (Object) treePath.getLastPathComponent();
//	if (e.getClickCount() == 1) {
//		if (model != null) {
//			Variable node = model.getVariable(lastPathComponent.toString());
////			node.initVariableMap();
//			if (node != null) {
//				model.getCamera().setNode(node);
////				model.updateNode(node);
//				mainFrame.init();
//			}
//		}
//	}
//}
//}
//if (e.isPopupTrigger()) {
//TreePath treePath = jTree1.getPathForLocation(e.getX(), e.getY());
//if (treePath != null) {
//	Object lastPathComponent = (Object) treePath.getLastPathComponent();
//	if (e.getClickCount() == 1) {
//		if (model != null) {
//			Variable node = model.getVariable(lastPathComponent.toString());
////			node.initVariableMap();
//			if (node != null) {
//				model.getCamera().setNode(node);
//				nodeMenu = new VariableMenu(model, mainFrame);//can conceivably pass model
//				nodeMenu.show(e.getComponent(), e.getX(), e.getY());
//			}
//		}
//	}
//}
//} else {
