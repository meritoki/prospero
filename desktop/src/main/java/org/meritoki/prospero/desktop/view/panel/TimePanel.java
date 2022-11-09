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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.meritoki.prospero.desktop.view.frame.MainFrame;
import org.meritoki.prospero.library.model.Model;

/**
 *
 * @author jorodriguez
 */
public class TimePanel extends javax.swing.JPanel implements Runnable {

	private Model model;
	public MainFrame mainFrame;
	public boolean forward;
	public boolean backward;
	public boolean run;
	public Thread thread;
	public SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public int calendarUnit = Calendar.DATE;
	public int calendarIncrement = 1;
	public int delay = 1000;
    /**
     * Creates new form TimePanel
     */
    public TimePanel() {
        initComponents();
    }
    
    public void setModel(Model model) {
    	this.model = model;
		this.init();
		this.initComboBox();
    }
    
    public void setMainFrame(MainFrame mainFrame) {
    	this.mainFrame = mainFrame;
    }
    
    public void init() {
		this.initTextField();

	}

	public void initComboBox() {
		this.initTimeComboBox(new String[] { "Minute", "Hour", "Day", "Month", "Year" });
	}

	public void initTimeComboBox(String[] list) {
		this.incrementUnitTimeComboBox.removeAllItems();
		for (String s : list) {
			this.incrementUnitTimeComboBox.addItem(s);
		}
	}

	public void initTextField() {
		Calendar calendar = (this.model != null) ? this.model.calendar : null;
		Calendar startCalendar = (this.model != null) ? this.model.startCalendar : null;
		Calendar endCalendar = (this.model != null) ? this.model.endCalendar : null;
		if (calendar != null) {
			this.currentTimeTextField.setText(this.simpleDateFormat.format(calendar.getTime()));
		}

		if (startCalendar != null && endCalendar != null) {
			this.rangeATimeTextField.setText(this.simpleDateFormat.format(startCalendar.getTime()));
			this.rangeBTimeTextField.setText(this.simpleDateFormat.format(endCalendar.getTime()));
		}
		this.currentTimeTextField.setToolTipText("Current");
		this.rangeATimeTextField.setToolTipText("Range");
		this.incrementTimeTextField.setToolTipText("Increment");
		this.delayTimeTextField.setToolTipText("Delay");
	}
	
	@Override
	public void run() {
		while (this.run) {
			try {
				Calendar calendar = this.model.calendar;
				String unit = (String) this.incrementUnitTimeComboBox.getSelectedItem();
				unit = unit.toLowerCase();
				switch (unit) {
				case "minute": {
					this.calendarUnit = Calendar.MINUTE;
					break;
				}
				case "hour": {
					this.calendarUnit = Calendar.HOUR;
					break;
				}
				case "day": {
					this.calendarUnit = Calendar.DATE;
					break;
				}
				case "month": {
					this.calendarUnit = Calendar.MONTH;
					break;
				}
				case "year": {
					this.calendarUnit = Calendar.YEAR;
					break;
				}
				}
				if (this.forward) {
					calendar.add(this.calendarUnit, this.calendarIncrement);
				} else if (this.backward) {
					calendar.add(this.calendarUnit, -this.calendarIncrement);
				}
//				this.model.data.setCalendar(calendar);// data uses calendar differently from terra.
				this.model.setCalendar(calendar);
				this.currentTimeTextField.setText(simpleDateFormat.format(this.model.calendar.getTime()));
				this.mainFrame.init();
				Thread.sleep(this.delay);
			} catch (Exception e) {

			}
		}
	}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        forwardButton = new javax.swing.JButton();
        rangeBTimeTextField = new javax.swing.JTextField();
        currentTimeBCCheckBox = new javax.swing.JCheckBox();
        backwardButton = new javax.swing.JButton();
        rangeATimeBCCheckBox = new javax.swing.JCheckBox();
        incrementUnitTimeComboBox = new javax.swing.JComboBox<>();
        rangeBTimeBCCheckBox = new javax.swing.JCheckBox();
        rangeATimeTextField = new javax.swing.JTextField();
        currentTimeTextField = new javax.swing.JTextField();
        incrementTimeTextField = new javax.swing.JTextField();
        delayTimeTextField = new javax.swing.JTextField();

