/*
 * Copyright 2016-2022 Joaquin Osvaldo Rodriguez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meritoki.prospero.library.model.node.data.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.meritoki.prospero.library.model.node.query.Query;
import org.meritoki.prospero.library.model.unit.Frame;
import org.meritoki.prospero.library.model.unit.Interval;
import org.meritoki.prospero.library.model.unit.Mode;
import org.meritoki.prospero.library.model.unit.Result;
import org.meritoki.prospero.library.model.unit.Time;

public class WindSource extends Source {
	
	protected Map<String, List<Frame>> frameMap = new HashMap<>();
	
	public WindSource() {
		super();
	}
	
	@Override
	public void query(Query query) throws Exception {
		this.intervalList = query.getIntervalList(this.getStartYear(), this.getEndYear());
		if (this.intervalList != null) {
			for (Interval i : this.intervalList) {
				this.load(query, i);
			}
			query.objectListAdd(new Result(Mode.COMPLETE));
		}
	}
	
	public void load(Query query, Interval interval) throws Exception {
		List<Time> timeList = Time.getTimeList(interval);
		List<Frame> loadList;
		for(Time time: timeList) {
			if (!Thread.interrupted()) {
				loadList = this.read(time.year, time.month);
				Result result = new Result();
				result.map.put("time", time);
				result.map.put("frameList", new ArrayList<Frame>((loadList)));
				query.objectList.add(result);
			} else {
				throw new InterruptedException();
			}
		}
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
