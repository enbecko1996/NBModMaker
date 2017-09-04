package com.enbecko.nbmodmaker.creator_3d.grids.raytrace;

import com.enbecko.nbmodmaker.creator_3d.grids.Grid;
import com.enbecko.nbmodmaker.creator_3d.grids.OverlyExtendedBlockStorage;
import com.enbecko.nbmodmaker.linalg.real.Vec3;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * Created by enbec on 21.02.2017.
 */
public class FirstOrderHolder extends CubicContentHolderGeometry implements ContentHolder<OverlyExtendedBlockStorage> {
    OverlyExtendedBlockStorage theContent;
    ContentHolder parent;

    public FirstOrderHolder(Grid parentGrid, Vec3 positionInGridCoords, boolean isMaxOrder, OverlyExtendedBlockStorage theContent) {
        super(parentGrid, positionInGridCoords, (byte) 1, isMaxOrder);
        this.theContent = theContent;
    }

    @Override
    public List<OverlyExtendedBlockStorage> getContent() {
        return Collections.singletonList(this.theContent);
    }

    @Override
    public boolean addChunk(OverlyExtendedBlockStorage chunk) {
        return false;
    }

    @Override
    public int getContentCount() {
        return 1;
    }

    @Override
    public boolean addNewChild(@Nonnull OverlyExtendedBlockStorage content) {
        return false;
    }

    @Override
    public boolean removeChild(@Nonnull OverlyExtendedBlockStorage content) {
        return false;
    }

    @Override
    public void askForOrderDegrade(@Nonnull OverlyExtendedBlockStorage asker, CubicContentHolderGeometry degradeTo) {

    }

    @Override
    public int getParentCount() {
        return 1;
    }

    @Override
    public ContentHolder getParent(int pos) {
        return this.parent;
    }

    public ContentHolder getParent() {
        return this.parent;
    }

    @Override
    public boolean addParent(ContentHolder higherOrderHolder) {
        if (higherOrderHolder != null) {
            this.parent = higherOrderHolder;
            return true;
        } else
            throw new RuntimeException("Can't make a null parent " + this);
    }

    public void setParent(HigherOrderHolder contentHolder) {
        this.parent = contentHolder;
    }

    /**
    @Override
    @SideOnly(Side.CLIENT)
    public void render(VertexBuffer buffer, LocalRenderSetting... localRenderSettings) {
        if (GlobalRenderSetting.getRenderMode() == GlobalRenderSetting.RenderMode.DEBUG)
            super.render(buffer);
        for (Content child : this.content) {
            child.render(buffer, localRenderSettings);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderContentWithExceptions(VertexBuffer buffer, @Nullable List<Content> exceptions, LocalRenderSetting... localRenderSettings) {
        if (GlobalRenderSetting.getRenderMode() == GlobalRenderSetting.RenderMode.DEBUG)
            super.render(buffer);
        for (Content child : this.content) {
            if(exceptions == null || !exceptions.contains(child))
                child.render(buffer);
        }
    }*/

    @Override
    public Content getRayTraceResult(RayTrace3D rayTrace3D) {
        /**
        double smallestDist = Double.POSITIVE_INFINITY;
        Content tmpResult = null;
        for (Content content : this.content) {
            Vec3 pos;
            if ((pos = content.checkIfCrosses(rayTrace3D)) != null) {
                double d = pos.subFromThis(rayTrace3D.getOnPoint()).length();
                if (d < smallestDist) {
                    smallestDist = d;
                    tmpResult = content;
                }
            }
        }
        return tmpResult; */
        return null;
    }

    public String toString() {
        return "\nFirstOrderHolder " + this.getGeometryInfo();
    }
}
