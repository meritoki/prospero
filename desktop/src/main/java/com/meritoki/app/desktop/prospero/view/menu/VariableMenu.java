package com.meritoki.app.desktop.prospero.view.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.meritoki.app.desktop.prospero.view.frame.MainFrame;
import com.meritoki.library.prospero.model.Model;
import com.meritoki.library.prospero.model.query.Query;
import com.meritoki.library.prospero.model.unit.Operator;

public class VariableMenu extends JPopupMenu {

	@JsonIgnore
	protected Logger logger = Logger.getLogger(VariableMenu.class.getName());
	/**
	 * 
	 */
	private static final long serialVersionUID = 288610046583103334L;

	public VariableMenu(Model model, MainFrame mainFrame) {
		for (String source : model.node.getSourceList()) {
			JCheckBoxMenuItem sourceMenuItem = new JCheckBoxMenuItem(source);
			if (source.equals(model.node.source)) {
				sourceMenuItem.setState(true);
			}
			sourceMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) e.getSource();
					if (menuItem.isSelected()) {
						logger.info("VariableMenu(" + model.node + ") e.getActionCommand()=" + e.getActionCommand());
						String source = e.getActionCommand();
						model.node.source = source;
						model.node.start();
						model.node.query.map.put("variable",model.node.name);//Consider generalizing and allowing in all cases, especifally if we want a user to ba able to save and rerun any script
						model.node.init();
						if(model.node.query.getOperator()== Operator.OR) {//Here we detect OR and know we MUST process as a script
							model.scriptList.add(model.node.script);
							mainFrame.getMainDialog().getModelPanel().getScriptPanel().init();
							mainFrame.getMainDialog().getModelPanel().getScriptPanel().query();
							int index = mainFrame.getMainDialog().getModelPanel().getTabbedPane().indexOfTab("Script");
							mainFrame.getMainDialog().getModelPanel().getTabbedPane().setSelectedIndex(index);
						} else {//IF an AND, we can run the query
							model.node.query(model.node.query);
						}
						mainFrame.init();
					} else {
						logger.info("VariableMenu(" + model.node + ") !menuItem.isSelected()");
						model.node.stop();
					}
				}
			});
			this.add(sourceMenuItem);
		}
		for (String variable : model.node.getVariableList()) {
			JCheckBoxMenuItem variableMenuItem = new JCheckBoxMenuItem(variable);
			boolean loaded = model.node.variableMap.get(variable);
			if (loaded) {
				variableMenuItem.setState(true);
			}
			variableMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) ev.getSource();
					if (menuItem.isSelected()) {
						System.out.println(ev.getActionCommand());
						model.node.start();
						model.node.variableMap.put(ev.getActionCommand(), true);
					} else {
						model.node.stop();
						model.node.variableMap.put(ev.getActionCommand(), false);
					}
				}
			});
			this.add(variableMenuItem);
		}
	}
}
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
