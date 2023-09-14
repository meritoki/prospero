package org.meritoki.prospero.library.model.provider.aws;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.meritoki.prospero.library.controller.node.NodeController;
import org.meritoki.prospero.library.model.provider.aws.s3.S3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meritoki.library.controller.model.provider.Provider;
import com.meritoki.library.controller.node.Exit;

public class AWS extends Provider {
	
	static Logger logger = LoggerFactory.getLogger(AWS.class.getName());
	
	public AWS() {
		super("aws");
	}
	
	public AWS(String name) {
		super(name);
	}
	
	public void init() throws Exception {
		if(NodeController.isLinux()) {
			Exit exit = NodeController.executeCommand(false, "aws --version");
			if(exit.value != 0) {
				exit = NodeController.executeCommand(true,"apt-get install awscli");
				if(exit.value == 0) {
					logger.info("init() AWS CLI Installed");
				}
			} else {
				logger.info("init() AWS CLI Installed");
				Object region = this.model.getSystem().getProperties().get("awsRegion");
				Object accessKeyID = this.model.getSystem().getProperties().get("awsAccessKeyID");
				Object secretAccessKey = this.model.getSystem().getProperties().get("awsSecretAccessKey");
				if(region instanceof String && accessKeyID instanceof String && secretAccessKey instanceof String) {
					configure((String)region,(String)accessKeyID,(String)secretAccessKey);
				} else {
					logger.info("init() AWS CLI Config and Credentials Missing");
				}
			}
		} else if(NodeController.isWindows()) {
			Exit exit = NodeController.executeCommand(false, "aws --version");
			if(exit.value != 0) {
				exit = NodeController.executeCommand(true,"msiexec.exe /i https://awscli.amazonaws.com/AWSCLIV2.msi /qn");
				if(exit.value == 0) {
					logger.info("init() AWS CLI Installed");
				}
			} else {
				logger.info("init() AWS CLI Installed");
				Object region = this.model.getSystem().getProperties().get("awsRegion");
				Object accessKeyID = this.model.getSystem().getProperties().get("awsAccessKeyID");
				Object secretAccessKey = this.model.getSystem().getProperties().get("awsSecretAccessKey");
				if(region instanceof String && accessKeyID instanceof String && secretAccessKey instanceof String) {
					configure((String)region,(String)accessKeyID,(String)secretAccessKey);
				} else {
					logger.info("init() AWS CLI Config and Credentials Missing");
				}
			}
		}
	}
	
	public void configure(String region, String accessKeyID, String secretAccessKey) {
		logger.info("configure("+region+", "+accessKeyID+", "+secretAccessKey+")");
		if(NodeController.isLinux()) {
			logger.info("configure("+region+", "+accessKeyID+", "+secretAccessKey+") Linux");
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("AWS_REGION=");
			stringBuilder.append(region);
			stringBuilder.append("\r\n");
			stringBuilder.append("AWS_ACCESS_KEY_ID=");
			stringBuilder.append(accessKeyID);
			stringBuilder.append("\r\n");
			stringBuilder.append("AWS_SECRET_ACCESS_KEY=");
			stringBuilder.append(secretAccessKey);
			stringBuilder.append("\r\n");
			NodeController.saveText("./","vars.sh",stringBuilder);
			try {
				URL inputUrl = getClass().getClassLoader().getResource("configure-aws.sh");
				File dest = new File("./configure-aws.sh");
				FileUtils.copyURLToFile(inputUrl, dest);
				Exit exit = NodeController.executeCommand("chmod 755 configure-aws.sh");
				exit = NodeController.executeCommand("./configure-aws.sh");
				if(exit.value == 0) {
					logger.info("configure("+region+", "+accessKeyID+", "+secretAccessKey+") AWS Configured");
					File file = new File("./configure-aws.sh");
					file.delete();
					file = new File("./vars.sh");
					file.delete();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
