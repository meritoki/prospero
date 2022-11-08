package org.meritoki.prospero.library.model.unit;

import org.apache.commons.math3.ml.clustering.Clusterable;

public class TileWrapper implements Clusterable {

    private double[] point;
    private Tile tile;

    public TileWrapper(Tile tile) {
        this.tile = tile;
        this.point = new double[] { tile.value };
    }

    public Tile getTile() {
        return tile;
    }
    
	@Override
	public double[] getPoint() {
		return point;
	}

}
