package com.enbecko.nbmodmaker.creator_3d.grids.raytrace;

import com.enbecko.nbmodmaker.creator_3d.grids.Grid;
import com.enbecko.nbmodmaker.linalg.real.Vec4;

/**
 * Created by enbec on 21.02.2017.
 */
public abstract class CubicContent extends CuboidContent {
    protected CubicContent(Grid parentGrid, Vec4 positonInGridCoords, float size) {
        super(parentGrid, positonInGridCoords, size, size, size);
    }

    @Override
    public void updateSize(float xSize, float ySize, float zSize) {
        throw new RuntimeException("This is no valid method for a CUBIC content");
    }

    public void updateSize(int size) {
        super.updateSize(size, size, size);
    }
}
