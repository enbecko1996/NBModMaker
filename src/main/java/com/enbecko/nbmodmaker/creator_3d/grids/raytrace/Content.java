package com.enbecko.nbmodmaker.creator_3d.grids.raytrace;

import com.enbecko.nbmodmaker.LocalRenderSetting;
import com.enbecko.nbmodmaker.creator_3d.grids.Grid;
import com.enbecko.nbmodmaker.linalg.real.Vec4;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by enbec on 21.02.2017.
 */
public interface Content {

    public abstract boolean isColliding(Content other);

    public abstract boolean isFullInside(Content other);

    public abstract boolean isInside(Vec4 vec);

    public abstract Vec4 checkIfCrosses(RayTrace3D rayTrace3D);

    float getMaxX();

    float getMaxY();

    float getMaxZ();

    float getMinX();

    float getMinY();

    float getMinZ();

    boolean addParent(ContentHolder holder);

    Vec4 getPositionInGridCoords();

    public abstract static class ContentBase implements Content {
        protected final Vec4 positionInGridCoords;
        private final List<ContentHolder> parents = new ArrayList<ContentHolder>();
        @Nonnull
        protected final Grid parentGrid;
        private boolean canChangePosition;

        public ContentBase(Grid parentGrid, Vec4 positionInGridCoords, boolean canChangePosition) {
            this.positionInGridCoords = (Vec4) new Vec4(positionInGridCoords).setMuted(canChangePosition);
            this.parentGrid = parentGrid;
            this.canChangePosition = canChangePosition;
        }

        public ContentBase(Grid parentGrid, Vec4 positionInGridCoords) {
            this(parentGrid, positionInGridCoords, true);
        }

        public ContentBase(Grid parentGrid, ContentHolder parent, Vec4 positionInGridCoords) {
            this(parentGrid, positionInGridCoords);
            this.addParent(parent);
        }

        public void setCanChangePosition(boolean canChangePosition) {
            this.canChangePosition = canChangePosition;
            this.positionInGridCoords.setMuted(canChangePosition);
        }

        @SuppressWarnings("unchecked")
        void removeMe() {
            for (int k = 0; k < this.getParentCount(); k++)
                this.getParent(k).removeChild(this);
        }

        public void updatePosition(float x, float y, float z) {
            this.positionInGridCoords.update(x, y, z);
        }

        public void updatePosition(Vec4 other) {
            this.positionInGridCoords.update(other);
        }

        @Nonnull
        public Grid getParentGrid() {
            return this.parentGrid;
        }

        public Vec4 getPositionInGridCoords() {
            return this.positionInGridCoords;
        }

        public boolean addParent(ContentHolder contentHolder) {
            if (!this.parents.contains(contentHolder)) {
                this.parents.add(contentHolder);
                return true;
            }
            return false;
        }

        public int getParentCount() {
            return this.parents.size();
        }

        public ContentHolder getParent(int pos) {
            return this.parents.get(pos);
        }
    }
}
