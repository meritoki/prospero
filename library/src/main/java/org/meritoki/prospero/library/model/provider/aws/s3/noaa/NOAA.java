package org.meritoki.prospero.library.model.provider.aws.s3.noaa;

import org.meritoki.prospero.library.model.provider.aws.s3.S3;

public class NOAA extends S3 {

	public NOAA() {
		super("noaa");
	}
	
	public void init() throws Exception {
		super.init();
	}
}
//listBucketObjects(getClient(), "noaa-goes17","ABI-L2-MCMIPF/2019/001/12/");
//this.keyList = listBucketObjects(client, "noaa-goes16");