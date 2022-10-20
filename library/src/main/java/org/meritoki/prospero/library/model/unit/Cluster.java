package org.meritoki.prospero.library.model.unit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.table.TableModel;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.meritoki.prospero.library.model.table.Table;

public class Cluster {
	
	public String uuid;
	public Integer id;
	public List<Tile> tileList = new ArrayList<>();
	
	public Cluster() {
		this.uuid = UUID.randomUUID().toString();
	}
	
	public static TableModel getTableModel(List<Cluster> clusterList) {
		Object[] objectArray = getObjectArray(clusterList);
		return new javax.swing.table.DefaultTableModel((Object[][]) objectArray[1], (Object[]) objectArray[0]);
	}

	public static Object[] getObjectArray(List<Cluster> clusterList) {
		Object[] objectArray = new Object[2];
		Object[] columnArray = new Object[0];
		Object[][] dataMatrix = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		if (clusterList != null) {
			if (clusterList.size() > 0) {
				for (int i = 0; i < clusterList.size(); i++) {
					Cluster e = clusterList.get(i);
					if (e instanceof Cluster) {
						Cluster cluster = (Cluster) e;
						if (i == 0) {
							columnArray = Table.getColumnNames(3).toArray();
							dataMatrix = new Object[clusterList.size() + 1][3];
							dataMatrix[i][0] = "uuid";
							dataMatrix[i][1] = "id";
							dataMatrix[i][2] = "tileCount";
						}
						dataMatrix[i + 1][0] = cluster.uuid;
						dataMatrix[i + 1][1] = cluster.id;
						dataMatrix[i + 1][2] = cluster.tileList.size();
					}
				}
			}
			objectArray[0] = columnArray;
			objectArray[1] = dataMatrix;
		}
		return objectArray;
	}
	
	public boolean contains(Tile tile) {
		boolean flag = false;
		for(Tile t: this.tileList) {
			if(t.equals(tile)) {
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	public int getID() {
		return (id != null)?id:0;
	}
	
	public boolean setTile(Tile tile) {
		boolean flag = false;
		for(Tile t: this.tileList) {
			if(t.equals(tile)) {
				flag = true;
				t.value = tile.value;
				break;
			}
		}
		return flag;
	}
	
	public double getAverageValue() {
		StandardDeviation standardDeviation = new StandardDeviation();
		Mean mean = new Mean();
		for (Tile tile : this.tileList) {
			standardDeviation.increment(tile.value);
			mean.increment(tile.value);
		}
		double value = mean.getResult();
		return value;
	}

}
