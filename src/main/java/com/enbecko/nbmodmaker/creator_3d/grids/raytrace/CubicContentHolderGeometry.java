package com.enbecko.nbmodmaker.creator_3d.grids.raytrace;

import com.enbecko.nbmodmaker.creator_3d.grids.Grid;
import com.enbecko.nbmodmaker.creator_3d.minecraft.Creator_Main;
import com.enbecko.nbmodmaker.linalg.real.Vec4;

/**
 * Created by enbec on 25.02.2017.
 */
public abstract class CubicContentHolderGeometry extends CubicContent {
    private byte order;
    private boolean isMaxOrder;
    private int size;

    protected CubicContentHolderGeometry(Grid parentGrid, Vec4 positionInGridCoords, byte order, boolean isMaxOrder) {
        super(parentGrid, positionInGridCoords, (int) Math.pow(Creator_Main.contentCubesPerCube, order));
        this.setCanChangePosition(false);
        this.size = (int) Math.pow(Creator_Main.contentCubesPerCube, order);
        this.order = order;
        this.isMaxOrder = isMaxOrder;
    }

    public CubicContentHolderGeometry setMaxOrder(boolean order) {
        this.isMaxOrder = order;
        return this;
    }

    public abstract Content getRayTraceResult(RayTrace3D rayTrace3D);

    public byte getOrder() {
        return this.order;
    }

    @Override
    public Vec4 getPositionInGridCoords() {
        return this.positionInGridCoords;
    }

    /**
    @Override
    @SideOnly(Side.CLIENT)
    public void manipulateMe(ManipulatingEvent event, RayTrace3D rayTrace3D) {

    }*/

    public int getSize() {
        return this.size;
    }

    /**
    public abstract void renderContentWithExceptions(VertexBuffer buffer, @Nullable List<Content> exceptions, LocalRenderSetting ... localRenderSettings);
    */
}