        setButton.setText("Set");
        setButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setButtonActionPerformed(evt);
            }
        });

        stopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Stop-Music-icon.png"))); // NOI18N
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        forwardButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Next-Music-icon.png"))); // NOI18N
        forwardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forwardButtonActionPerformed(evt);
            }
        });

        currentTimeBCCheckBox.setText("BC");

        backwardButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Previous-Music-icon.png"))); // NOI18N
        backwardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backwardButtonActionPerformed(evt);
            }
        });

        rangeATimeBCCheckBox.setText("BC");

        incrementUnitTimeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        rangeBTimeBCCheckBox.setText("BC");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(backwardButton)
                        .addGap(18, 18, 18)
                        .addComponent(stopButton)
                        .addGap(18, 18, 18)
                        .addComponent(forwardButton))
                    .addComponent(setButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rangeBTimeTextField)
                    .addComponent(rangeATimeTextField)
                    .addComponent(incrementTimeTextField)
                    .addComponent(incrementUnitTimeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(delayTimeTextField, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(currentTimeTextField, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(currentTimeBCCheckBox)
                    .addComponent(rangeATimeBCCheckBox)
                    .addComponent(rangeBTimeBCCheckBox))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(currentTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(currentTimeBCCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rangeATimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rangeATimeBCCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rangeBTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rangeBTimeBCCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(incrementTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(incrementUnitTimeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(delayTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(setButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stopButton)
                    .addComponent(forwardButton)
                    .addComponent(backwardButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void setButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setButtonActionPerformed
		String time = this.currentTimeTextField.getText();
		String interval = this.incrementTimeTextField.getText();
		String sleep = this.delayTimeTextField.getText();
		String rangeATime = this.rangeATimeTextField.getText();
		String rangeBTime = this.rangeBTimeTextField.getText();
		boolean currentTimeBC = this.currentTimeBCCheckBox.isSelected();
		boolean rangeATimeBC = this.rangeATimeBCCheckBox.isSelected();
		boolean rangeBTimeBC = this.rangeBTimeBCCheckBox.isSelected();
		this.calendarIncrement = (!interval.isEmpty()) ? Integer.parseInt(interval) : 1;
		this.delay = (!sleep.isEmpty()) ? Integer.parseInt(sleep) : 1000;
		if (!time.isEmpty()) {
			GregorianCalendar calendar = new GregorianCalendar();
			GregorianCalendar startCalendar = new GregorianCalendar();//Calendar.getInstance();
			GregorianCalendar endCalendar = new GregorianCalendar();//Calendar.getInstance();
			calendar.setTimeZone(TimeZone.getTimeZone(this.model.timeZone));
			startCalendar.setTimeZone(TimeZone.getTimeZone(this.model.timeZone));
			endCalendar.setTimeZone(TimeZone.getTimeZone(this.model.timeZone));
			try {
				calendar.setTime(this.simpleDateFormat.parse(time));
				if(currentTimeBC) calendar.set(Calendar.ERA, GregorianCalendar.BC);
				startCalendar.setTime(this.simpleDateFormat.parse(rangeATime));
				if(rangeATimeBC) startCalendar.set(Calendar.ERA, GregorianCalendar.BC);
				endCalendar.setTime(this.simpleDateFormat.parse(rangeBTime));
				if(rangeBTimeBC) endCalendar.set(Calendar.ERA, GregorianCalendar.BC);
				this.model.setCalendar(calendar);
				this.model.setStartCalendar(startCalendar);
				this.model.setEndCalendar(endCalendar);
//				this.model.query();
				this.mainFrame.init();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }//GEN-LAST:event_setButtonActionPerformed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
    	this.run = false;
    }//GEN-LAST:event_stopButtonActionPerformed

    private void forwardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forwardButtonActionPerformed
    	this.backward = false;
		this.forward = true;
		this.run = true;
		this.thread = new Thread(this);
		this.thread.start();
    }//GEN-LAST:event_forwardButtonActionPerformed

    private void backwardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backwardButtonActionPerformed
    	this.forward = false;
		this.backward = true;
		this.run = true;
		this.thread = new Thread(this);
		this.thread.start();
    }//GEN-LAST:event_backwardButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backwardButton;
    private javax.swing.JCheckBox currentTimeBCCheckBox;
    private javax.swing.JTextField currentTimeTextField;
    private javax.swing.JTextField delayTimeTextField;
    private javax.swing.JButton forwardButton;
    private javax.swing.JTextField incrementTimeTextField;
    private javax.swing.JComboBox<String> incrementUnitTimeComboBox;
    private javax.swing.JCheckBox rangeATimeBCCheckBox;
    private javax.swing.JTextField rangeATimeTextField;
    private javax.swing.JCheckBox rangeBTimeBCCheckBox;
    private javax.swing.JTextField rangeBTimeTextField;
    private javax.swing.JButton setButton;
    private javax.swing.JButton stopButton;
    // End of variables declaration//GEN-END:variables
}
