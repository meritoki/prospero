package org.meritoki.prospero.library.model.vendor.r;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.meritoki.prospero.library.model.vendor.Vendor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public void init() {
		//Check for R Installation, Provide Scripts to Install
		if(NodeController.isLinux()) {
			logger.debug("init() Linux");
			try {
				Exit exit = NodeController.executeCommand("R --version");
				if(exit.value != 0) {
					URL inputUrl = getClass().getResource("install-R.sh");
					File dest = new File("./install-R.sh");
					FileUtils.copyURLToFile(inputUrl, dest);
					exit = NodeController.executeCommand("chmod 755 install-R.sh");
					exit = NodeController.executeCommand("./install-R.sh");
					if(exit.value == 0) {
						//TODO
					}
				} else {
					logger.info("init() R Installed");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
