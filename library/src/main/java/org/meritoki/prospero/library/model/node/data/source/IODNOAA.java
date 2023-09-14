package org.meritoki.prospero.library.model.node.data.source;

public class IODNOAA extends NOAA {
	
	public IODNOAA() {
		super();
		this.downloadURL = "https://psl.noaa.gov/gcos_wgsp/Timeseries/Data/dmi.had.long.data";
		this.setDownloadPath("NOAA"+seperator+"IOD"+seperator);
		this.setRelativePath("NOAA"+seperator+"IOD"+seperator);
		this.setFileName("dmi.had.long.data");
	}

}
