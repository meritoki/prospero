package org.meritoki.prospero.library.model.vendor.r;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meritoki.library.controller.model.vendor.Vendor;
import com.meritoki.library.controller.node.Exit;
import com.meritoki.library.controller.node.NodeController;

public class R extends Vendor {

	static Logger logger = LoggerFactory.getLogger(R.class.getName());

	public R() {
		super("R");
		// TODO Auto-generated constructor stub
	}

	public R(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() throws Exception {
		// Check for R Installation, Provide Scripts to Install
		if (NodeController.isLinux()) {
			logger.debug("init() Linux");
			Exit exit = NodeController.executeCommand("R --version");
			if (exit.value != 0) {
				URL inputUrl = getClass().getResource("install-R.sh");
				File dest = new File("./install-R.sh");
				FileUtils.copyURLToFile(inputUrl, dest);
				exit = NodeController.executeCommand("chmod 755 install-R.sh");
				throw new Exception("R Not Installed");
			} else {
				logger.info("init() R Installed");
				URL inputUrl = getClass().getResource("/prospero-find.R");
				File dest = new File("./prospero-find.R");
				FileUtils.copyURLToFile(inputUrl, dest);
				exit = NodeController.executeCommand("Rscript prospero-find.R");
				if (exit.value != 0) {
					logger.info("init() Installing R Dependencies");
					inputUrl = getClass().getResource("/prospero-install.R");
					dest = new File("./prospero-install.R");
					FileUtils.copyURLToFile(inputUrl, dest);
					exit = NodeController.executeCommand(true, "Rscript prospero-install.R", 60 * 15);
					if (exit.value != 0) {
						throw new Exception("Install R Dependencies Failed");
					} else {
						logger.info("init() R Dependencies Installed");
					}
				} else {
					logger.info("init() R Dependencies Installed");
				}
			}
		} else if (NodeController.isWindows()) {
			Exit exit = NodeController.executeCommand("R --version");
			if (exit.value != 0) {
				throw new Exception("R Not Installed");
			} else {
				logger.info("init() R Installed");
			}
		}
	}

}
//exit = NodeController.executeCommand("./install-R.sh");
//if(exit.value == 0) {
//	//TODO
//}