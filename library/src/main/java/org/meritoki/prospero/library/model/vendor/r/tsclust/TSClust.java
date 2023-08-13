package org.meritoki.prospero.library.model.vendor.r.tsclust;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.meritoki.prospero.library.model.vendor.r.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meritoki.library.controller.node.Exit;
import com.meritoki.library.controller.node.NodeController;

public class TSClust extends R {
	
	static Logger logger = LoggerFactory.getLogger(TSClust.class.getName());

	public TSClust() {
		super("TSClust");
		// TODO Auto-generated constructor stub
	}
	
	public TSClust(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void init() throws Exception {
		super.init();
		//If Super Goes Well, Download and Install TSClust and Dependencies
		URL inputUrl = getClass().getResource("/tsclust-find.R");
		File dest = new File("./tsclust-find.R");
		FileUtils.copyURLToFile(inputUrl, dest);
		Exit exit = NodeController.executeCommand("Rscript tsclust-find.R");
		if(exit.value != 0) {
			logger.info("init() Installing R TSclust");
			inputUrl = getClass().getResource("/TSclust_1.2.3.tar.gz");
			dest = new File("./TSclust_1.2.3.tar.gz");
			FileUtils.copyURLToFile(inputUrl, dest);
			exit = NodeController.executeCommand(true,"R CMD INSTALL ./TSclust_1.2.3.tar.gz");
		} else {
			logger.info("init() R TSclust Installed");
		}
	}

	//Wrap Calls to comparison.R, Make More Advanced Calls
}
