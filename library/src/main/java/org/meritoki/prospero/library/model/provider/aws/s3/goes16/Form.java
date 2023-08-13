package org.meritoki.prospero.library.model.provider.aws.s3.goes16;

import java.util.ArrayList;
import java.util.List;

import org.meritoki.prospero.library.model.unit.Time;

public class Form {
	
	public List<Time> timeList = new ArrayList<>();
	public String bucket;
	public String prefix;
	public String outputPath;

}
