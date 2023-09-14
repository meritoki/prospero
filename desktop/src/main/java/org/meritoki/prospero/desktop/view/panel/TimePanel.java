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
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.meritoki.prospero.desktop.view.frame.MainFrame;
import org.meritoki.prospero.library.model.Model;
import org.meritoki.prospero.library.model.node.Variable;
import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.unit.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meritoki.library.controller.memory.MemoryController;
import com.meritoki.library.controller.time.TimeController;

/**
 *
 * @author jorodriguez
 */
public class TimePanel extends javax.swing.JPanel implements Runnable {

	private static final long serialVersionUID = -1351214701842111648L;
	static Logger logger = LoggerFactory.getLogger(TimePanel.class.getName());
	private Model model;
	public MainFrame mainFrame;
	public boolean forth;
	public boolean back;
	public boolean run;
	public Thread thread;
	public Runnable runnable;
	public SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public int timeUnit = Calendar.DATE;
	public int timeIncrement = 1;
	public int timeDelay = 1000;

	/**
	 * Creates new form TimePanel
	 */
	public TimePanel() {
		initComponents();
		this.initComboBox();
	}

	public void setModel(Model model) {
		this.model = model;
		this.init();
	}

	public void setMainFrame(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	public void init() {
		this.initTextField();
		this.initComboBox();

	}

	public void initComboBox() {
		this.initTimeComboBox(new String[] { "Second", "Minute", "Hour", "Day", "Month", "Year" });
	}

	public void initTimeComboBox(String[] list) {
		if (this.incrementUnitTimeComboBox.getItemCount() == 0) {
			for (String s : list) {
				this.incrementUnitTimeComboBox.addItem(s);
			}
		}
		if (this.model != null) {
			if (this.model.query != null) {
				this.incrementUnitTimeComboBox.setSelectedItem(this.model.query.getUnit());
			} else {
				this.incrementUnitTimeComboBox.setSelectedItem("Day");
			}
		}
	}

	public void initTextField() {
		if (this.model != null) {
			if (this.model.query != null) {
				String time = this.model.query.getTime();
				if (Time.isDate(time) != null) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(Time.getDate(time));
					this.model.setCalendar(calendar);
					this.currentTimeTextField.setText(this.simpleDateFormat.format(calendar.getTime()));
				} else {
					this.currentTimeTextField.setText(model.query.getTime());
				}
				Calendar startCalendar = this.model.startCalendar;
				Calendar endCalendar = this.model.endCalendar;

				try {
					if (this.model.query.getWindow().length > 0) {
						startCalendar = this.model.query.getWindow()[0];
						endCalendar = this.model.query.getWindow()[1];
						if (startCalendar != null && endCalendar != null) {
							this.model.setStartCalendar(startCalendar);
							this.model.setEndCalendar(endCalendar);
							this.startTimeTextField.setText(this.simpleDateFormat.format(startCalendar.getTime()));
							this.endTimeTextField.setText(this.simpleDateFormat.format(endCalendar.getTime()));
						}
						String increment = model.query.getIncrement();
						String delay = model.query.getDelay();
						this.incrementTimeTextField.setText(increment);
						this.timeIncrement = (!increment.isEmpty()) ? Integer.parseInt(increment) : 1;
						this.timeDelay = (!delay.isEmpty()) ? Integer.parseInt(delay) : 1000;
					} else {
						if (startCalendar != null && endCalendar != null) {
							this.startTimeTextField.setText(this.simpleDateFormat.format(startCalendar.getTime()));
							this.endTimeTextField.setText(this.simpleDateFormat.format(endCalendar.getTime()));
						}
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				Calendar calendar = this.model.calendar;
				Calendar startCalendar = this.model.startCalendar;
				Calendar endCalendar = this.model.endCalendar;
				if (calendar != null) {
					this.currentTimeTextField.setText(this.simpleDateFormat.format(calendar.getTime()));
				}
				if (startCalendar != null && endCalendar != null) {
					this.startTimeTextField.setText(this.simpleDateFormat.format(startCalendar.getTime()));
					this.endTimeTextField.setText(this.simpleDateFormat.format(endCalendar.getTime()));
				}
				this.incrementTimeTextField.setText(String.valueOf(this.timeIncrement));
			}
		}

		this.delayTimeTextField.setText(String.valueOf(this.timeDelay));
		this.currentTimeTextField.setToolTipText("Current Time");
		this.startTimeTextField.setToolTipText("Start Time");
		this.endTimeTextField.setToolTipText("End Time");
		this.incrementTimeTextField.setToolTipText("Increment");
		this.delayTimeTextField.setToolTipText("Delay");
	}

	@Override
	public void run() {
		Thread.currentThread().setName("Time");
		while (run) {
			try {
				Calendar calendar = getCalendar();// Back or For
				model.setCalendar(calendar);
				currentTimeTextField.setText(simpleDateFormat.format(model.calendar.getTime()));
				mainFrame.init();
				List<Variable> variableList = model.getList();
				Iterator<Variable> variableIterator = variableList.iterator();
				while (variableIterator.hasNext()) {
					Variable v = variableIterator.next();
					if (!v.load) {
						variableIterator.remove();
					}
				}
				Query query = new Query();
				for (Variable node : variableList) {
					if (node != null) {
						query.setTime(calendar);
						logger.info("run() node.name=" + node.name);
						query.addVariable(node.name);
						// Take Existing Node And Update Time w/ Calendar
						node.query.setTime(calendar);
						try {
							node.query();// discrete finite task that sets a new query, includes process
							while (!node.isComplete() && !Thread.interrupted()) {
								Thread.sleep(4000);
							}
							logger.info("query() node.isComplete()=" + node.isComplete());
						} catch (Exception e) {
							logger.error("step() e=" + e);
							e.printStackTrace();
						}
					}
				}

				if (!Thread.currentThread().isInterrupted()) {
					mainFrame.init();
					mainFrame.saveQuery(query);
					MemoryController.log();
					TimeController.stop();
				} else {
					break;
				}
				Thread.sleep(timeDelay);
			} catch (Exception e) {
				logger.error("step() e=" + e);
				e.printStackTrace();
			}
		}
	}

	public Calendar getCalendar() {
		Calendar calendar = this.model.calendar;
		String unit = (String) this.incrementUnitTimeComboBox.getSelectedItem();
		unit = unit.toLowerCase();
		switch (unit) {
		case "second": {
			this.timeUnit = Calendar.SECOND;
			break;
		}
		case "minute": {
			this.timeUnit = Calendar.MINUTE;
			break;
		}
		case "hour": {
			this.timeUnit = Calendar.HOUR;
			break;
		}
		case "day": {
			this.timeUnit = Calendar.DATE;
			break;
		}
		case "month": {
			this.timeUnit = Calendar.MONTH;
			break;
		}
		case "year": {
			this.timeUnit = Calendar.YEAR;
			break;
		}
		}
		if (this.forth) {
			calendar.add(this.timeUnit, this.timeIncrement);
		} else if (this.back) {
			calendar.add(this.timeUnit, -this.timeIncrement);
		}
		return calendar;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated
	// <editor-fold defaultstate="collapsed" desc="Generated
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		setButton = new javax.swing.JButton();
		stopButton = new javax.swing.JButton();
		forwardButton = new javax.swing.JButton();
		endTimeTextField = new javax.swing.JTextField();
		currentTimeBCCheckBox = new javax.swing.JCheckBox();
		backwardButton = new javax.swing.JButton();
		startTimeBCCheckBox = new javax.swing.JCheckBox();
		incrementUnitTimeComboBox = new javax.swing.JComboBox<>();
		endTimeBCCheckBox = new javax.swing.JCheckBox();
		startTimeTextField = new javax.swing.JTextField();
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

		startTimeBCCheckBox.setText("BC");

		endTimeBCCheckBox.setText("BC");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE)
								.addComponent(backwardButton).addGap(18, 18, 18).addComponent(stopButton)
								.addGap(18, 18, 18).addComponent(forwardButton))
						.addComponent(setButton, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(endTimeTextField).addComponent(startTimeTextField)
						.addComponent(incrementTimeTextField)
						.addComponent(incrementUnitTimeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)
						.addComponent(delayTimeTextField, javax.swing.GroupLayout.Alignment.TRAILING)
						.addComponent(currentTimeTextField, javax.swing.GroupLayout.Alignment.TRAILING))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(currentTimeBCCheckBox).addComponent(startTimeBCCheckBox)
						.addComponent(endTimeBCCheckBox))
				.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(currentTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(currentTimeBCCheckBox))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(startTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(startTimeBCCheckBox))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(endTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(endTimeBCCheckBox))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(incrementTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(incrementUnitTimeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(10, 10, 10)
						.addComponent(delayTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(setButton)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(stopButton)
								.addComponent(forwardButton, javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(backwardButton))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
	}// </editor-fold>//GEN-END:initComponents

	private void setButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_setButtonActionPerformed
		String time = this.currentTimeTextField.getText();
		String increment = this.incrementTimeTextField.getText();
		String sleep = this.delayTimeTextField.getText();
		String startTime = this.startTimeTextField.getText();
		String endTime = this.endTimeTextField.getText();
		String unit = (String) this.incrementUnitTimeComboBox.getSelectedItem();
		boolean currentTimeBC = this.currentTimeBCCheckBox.isSelected();
		boolean startTimeBC = this.startTimeBCCheckBox.isSelected();
		boolean endTimeBC = this.endTimeBCCheckBox.isSelected();
		this.timeIncrement = (!increment.isEmpty()) ? Integer.parseInt(increment) : 1;
		this.timeDelay = (!sleep.isEmpty()) ? Integer.parseInt(sleep) : 1000;
		if (!time.isEmpty()) {
			GregorianCalendar calendar = new GregorianCalendar();
			GregorianCalendar startCalendar = new GregorianCalendar();// Calendar.getInstance();
			GregorianCalendar endCalendar = new GregorianCalendar();// Calendar.getInstance();
			calendar.setTimeZone(TimeZone.getTimeZone(this.model.timeZone));
			startCalendar.setTimeZone(TimeZone.getTimeZone(this.model.timeZone));
			endCalendar.setTimeZone(TimeZone.getTimeZone(this.model.timeZone));
			calendar.setTime(Time.getDate(time));
			if (currentTimeBC)
				calendar.set(Calendar.ERA, GregorianCalendar.BC);
			startCalendar.setTime(Time.getDate(startTime));
			if (startTimeBC)
				startCalendar.set(Calendar.ERA, GregorianCalendar.BC);
			endCalendar.setTime(Time.getDate(endTime));
			if (endTimeBC)
				endCalendar.set(Calendar.ERA, GregorianCalendar.BC);
			this.model.setCalendar(calendar);
			this.model.setStartCalendar(startCalendar);
			this.model.setEndCalendar(endCalendar);
			this.model.getCamera().node.query.setIncrement(timeIncrement);
			this.model.getCamera().node.query.setUnit(unit);
			this.model.getCamera().node.query.setTime(calendar);
			this.model.getCamera().node.query.setWindow(startCalendar, endCalendar);
			this.model.query = this.model.getCamera().node.query;
			this.model.getCamera().node.query();
			this.mainFrame.init();
		}
	}// GEN-LAST:event_setButtonActionPerformed

	private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_stopButtonActionPerformed
		this.run = false;
	}// GEN-LAST:event_stopButtonActionPerformed

