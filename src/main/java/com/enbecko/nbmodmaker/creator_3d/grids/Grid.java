package com.enbecko.nbmodmaker.creator_3d.grids;

import com.enbecko.nbmodmaker.creator_3d.objects.IGridded;
import com.enbecko.nbmodmaker.creator_3d.objects.MCBlockProxy;
import com.google.common.collect.Maps;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by enbec on 16.08.2017.
 */
public class Grid implements IBlockAccess {
    private final HashMap<Integer, HashMap<Integer, HashMap<Integer, OverlyExtendedBlockStorage>>> chunks = new HashMap<Integer, HashMap<Integer, HashMap<Integer, OverlyExtendedBlockStorage>>>();
    private final Map<BlockPos, TileEntity> chunkTileEntityMap;
    private final int size;

    public Grid(int size) {
        if ((size & (size - 1)) != 0)
            throw new RuntimeException("Grid must have a size which is a multiple of 2. size: " + size);
        this.size = size;
        this.chunkTileEntityMap = Maps.<BlockPos, TileEntity>newHashMap();
    }

    @Nullable
    public IBlockState getAt(int x, int y, int z) {
        OverlyExtendedBlockStorage chunk = this.getChunkFromBlockPos(x, y, z);
        if (chunk != null) {
            return chunk.get(x & 15, y & 15, z & 15);
        }
        return null;
    }

    public void setAt(int x, int y, int z, IBlockState blockState) {
        OverlyExtendedBlockStorage chunk = this.getChunkFromBlockPos(x, y, z);
        if (chunk != null) {
            chunk.set(x & 15, y & 15, z & 15, blockState);
        } else {
            this.createChunkAtBlockPos(x, y, z).set(x & 15, y & 15, z & 14, blockState);
        }
    }

    @Nullable
    private OverlyExtendedBlockStorage getChunkFromBlockPos(int x, int y, int z) {
        return this.getChunkAt((int) Math.floor(x / 16), ((int) Math.floor(y / 16)), ((int) Math.floor(z / 16)));
    }

    @Nullable
    private OverlyExtendedBlockStorage getChunkAt(int x, int y, int z) {
        try {
            return this.chunks.get(x).get(y).get(z);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private OverlyExtendedBlockStorage createChunkAtBlockPos(int x, int y, int z) {
        return this.createChunkAt((int) Math.floor(x / 16), ((int) Math.floor(y / 16)), ((int) Math.floor(z / 16)));
    }

    private OverlyExtendedBlockStorage createChunkAt(int x, int y, int z) {
        HashMap<Integer, HashMap<Integer, OverlyExtendedBlockStorage>> tmpX;
        HashMap<Integer, OverlyExtendedBlockStorage> tmpY;
        if ((tmpX = this.chunks.get(x)) != null) {
            if ((tmpY = tmpX.get(y)) != null) {
                if (tmpY.get(z) != null) {
                    return tmpY.get(z);
                } else {
                    tmpY.put(z, new OverlyExtendedBlockStorage(x, y, z, false));
                    return tmpY.get(z);
                }
            } else {
                tmpX.put(y, (tmpY = new HashMap<Integer, OverlyExtendedBlockStorage>()));
                tmpY.put(z, new OverlyExtendedBlockStorage(x, y, z, false));
                return tmpY.get(z);
            }
        } else {
            this.chunks.put(x, (tmpX = new HashMap<Integer, HashMap<Integer, OverlyExtendedBlockStorage>>()));
            tmpX.put(y, (tmpY = new HashMap<Integer, OverlyExtendedBlockStorage>()));
            tmpY.put(z, new OverlyExtendedBlockStorage(x, y, z, false));
            return tmpY.get(z);
        }
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return null;
    }

    /**

    public void addTileEntity(TileEntity tileEntityIn) {
        this.addTileEntity(tileEntityIn.getPos(), tileEntityIn);
        this.world.addTileEntity(tileEntityIn);
    }

    public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
        if (tileEntityIn.getWorld() != this.world) //Forge don't call unless it's changed, could screw up bad mods.
            tileEntityIn.setWorld(this.world);
        tileEntityIn.setPos(pos);

        if (this.getBlockState(pos).getBlock().hasTileEntity(this.getBlockState(pos))) {
            if (this.chunkTileEntityMap.containsKey(pos)) {
                ((TileEntity) this.chunkTileEntityMap.get(pos)).invalidate();
            }

            tileEntityIn.validate();
            this.chunkTileEntityMap.put(pos, tileEntityIn);
            tileEntityIn.onLoad();
        }
    }

    public void removeTileEntity(BlockPos pos) {
        TileEntity tileentity = (TileEntity) this.chunkTileEntityMap.remove(pos);

        if (tileentity != null) {
            tileentity.invalidate();
        }
    }

    @Nullable
    private TileEntity createNewTileEntity(BlockPos pos) {
        IBlockState iblockstate = this.getBlockState(pos);
        Block block = iblockstate.getBlock();
        return !block.hasTileEntity(iblockstate) ? null : block.createTileEntity(this.world, iblockstate);
    }

    @Nullable
    public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType p_177424_2_) {
        TileEntity tileentity = (TileEntity) this.chunkTileEntityMap.get(pos);

        if (tileentity != null && tileentity.isInvalid()) {
            chunkTileEntityMap.remove(pos);
            tileentity = null;
        }

        if (tileentity == null) {
            if (p_177424_2_ == Chunk.EnumCreateEntityType.IMMEDIATE) {
                tileentity = this.createNewTileEntity(pos);
                this.world.setTileEntity(pos, tileentity);
            }
        }

        return tileentity;
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        IBlockState tmp;
        if ((tmp = this.getAt(pos.getX(), pos.getY(), pos.getZ())) instanceof MCBlockProxy) {
            if (((MCBlockProxy) tmp).hasTileEntity())
                return ((MCBlockProxy) tmp).getTileEntity();
        }
        return null;
    }
    */

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        return 0;
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        return this.getBlockState(pos.getX(), pos.getY(), pos.getZ());
    }

    public IBlockState getBlockState(int x, int y, int z) {
        return this.getAt(x, y, z);
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        return this.getBlockState(pos) == Blocks.AIR.getDefaultState();
    }

    @Override
    public Biome getBiome(BlockPos pos) {
        return null;
    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        return 0;
    }

    @Override
    public WorldType getWorldType() {
        return null;
    }

    @Override
    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
        return this.getBlockState(pos).isSideSolid(this, pos, side);
    }

    public float getUnitLength() {
        return (float) 1;//(1/Math.pow(this.lengthInOneThroughTheThisToPowerOfTwo, 2));
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GRID ->\n");
        for (Integer x : this.chunks.keySet()) {
            for (Integer y : this.chunks.get(x).keySet()) {
                for (Integer z : this.chunks.get(x).get(y).keySet()) {
                    builder.append("x = " + x + ", y = " + y + ", z = " + z + ", content = " + this.chunks.get(x).get(y).get(z)+"\n");
                }
            }
        }
        builder.append("<- GRID");
        return builder.toString();
    }

}
