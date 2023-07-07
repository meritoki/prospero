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
package org.meritoki.prospero.desktop.model.resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.meritoki.prospero.library.controller.node.NodeController;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class Resource {
	@JsonIgnore
	public List<String> recentList = new ArrayList<>();

	public Resource() {
		this.initRecentList();
	}

	public void initResourceCache() {
		File file = new File(NodeController.getResourceCache());
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public void addRecent(String recent) {
		File file = new File(NodeController.getResourceCache() + NodeController.getSeperator() + "recent.csv");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (!this.recentList.contains(recent)) {
			this.recentList.add(recent);
			NodeController.saveText(NodeController.getResourceCache(), "recent.csv", this.recentList);
		}
	}

	public void removeRecent(String recent) {
		if (this.recentList.contains(recent)) {
			this.recentList.remove(recent);
			NodeController.saveText(NodeController.getResourceCache(), "recent.csv", this.recentList);
		}
	}
	
	public void save() {
		NodeController.saveText(NodeController.getResourceCache(), "recent.csv", this.recentList);
	}

	public void initRecentList() {
		File file = new File(NodeController.getResourceCache() + NodeController.getSeperator() + "recent.csv");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		List<String[]> list = NodeController
				.openCsv(NodeController.getResourceCache() + NodeController.getSeperator() + "recent.csv");
		for (String[] stringArray : list) {
			for (String string : stringArray) {
				this.recentList.add(string);
			}
		}
	}
}
