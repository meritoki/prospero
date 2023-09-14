package org.meritoki.prospero.library.model.node.data.source;

public class AMONOAA extends NOAA {
	
	public AMONOAA() {
		super();
		this.downloadURL = "https://psl.noaa.gov/data/correlation/amon.sm.long.data";
		this.setRelativePath("NOAA"+seperator+"AMO"+seperator);
		this.setDownloadPath("NOAA"+seperator+"AMO"+seperator);
		this.setFileName("amon.sm.long.data");
	}
}
