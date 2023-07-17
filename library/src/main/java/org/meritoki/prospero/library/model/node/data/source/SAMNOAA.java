package org.meritoki.prospero.library.model.node.data.source;

import java.util.ArrayList;

import org.meritoki.prospero.library.model.unit.Region;

public class SAMNOAA extends NOAA {
	
	public SAMNOAA() {
		super();
		this.downloadURL = "https://psl.noaa.gov/data/20thC_Rean/timeseries/monthly/SAM/sam.20crv3.long.data";
		this.setRelativePath("NOAA"+seperator+"SAM"+seperator);
		this.setDownloadPath("NOAA"+seperator+"SAM"+seperator);
		this.setFileName("sam.20crv3.long.data");
		Region region = new Region(-65,-180,-40, 180);
		this.regionList = new ArrayList<>();
		this.regionList.add(region);
	}

}
