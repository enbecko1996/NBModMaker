package com.enbecko.nbmodmaker.creator_3d.grids;

import com.enbecko.nbmodmaker.GlobalSettings;
import com.enbecko.nbmodmaker.MathHelper;
import com.enbecko.nbmodmaker.linalg.real.Matrix_4x4;
import com.enbecko.nbmodmaker.linalg.real.Real_Vec_n;
import com.enbecko.nbmodmaker.linalg.real.Vec3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by enbec on 15.08.2017.
 */
public class Bone {
    /**
     * Rotation is in a right-handed coordinate system with the
     * roll-axis as xInGrid, yaw as zInGrid und pitch as yInGrid.
     */
    private double yaw, pitch, roll, axisAngle;
    private Vec3 rotationAxis;
    private Matrix_4x4 rotation, translation, scale, offset, combined, inverseCombined;
    private int boneID;
    private final String name;
    private boolean isRegistered = false;

    private HashMap<Integer, Grid> grids = new HashMap<>();

    public Bone(String name) {
        this.init();
        this.name = name;
        /**
         * TEST:
         */
        //System.out.println("wrong: " + g.getBlockState(190, 2, -118));
        //System.out.println(g.getBlockState(16, 1, -118));
        //System.out.println(g);
    }

    public void init() {
        this.rotationAxis = new Vec3();
        this.rotation = Matrix_4x4.MATRIXFACTORY.makeIdent(0);
        this.translation = Matrix_4x4.MATRIXFACTORY.makeIdent(0);
        this.scale = Matrix_4x4.MATRIXFACTORY.makeIdent(0);
        this.offset = Matrix_4x4.MATRIXFACTORY.makeIdent(0);
        this.combined = Matrix_4x4.MATRIXFACTORY.makeIdent(0);
        this.inverseCombined = Matrix_4x4.MATRIXFACTORY.makeIdent(0);
    }

    public final Bone setBoneID(int id) {
        if (!this.isRegistered) {
            this.boneID = id;
            this.isRegistered = true;
        } else
            throw new RuntimeException("Can't set new boneID to \n" + this + "\n because it's already registered");
        return this;
    }

    public boolean isRegistered() {
        return this.isRegistered;
    }

    public int getBoneID() {
        return this.boneID;
    }

    public Set<Integer> getGridSizes() {
        return this.grids.keySet();
    }

    public Grid getGridBySize(int size) {
        return this.grids.get(size);
    }

    public IBlockState getAtBoneCoords(float x, float y, float z) {
        return this.getAtBoneCoords(x, y, z, -1);
    }

    @Nullable
    private Grid initializeNewGrid(int size) {
        if (!this.grids.containsKey(size) && MathHelper.isPowerOf2(size)) {
            Grid g;
            this.grids.put(size, (g = new Grid(size)));
            return g;
        }
        return null;
    }

    public IBlockState getAtGridCoords(int x, int y, int z, int gridSize) {
        Grid grid = this.grids.get(gridSize);
        if (grid != null) {
            return grid.getBlockState(x, y, z);
        } else
            return Blocks.AIR.getDefaultState();
    }

    /**
     * xInGrid, yInGrid, zInGrid is in bone coord. system.
     * A grid size of GlobalSettings.unitGridSize() is one unit.
     */
    public IBlockState getAtBoneCoords(float x, float y, float z, int gridSize) {
        switch (gridSize) {
            case -1:
                IBlockState tmpOut = Blocks.AIR.getDefaultState();
                for (Grid grid : this.grids.values()) {
                    float mul = GlobalSettings.unitGridSize() / grid.getSize();
                    if (grid.getBlockState(x * mul, y * mul, z * mul) != Blocks.AIR.getDefaultState()) {
                        if (tmpOut == Blocks.AIR.getDefaultState())
                            tmpOut = grid.getBlockState(x * mul, y * mul, z * mul);
                        else {
                            /**
                             * TODO
                             * there might be a problem, because to grids have something on the same spot.
                             */
                        }
                    }
                }
                return tmpOut;
            default:
                Grid grid = this.grids.get(gridSize);
                if (grid != null) {
                    float mul = GlobalSettings.unitGridSize() / grid.getSize();
                    return grid.getBlockState(x * mul, y * mul, z * mul);
                } else
                    return Blocks.AIR.getDefaultState();
        }
    }

    public boolean canSetAtCoordsInGrid(int gridSize, int x, int y, int z) {
        Grid grid = this.grids.get(gridSize);
        return grid == null || grid.canSetAt(x, y, z);
    }

