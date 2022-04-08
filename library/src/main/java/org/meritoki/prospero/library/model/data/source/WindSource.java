package org.meritoki.prospero.library.model.data.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.meritoki.prospero.library.model.unit.Frame;

public class WindSource extends Source {
	
	protected Map<String, List<Frame>> frameMap = new HashMap<>();
	
	public WindSource() {
	}
	
	public List<Frame> frameMapGet(int y, int m) throws Exception {
		if (this.frameMap == null)
			this.frameMap = new HashMap<>();
		List<Frame> eList = this.frameMap.get(y + "" + m);
		if (eList == null) {
			eList = this.read(y, m);
			if (eList != null) {
				this.frameMap.put(y + "" + m, eList);
			} else {
				eList = new ArrayList<>();
			}
		}
		eList = new ArrayList<>(eList);
		return eList;
	}
	
	public List<Frame> read(int year, int month) throws Exception {
		return null;
	}
}
//@Override
//public double getDimension(String dimension) throws Exception {
//	int d = 2;
//	if (dimension != null && !dimension.isEmpty()) {
//		boolean valid = true;
//		try {
//			d = Integer.parseInt(dimension);
//		} catch (NumberFormatException e) {
//			valid = false;
//		}
//		if (d <= 0 || d > 2) {
//			valid = false;
//		}
//		if (!valid) {
//			throw new Exception("invalid dimension format: " + dimension);
//		}
//	}
//	return d;
//}
