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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import org.meritoki.prospero.desktop.view.frame.MainFrame;
import org.meritoki.prospero.library.model.Model;
import org.meritoki.prospero.library.model.node.Camera;
import org.meritoki.prospero.library.model.unit.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class VariableMenu extends JPopupMenu {

	@JsonIgnore
	static Logger logger = LoggerFactory.getLogger(VariableMenu.class.getName());
	/**
	 * 
	 */
	private static final long serialVersionUID = 288610046583103334L;

	public VariableMenu(Model model, MainFrame mainFrame) {
		JMenuItem newMenuItem = new JMenuItem("New");
		newMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				model.addCamera(new Camera(model.getCamera().getNode()));
			}
		});
		this.add(newMenuItem);
		this.add(new JSeparator());
		for (String source : model.getCamera().node.getSourceList()) {
			JCheckBoxMenuItem sourceMenuItem = new JCheckBoxMenuItem(source);
			if (source.equals(model.getCamera().node.query.getSource())) {
				sourceMenuItem.setState(true);
			}
			sourceMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) e.getSource();
					if (menuItem.isSelected()) {
						String source = e.getActionCommand();
						logger.info("VariableMenu(" + model.getCamera().node + ") source=" + source);
						model.getCamera().node.source = source;
						model.getCamera().node.start();
						model.getCamera().node.query.map.put("source",source);
						model.getCamera().node.query.map.put("variable",model.getCamera().node.name);//Consider generalizing and allowing in all cases, especifally if we want a user to ba able to save and rerun any script
						model.getCamera().node.init();
						if(model.getCamera().node.query.getOperator()== Operator.OR) {//Here we detect OR and know we MUST process as a script
							model.scriptList.add(model.getCamera().node.script);
							mainFrame.getMainDialog().getModelPanel().getScriptPanel().init();
							mainFrame.getMainDialog().getModelPanel().getScriptPanel().query();
							int index = mainFrame.getMainDialog().getModelPanel().getTabbedPane().indexOfTab("Script");
							mainFrame.getMainDialog().getModelPanel().getTabbedPane().setSelectedIndex(index);
						} else {//IF an AND, we can run the query
							model.getCamera().node.query();//model.node.query);
						}
						mainFrame.init();
					} else {
						logger.info("VariableMenu(" + model.getCamera().node + ") !menuItem.isSelected()");
						model.getCamera().node.stop();
						model.getCamera().node.query.map.remove("source");
						model.getCamera().node.query.map.remove("sourceUUID");
						mainFrame.init();
					}
				}
			});
			this.add(sourceMenuItem);
		}
		for (String variable : model.getCamera().node.getVariableList()) {
			JCheckBoxMenuItem variableMenuItem = new JCheckBoxMenuItem(variable);
			boolean loaded = model.getCamera().node.variableMap.get(variable);
			if (loaded) {
				variableMenuItem.setState(true);
			}
			variableMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) ev.getSource();
					if (menuItem.isSelected()) {
						logger.info(ev.getActionCommand());
						model.getCamera().node.start();
						model.getCamera().node.variableMap.put(ev.getActionCommand(), true);
					} else {
						model.getCamera().node.stop();
						model.getCamera().node.variableMap.put(ev.getActionCommand(), false);
					}
				}
			});
			this.add(variableMenuItem);
		}
	}
}
//JMenuItem menuItem = (JMenuItem) ev.getSource();
//if (menuItem.isSelected()) {
//	System.out.println(ev.getActionCommand());
//	model.getNode().start();
//	model.getNode().variableMap.put(ev.getActionCommand(), true);
//} else {
//	model.getNode().stop();
//	model.getNode().variableMap.put(ev.getActionCommand(), false);
//}
//while (node.mode != Mode.COMPLETE && !Thread.interrupted()) {
//logger.info("query() node.mode="+node.mode);
//mainFrame.init();
//try {
//	Thread.sleep(1000);
//}catch(Exception ex) {
//	ex.printStackTrace();
//}
//}
//node.sourceKey = sourceKey;
//node.load = true;
//node.load();
//try {
//	node.query();
//} catch(Exception e) {
//	e.printStackTrace();
//}