    /**
     *
     * xInGrid, yInGrid, zInGrid are grid coordinates in the gridSize.
     *
     */
    public boolean setAtCoordsInGrid(int x, int y, int z, int gridSize, IBlockState blockState) {
        Grid grid;
        if ((grid = this.grids.get(gridSize)) != null) {
            grid.setAt(x, y, z, blockState);
            return true;
        } else {
            Grid g = this.initializeNewGrid(gridSize);
            if (g != null) {
                g.setAt(x, y, z, blockState);
                return true;
            }
        }
        return false;
    }

    /**
     *
     * xInGrid, yInGrid, zInGrid are bone coordinates.
     *
     */
    public boolean setAtCoordsInBone(float x, float y, float z, int gridSize, IBlockState blockState) {
        float mul = GlobalSettings.unitGridSize() / gridSize;
        return this.setAtCoordsInGrid((int) Math.floor(x * mul), (int) Math.floor(y * mul), (int) Math.floor(z * mul), gridSize, blockState);
    }

    public void move(float dX, float dY, float dZ) {
        this.translation.setAt(0, 3, this.translation.getAt(0, 3) + dX);
        this.translation.setAt(1, 3, this.translation.getAt(1, 3) + dY);
        this.translation.setAt(2, 3, this.translation.getAt(2, 3) + dZ);
    }

    public void moveTo(float x, float y, float z) {
        this.translation.setAt(0, 3, x);
        this.translation.setAt(1, 3, y);
        this.translation.setAt(2, 3, z);
    }

    public void incYawPitchRoll(double dYaw, double dPitch, double dRoll) {
        this.yaw += Math.toRadians(dYaw);
        this.pitch += Math.toRadians(dPitch);
        this.roll += Math.toRadians(dRoll);
        this.setYawPitchRoll(this.yaw, this.pitch, this.roll);
    }

    /**
     * GEGEN den Uhrzeigersinn.
     *
     * @param yaw   around zInGrid-axis
     * @param pitch around yInGrid-axis
     * @param roll  around xInGrid-axis
     */
    public void setYawPitchRoll(double yaw, double pitch, double roll) {
        this.yaw = Math.toRadians(yaw);
        this.pitch = Math.toRadians(pitch);
        this.roll = Math.toRadians(roll);
        double c1 = Math.cos(this.yaw), c2 = Math.cos(this.pitch), c3 = Math.cos(this.roll);
        double s1 = Math.sin(this.yaw), s2 = Math.sin(this.pitch), s3 = Math.sin(this.roll);
        this.rotation.setAt(0, 0, (float) (c2 * c1));
        this.rotation.setAt(0, 1, (float) (s3 * s2 * c1 - c3 * s1));
        this.rotation.setAt(0, 2, (float) (s3 * s1 + c3 * s2 * c1));

        this.rotation.setAt(1, 0, (float) (c2 * s1));
        this.rotation.setAt(1, 1, (float) (c3 * c1 + s3 * s2 * s1));
        this.rotation.setAt(1, 2, (float) (c3 * s2 * s1 - s3 * c1));

        this.rotation.setAt(2, 0, (float) -s2);
        this.rotation.setAt(2, 1, (float) (s3 * c2));
        this.rotation.setAt(2, 2, (float) (c3 * c2));
    }

    public void incRollPitchYaw(double dRoll, double dPitch, double dYaw) {
        this.yaw += Math.toRadians(dYaw);
        this.pitch += Math.toRadians(dPitch);
        this.roll += Math.toRadians(dRoll);
        this.setRollPitchYaw(this.roll, this.pitch, this.yaw);
    }

    public void setRollPitchYaw(double roll, double pitch, double yaw) {
        this.yaw = Math.toRadians(yaw);
        this.pitch = Math.toRadians(pitch);
        this.roll = Math.toRadians(roll);
        double c1 = Math.cos(this.yaw), c2 = Math.cos(this.pitch), c3 = Math.cos(this.roll);
        double s1 = Math.sin(this.yaw), s2 = Math.sin(this.pitch), s3 = Math.sin(this.roll);
        this.rotation.setAt(0, 0, (float) (c1 * c2));
        this.rotation.setAt(0, 1, (float) (-s1 * c2));
        this.rotation.setAt(0, 2, (float) (s2));

        this.rotation.setAt(1, 0, (float) (s1 * c3 + c1 * s2 * s3));
        this.rotation.setAt(1, 1, (float) (c1 * c3 - s1 * s2 * s3));
        this.rotation.setAt(1, 2, (float) (-s3 * c2));

        this.rotation.setAt(2, 0, (float) (s1 * s3 - c1 * s2 * c3));
        this.rotation.setAt(2, 1, (float) (c1 * s3 + s1 * s2 * c3));
        this.rotation.setAt(2, 2, (float) (c2 * c3));
    }

    public void incRotationAroundCurrentAxis(double dAngle) {
        this.axisAngle += dAngle;
        this.setRotationAroundAxis(this.rotationAxis, this.axisAngle);
    }

