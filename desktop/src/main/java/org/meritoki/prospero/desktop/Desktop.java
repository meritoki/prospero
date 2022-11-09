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
package org.meritoki.prospero.desktop;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.desktop.model.Model;
import org.meritoki.prospero.desktop.view.frame.MainFrame;
import org.meritoki.prospero.desktop.view.window.SplashWindow;


public class Desktop {

	static Logger logger = LogManager.getLogger(Desktop.class.getName());
	public static String versionNumber = "0.7.202203";
	public static String vendor = "Meritoki";
	public static String about = "Version " + versionNumber + " Copyright " + vendor + " 2020-2022";
	public static Option versionOption = new Option("v", "version", false, "Print version information");
	public static Option helpOption = new Option("h", "help", false, "Print usage information");
	public static Option cacheOption = new Option("c", "cache", false, "Cache data between queries, requires memory");
	public static Option scriptPathOption = Option.builder("s").longOpt("script").desc("Option to input script file or folder path")
			.hasArg().build();
	public static String scriptPath = null;
	public static boolean mainFlag = false;
	public static boolean cacheFlag = false;

	public static void main(String args[]) {
		logger.info("Starting Prospero Desktop Application...");
		Options options = new Options();
		options.addOption(cacheOption);
		options.addOption(helpOption);
		options.addOption(scriptPathOption);
		options.addOption(versionOption);
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine commandLine = parser.parse(options, args);
			if (commandLine.hasOption("help")) {
				logger.info("main(args) help");
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("prospero", options, true);
			} else if (commandLine.hasOption("version")) {
				System.out.println(about);
			} else {
				if (commandLine.hasOption("script")) {
					scriptPath = commandLine.getOptionValue("script");
					logger.info("main(args) script=" + scriptPath);
				}
				if (commandLine.hasOption("cache")) {
					cacheFlag = true;
				}
				mainFlag = true;
			}
		} catch (org.apache.commons.cli.ParseException ex) {
			logger.error(ex);
		}

		if(mainFlag) {
			final MainFrame mainFrame = new MainFrame();
			final SplashWindow splashWindow = new SplashWindow("/Splash.png", mainFrame, 2000);
			final Model model = new Model();
			model.setCache(cacheFlag);
			try {
				model.setScriptPath(scriptPath);
				mainFrame.setModel(model);
				try {
					for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
						if ("Nimbus".equals(info.getName())) {
							javax.swing.UIManager.setLookAndFeel(info.getClassName());
							break;
						}
					}
				} catch (ClassNotFoundException ex) {
					logger.error(ex);
				} catch (InstantiationException ex) {
					logger.error(ex);
				} catch (IllegalAccessException ex) {
					logger.error(ex);
				} catch (javax.swing.UnsupportedLookAndFeelException ex) {
					logger.error(ex);
				}
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						mainFrame.setVisible(true);
					}
				});
			} catch (Exception e) {
				logger.error("Exception: "+e.getMessage());
			}
		}
		
	}
}
//FlatLightLaf.setup();
