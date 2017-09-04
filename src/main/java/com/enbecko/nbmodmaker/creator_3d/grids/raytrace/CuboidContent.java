package com.enbecko.nbmodmaker.creator_3d.grids.raytrace;

import com.enbecko.nbmodmaker.GlobalSettings;
import com.enbecko.nbmodmaker.creator_3d.grids.Grid;
import com.enbecko.nbmodmaker.linalg.real.Vec3;

import javax.annotation.Nullable;

public abstract class CuboidContent extends Content.ContentBase {
    float xSize, ySize, zSize;

    protected CuboidContent(Grid parentGrid, Vec3 pos, float xSize, float ySize, float zSize) {
        super(parentGrid, pos, false);
        if (xSize >= 0 && ySize >= 0 && zSize >= 0) {
            this.xSize = xSize;
            this.ySize = ySize;
            this.zSize = zSize;
        } else {
            throw new RuntimeException("This doesn't create a valid cuboid: xSize = " + xSize + ", ySize = " + ySize + ", zSize = " + zSize);
        }
    }

    public void updateSize(float xSize, float ySize, float zSize) {
        if (xSize > 0 && ySize > 0 && zSize > 0) {
            this.xSize = xSize;
            this.ySize = ySize;
            this.zSize = zSize;
        } else {
            throw new RuntimeException("This doesn't create a valid cuboid: xSize = " + xSize + ", ySize = " + ySize + ", zSize = " + zSize);
        }
    }

    public void update(float xMin, float yMin, float zMin, float xMax, float yMax, float zMax) {
        this.xSize = xMax - xMin;
        this.ySize = yMax - yMin;
        this.zSize = zMax - zMin;
        if (!(xSize > 0 && ySize > 0 && zSize > 0)) {
            throw new RuntimeException("This doesn't create a valid cuboid: xSize = " + xSize + ", ySize = " + ySize + ", zSize = " + zSize);
        }
        this.updatePosition(xMin, yMin, zMin);
        this.updateSize(this.xSize, this.ySize, this.zSize);
    }

    public void update(Vec3 pos, float xSize, float ySize, float zSize) {
        float xMin = pos.getX(), yMin = pos.getY(), zMin = pos.getZ(),
                xMax = xMin + xSize, yMax = yMin + ySize, zMax = zMin + zSize;
        this.update(xMin, yMin, zMin, xMax, yMax, zMax);
    }

    @Override
    public float getMaxX() {
        return this.getPositionInGridCoords().getX() + this.xSize;
    }

    @Override
    public float getMaxY() {
        return this.getPositionInGridCoords().getY() + this.ySize;
    }

    @Override
    public float getMaxZ() {
        return this.getPositionInGridCoords().getZ() + this.zSize;
    }

    @Override
    public float getMinX() {
        return this.getPositionInGridCoords().getX();
    }

    @Override
    public float getMinY() {
        return this.getPositionInGridCoords().getY();
    }

    @Override
    public float getMinZ() {
        return this.getPositionInGridCoords().getZ();
    }

    /**
     * TODO
     * @param other
     * @return
     */
    public boolean isColliding(Content other) {
        return false;
    }

    public boolean isFullInside(Content other) {
        return false;
    }

    @Override
    public boolean isInside(Vec3 vec) {
        if ((vec.getY() < this.getMaxY() && vec.getY() >= this.getMinY() && vec.getX() < this.getMaxX() && vec.getX() >= this.getMinX() && vec.getZ() < this.getMaxZ() && vec.getZ() >= this.getMinZ()))
            return true;
        return false;
    }


    @Override
    @Nullable
    public Vec3 checkIfCrosses(RayTrace3D rayTrace3D) {
        Vec3 vec = rayTrace3D.getU();
        Vec3 out;
        /**if (vec.getX() > 0) {
            if ((out = this.getBoundingFace(Faces.FRONT_X).checkIfCrosses(rayTrace3D)) != null)
                return out;
        } else if (vec.getX() < 0)
            if ((out = this.getBoundingFace(Faces.BACK_X).checkIfCrosses(rayTrace3D)) != null)
                return out;
        if (vec.getY() > 0) {
            if ((out = this.getBoundingFace(Faces.BOTTOM_Y).checkIfCrosses(rayTrace3D)) != null)
                return out;
        } else if (vec.getY() < 0)
            if ((out = this.getBoundingFace(Faces.TOP_Y).checkIfCrosses(rayTrace3D)) != null)
                return out;
        if (vec.getZ() > 0) {
            if ((out = this.getBoundingFace(Faces.LEFT_Z).checkIfCrosses(rayTrace3D)) != null)
                return out;
        } else if (vec.getZ() < 0)
            if ((out = this.getBoundingFace(Faces.RIGHT_Z).checkIfCrosses(rayTrace3D)) != null)
                return out;*/
        return null;
    }

    /**@Override
    public FaceCrossPosAngle getCrossedFaceVecAndAngle(RayTrace3D rayTrace3D, BlockSetMode editMode) {
        Vec3 vec = rayTrace3D.getVec();
        Vec3 tmp;
        Polygon3D tmp2;
        if (vec.getX() > 0) {
            if ((tmp = (tmp2 = this.getBoundingFace(Faces.FRONT_X)).checkIfCrosses(rayTrace3D)) != null)
                return new FaceCrossPosAngle(tmp2, tmp, tmp2.getAngleAndAngleNormal(rayTrace3D));
        } else if (vec.getX() < 0)
            if ((tmp = (tmp2 = this.getBoundingFace(Faces.BACK_X)).checkIfCrosses(rayTrace3D)) != null)
                return new FaceCrossPosAngle(tmp2, tmp, tmp2.getAngleAndAngleNormal(rayTrace3D));
        if (vec.getY() > 0) {
            if ((tmp = (tmp2 = this.getBoundingFace(Faces.BOTTOM_Y)).checkIfCrosses(rayTrace3D)) != null)
                return new FaceCrossPosAngle(tmp2, tmp, tmp2.getAngleAndAngleNormal(rayTrace3D));
        } else if (vec.getY() < 0)
            if ((tmp = (tmp2 = this.getBoundingFace(Faces.TOP_Y)).checkIfCrosses(rayTrace3D)) != null)
                return new FaceCrossPosAngle(tmp2, tmp, tmp2.getAngleAndAngleNormal(rayTrace3D));
        if (vec.getZ() > 0) {
            if ((tmp = (tmp2 = this.getBoundingFace(Faces.LEFT_Z)).checkIfCrosses(rayTrace3D)) != null)
                return new FaceCrossPosAngle(tmp2, tmp, tmp2.getAngleAndAngleNormal(rayTrace3D));
        } else if (vec.getZ() < 0)
            if ((tmp = (tmp2 = this.getBoundingFace(Faces.RIGHT_Z)).checkIfCrosses(rayTrace3D)) != null)
                return new FaceCrossPosAngle(tmp2, tmp, tmp2.getAngleAndAngleNormal(rayTrace3D));
        return null;
    }*/

    public String getGeometryInfo() {
        return "{pos = \n" + this.positionInGridCoords + ", xSize = " + this.xSize +
                ", ySize = " + this.ySize + ", zSize = " + this.zSize + "}";
    }

    public String toString() {
        return this.hashCode() + "CuboidContent: " + this.getGeometryInfo();
    }

    public enum Faces {
        BACK_X, TOP_Y, RIGHT_Z, FRONT_X, BOTTOM_Y, LEFT_Z;
    }
}