    public void setRotationAroundAxis(Real_Vec_n axis, double angle) {
        ((Real_Vec_n) this.rotationAxis.fillWithContent(axis)).normalize();
        this.axisAngle = angle;
        double theta = Math.toRadians(angle);
        double c = Math.cos(theta), s = Math.sin(theta), x = this.rotationAxis.getAt(0), y = this.rotationAxis.getAt(1), z = this.rotationAxis.getAt(2);
        this.rotation.setAt(0, 0, (float) (c + Math.pow(x, 2) * (1 - c)));
        this.rotation.setAt(0, 1, (float) (x * y * (1 - c) - z * s));
        this.rotation.setAt(0, 2, (float) (x * z * (1 - c) + y * s));

        this.rotation.setAt(1, 0, (float) (y * x * (1 - c) + z * s));
        this.rotation.setAt(1, 1, (float) (c + Math.pow(y, 2) * (1 - c)));
        this.rotation.setAt(1, 2, (float) (y * z * (1 - c) - x * s));

        this.rotation.setAt(2, 0, (float) (z * x * (1 - c) - y * s));
        this.rotation.setAt(2, 1, (float) (z * y * (1 - c) + x * s));
        this.rotation.setAt(2, 2, (float) (c + Math.pow(z, 2) * (1 - c)));
    }

    public void setRotationAroundAxis(float x, float y, float z, float angle) {
        this.rotationAxis.fillWithContent(x, y, z);
        this.axisAngle = angle;
        this.setRotationAroundAxis(this.rotationAxis, angle);
    }

    public void addRotation(byte axis, double angle) {
        double s = Math.sin(Math.toRadians(angle));
        double c = Math.cos(Math.toRadians(angle));
        switch (axis) {
            case 0:
                float r10 = this.rotation.getAt(1, 0), r11 = this.rotation.getAt(1, 1), r12 = this.rotation.getAt(1, 2);
                float r20 = this.rotation.getAt(2, 0), r21 = this.rotation.getAt(2, 1), r22 = this.rotation.getAt(2, 1);
                this.rotation.setAt(1, 0, (float) (r10 * c - r20 * s));
                this.rotation.setAt(1, 1, (float) (r11 * c - r21 * s));
                this.rotation.setAt(1, 2, (float) (r12 * c - r22 * s));

                this.rotation.setAt(2, 0, (float) (r10 * s + r20 * c));
                this.rotation.setAt(2, 1, (float) (r11 * s + r21 * c));
                this.rotation.setAt(2, 2, (float) (r12 * s + r22 * c));
                return;
            /**
             * TODO
             */
        }
    }

    public void scale(float dX, float dY, float dZ) {
        this.scale.setAt(0, 0, this.scale.getAt(0, 0) + dX);
        this.scale.setAt(1, 1, this.scale.getAt(1, 1) + dY);
        this.scale.setAt(2, 2, this.scale.getAt(2, 2) + dZ);
    }

    public void setScale(float x, float y, float z) {
        this.scale.setAt(0, 0, x);
        this.scale.setAt(1, 1, y);
        this.scale.setAt(2, 2, z);
    }

    public void offset(float dX, float dY, float dZ) {
        this.offset.setAt(0, 3, this.offset.getAt(0, 3) + dX);
        this.offset.setAt(1, 3, this.offset.getAt(1, 3) + dY);
        this.offset.setAt(2, 3, this.offset.getAt(2, 3) + dZ);

    }

    public void setOffset(float x, float y, float z) {
        this.offset.setAt(0, 3, x);
        this.offset.setAt(1, 3, y);
        this.offset.setAt(2, 3, z);
    }

    public void readFromNBT(NBTTagCompound compound) {
        int[] gridSizes = compound.getIntArray("gridSizes_" + this.boneID);
        for (int gridSize : gridSizes) {
            Grid g;
            this.grids.put(gridSize, (g = new Grid(gridSize)));
            g.readFromNBT(compound, boneID);
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        List<Integer> sizes = new ArrayList<>();
        for (Grid grid : this.grids.values()) {
            if (grid != null) {
                sizes.add(grid.getSize());
                grid.writeToNBT(compound, this.boneID);
            }
        }
        int[] s = MathHelper.toIntArray(sizes);
        compound.setIntArray("gridSizes_" + this.boneID, s);
        return compound;
    }

    public String getBoneName() {
        return this.name;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\nBoneID: " + this.boneID + "\n");
        builder.append("BoneName: " + this.name + "\n");
        builder.append("Registerd: " + this.isRegistered + "\n");
        for (Grid grid : this.grids.values()) {
            builder.append(grid.toString());
            builder.append("\n");
        }
        return builder.toString();
    }
}
