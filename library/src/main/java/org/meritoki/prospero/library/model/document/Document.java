package org.meritoki.prospero.library.model.document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.meritoki.prospero.library.model.node.query.Query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.meritoki.module.library.model.N;

public class Document {
	@JsonIgnore
	static Logger logger = LogManager.getLogger(Document.class.getName());
	@JsonProperty
	public String uuid;
	@JsonProperty
	public int index = 0;
	@JsonProperty
	public List<Query> queryList = new ArrayList<>();
	
	public Document() {
		this.uuid = UUID.randomUUID().toString();
	}
	
	public N getTree() {
		N root = new N("Document");
		List<String> sourceUUIDList = new ArrayList<>();
		List<String> timeList = new ArrayList<>();
		for(Query q: this.queryList) {
			String sourceUUID = q.getSourceUUID();
			root.getUserObject();
			String time = q.getTime();
			String variable = q.getVariable();
			if(!sourceUUIDList.contains(sourceUUID)) {
				sourceUUIDList.add(sourceUUID);
			}
		}
		return root;
	}
	
	/**
	 * Get the index of the current Query, used by Dialogs
	 * 
	 * @return
	 */
	@JsonIgnore
	public int getIndex() {
		return this.index;
	}

	@JsonIgnore
	public int getIndex(String uuid) {
		int index = 0;
		Query page;
		for (int i = 0; i < this.queryList.size(); i++) {
			page = this.queryList.get(i);
			if (page.uuid.equals(uuid)) {
				index = i;
				break;
			}
		}
		return index;
	}

	/**
	 * Functions gets Query object at current index from Query List
	 * 
	 * @return Query
	 */
	@JsonIgnore
	public Query getQuery() {
		int size = this.queryList.size();
		Query page = (this.index < size && size > 0) ? this.queryList.get(this.index) : null;
//		this.setBufferedImage(page);
		return page;
	}

	@JsonIgnore
	public Query getQuery(int index) {
		int size = this.queryList.size();
		Query page = (index < size && size > 0) ? this.queryList.get(index) : null;
		logger.debug("getQuery(" + index + ") page=" + page);
		return page;
	}

	@JsonIgnore
	public Query getQuery(String uuid) {
		Query page = null;
		for (Query p : this.queryList) {
			if (p.uuid.equals(uuid)) {
				page = p;
				break;
			}
		}
		return page;
	}

	@JsonIgnore
	public boolean setIndex(int index) {
		boolean flag = false;
		if (index >= 0 && index < this.queryList.size()) {
			this.index = index;
			flag = true;
		}
		return flag;
	}

	@JsonIgnore
	public boolean setQuery(String uuid) {
		logger.info("setQuery(" + uuid + ")");
		boolean flag = false;
		if (uuid != null) {
			Query page = null;
			for (int i = 0; i < this.queryList.size(); i++) {
				page = this.queryList.get(i);
				if (page.uuid.equals(uuid)) {
					flag = true;
					this.setIndex(i);
				}
			}
		}
		return flag;
	}

	@JsonIgnore
	public void setQueryList(List<Query> pageList) {
		logger.info("setQueryList(" + pageList + ")");
		this.queryList = pageList;
	}
}
