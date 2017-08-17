package com.enbecko.nbmodmaker.creator_3d.grids;

import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

/**
 * Created by enbec on 16.08.2017.
 */
public class OverlyExtendedBlockStorage extends ExtendedBlockStorage {
    private int x, y, z;

    public OverlyExtendedBlockStorage(int x, int y, int z, boolean storeSkylight) {
        super(y, storeSkylight);
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
