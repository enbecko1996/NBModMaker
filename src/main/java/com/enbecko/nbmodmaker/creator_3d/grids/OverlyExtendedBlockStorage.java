package com.enbecko.nbmodmaker.creator_3d.grids;

import com.enbecko.nbmodmaker.creator_3d.grids.raytrace.Content;
import com.enbecko.nbmodmaker.creator_3d.grids.raytrace.ContentHolder;
import com.enbecko.nbmodmaker.creator_3d.grids.raytrace.RayTrace3D;
import com.enbecko.nbmodmaker.linalg.real.Vec4;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Created by enbec on 16.08.2017.
 */
public class OverlyExtendedBlockStorage extends ExtendedBlockStorage implements Content{
    private int x, y, z;
    private final Vec4 posInGrid;

    public OverlyExtendedBlockStorage(int x, int y, int z, boolean storeSkylight) {
        super(y, storeSkylight);
        this.x = x;
        this.y = y;
        this.z = z;
        this.posInGrid = new Vec4(x, y, z, 1);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    if (this.get(x, y, z) != Blocks.AIR.getDefaultState())
                        builder.append("xInBone = " + x + ", yInBone = " + y + ", zInBone = " + z + ", block = " + this.get(x, y, z) + "\n");
                }
            }
        }
        return builder.toString();
    }

    @Override
    public boolean isColliding(Content other) {
        return false;
    }

    @Override
    public boolean isFullInside(Content other) {
        return false;
    }

    @Override
    public boolean isInside(Vec4 vec) {
        return false;
    }

    @Override
    public Vec4 checkIfCrosses(RayTrace3D rayTrace3D) {
        return null;
    }

    @Override
    public float getMaxX() {
        return 1;
    }

    @Override
    public float getMaxY() {
        return 1;
    }

    @Override
    public float getMaxZ() {
        return 1;
    }

    @Override
    public float getMinX() {
        return 0;
    }

    @Override
    public float getMinY() {
        return 0;
    }

    @Override
    public float getMinZ() {
        return 0;
    }

    @Override
    public boolean addParent(ContentHolder holder) {
        return false;
    }

    @Override
    public Vec4 getPositionInGridCoords() {
        return posInGrid;
    }
}
