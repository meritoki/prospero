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

import javax.swing.DefaultListModel;

import org.meritoki.prospero.desktop.view.frame.MainFrame;
import org.meritoki.prospero.library.model.Model;
import org.meritoki.prospero.library.model.node.Variable;

/**
 *
 * @author jorodriguez
 */
public class QueryPanel extends javax.swing.JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3523683516770087622L;
	private Model model;
	private MainFrame mainFrame;
    /**
     * Creates new form QueryPanel
     */
    public QueryPanel() {
        initComponents();
    }
    
    public void setModel(Model model) {
    	this.model = model;
    	this.init();
    }
    
    public void setMainFrame(MainFrame mainFrame) {
    	this.mainFrame = mainFrame;
    }
    
	public void init() {
		this.initList();
		this.keyTextField.setText("");
		this.valueTextField.setText("");
	}
    
	public void initList() {
		Variable node = (this.model != null) ? this.model.node : null;
		if (node != null) {
			this.initQueryList(node.query.getList());
		} else {
			this.initQueryList(new ArrayList<>());
		}
	}

	public void initQueryList(List<String> filterList) {
		DefaultListModel<String> defaultListModel = new DefaultListModel<>();
		if (filterList != null) {
			for (int i = 0; i < filterList.size(); i++) {
				defaultListModel.addElement(filterList.get(i));
			}
		}
		this.keyValueList.setModel(defaultListModel);
	}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        keyTextField = new javax.swing.JTextField();
        valueTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        keyValueList = new javax.swing.JList<>();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();

        keyValueList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(keyValueList);

        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        removeButton.setText("Remove");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(valueTextField)
                    .addComponent(keyTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(removeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                    .addComponent(addButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(keyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(valueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeButton)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        String key = this.keyTextField.getText();
        String value = this.valueTextField.getText();
        key = (!key.isEmpty()) ? key.trim() : null;
        value = (!value.isEmpty()) ? value.trim() : null;
        Variable node = (this.model != null) ? this.model.node : null;
        if (key != null && value != null && node != null) {
            node.query.put(key, value);
            try {
				node.init();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            this.mainFrame.init();
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        Variable node = (this.model != null) ? this.model.node : null;
        String attribute = this.keyValueList.getSelectedValue();
        if (attribute != null && node != null) {
            String[] pair = attribute.split("=");
            node.query.remove(pair[0]);
            try {
				node.init();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            this.mainFrame.init();
        }
    }//GEN-LAST:event_removeButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField keyTextField;
    private javax.swing.JList<String> keyValueList;
    private javax.swing.JButton removeButton;
    private javax.swing.JTextField valueTextField;
    // End of variables declaration//GEN-END:variables
}