	private void forwardButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_forwardButtonActionPerformed
		this.back = false;
		this.forth = true;
		this.run = true;
		this.thread = new Thread(this);
		this.thread.start();
	}// GEN-LAST:event_forwardButtonActionPerformed

	private void backwardButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_backwardButtonActionPerformed
		this.forth = false;
		this.back = true;
		this.run = true;
		this.thread = new Thread(this);
		this.thread.start();
	}// GEN-LAST:event_backwardButtonActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton backwardButton;
	private javax.swing.JCheckBox currentTimeBCCheckBox;
	private javax.swing.JTextField currentTimeTextField;
	private javax.swing.JTextField delayTimeTextField;
	private javax.swing.JCheckBox endTimeBCCheckBox;
	private javax.swing.JTextField endTimeTextField;
	private javax.swing.JButton forwardButton;
	private javax.swing.JTextField incrementTimeTextField;
	private javax.swing.JComboBox<String> incrementUnitTimeComboBox;
	private javax.swing.JButton setButton;
	private javax.swing.JCheckBox startTimeBCCheckBox;
	private javax.swing.JTextField startTimeTextField;
	private javax.swing.JButton stopButton;
	// End of variables declaration//GEN-END:variables
}
//Script script = new Script();
//while (variableIterator.hasNext()) {
//	Variable v = variableIterator.next();
//	if (v.load) {
//		System.err.println("schedule() v=" + v);
//		Query query = new Query(v.query);
////		query.setCalendar(calendar);
//		script.queryList.add(query);
//
//	} else {
//		variableIterator.remove();
//	}
//}
//System.err.println("schedule() script=" + script.getJson());
//@Override
//public void run() {
//	while (this.run) {
//		try {
//			Calendar calendar = this.model.calendar;
//			String unit = (String) this.incrementUnitTimeComboBox.getSelectedItem();
//			unit = unit.toLowerCase();
//			switch (unit) {
//			case "second" : {
//				this.calendarUnit = Calendar.SECOND;
//				break;
//			}
//			case "minute": {
//				this.calendarUnit = Calendar.MINUTE;
//				break;
//			}
//			case "hour": {
//				this.calendarUnit = Calendar.HOUR;
//				break;
//			}
//			case "day": {
//				this.calendarUnit = Calendar.DATE;
//				break;
//			}
//			case "month": {
//				this.calendarUnit = Calendar.MONTH;
//				break;
//			}
//			case "year": {
//				this.calendarUnit = Calendar.YEAR;
//				break;
//			}
//			}
//			if (this.forward) {
//				calendar.add(this.calendarUnit, this.calendarIncrement);
//			} else if (this.backward) {
//				calendar.add(this.calendarUnit, -this.calendarIncrement);
//			}
//
//			this.model.setCalendar(calendar);
//			this.currentTimeTextField.setText(this.simpleDateFormat.format(this.model.calendar.getTime()));
//			this.mainFrame.init();
//			Thread.sleep(this.delay);
//		} catch (Exception e) {
//
//		}
//	}
//}
//this.model.query();
//this.model.data.setCalendar(calendar);// data uses calendar differently from terra.
