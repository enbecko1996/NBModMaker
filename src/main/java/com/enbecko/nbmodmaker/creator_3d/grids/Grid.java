package com.enbecko.nbmodmaker.creator_3d.grids;

import com.enbecko.nbmodmaker.MathHelper;
import com.enbecko.nbmodmaker.creator_3d.grids.raytrace.*;
import com.enbecko.nbmodmaker.creator_3d.minecraft.Creator_Main;
import com.enbecko.nbmodmaker.linalg.real.Vec4;
import com.google.common.collect.Maps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.NibbleArray;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by enbec on 16.08.2017.
 */
public class Grid implements IBlockAccess {
    private final HashMap<Integer, HashMap<Integer, HashMap<Integer, OverlyExtendedBlockStorage>>> chunks = new HashMap<Integer, HashMap<Integer, HashMap<Integer, OverlyExtendedBlockStorage>>>();
    private final Map<BlockPos, TileEntity> chunkTileEntityMap;
    private final Map<Integer, HashMap<Integer, Set<Integer>>> dirtyPositions = new HashMap<>();
    private final int size;
    private final IBlockState AIR_BLOCK = Blocks.AIR.getDefaultState();
    private final GridRayTraceHandler rayTraceHandler;

    public Grid(int size) {
        if (!MathHelper.isPowerOf2(size))
            throw new RuntimeException("Grid must have a size which is a multiple of 2. size: " + size);
        this.size = size;
        this.chunkTileEntityMap = Maps.<BlockPos, TileEntity>newHashMap();
        this.rayTraceHandler = new GridRayTraceHandler(this);
    }

    public int getSize() {
        return this.size;
    }

    public GridRayTraceHandler getRayTraceHandler() {
        return rayTraceHandler;
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        return this.getBlockState(pos.getX(), pos.getY(), pos.getZ());
    }

    public IBlockState getBlockState(float x, float y, float z) {
        return this.getBlockState((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
    }

    public IBlockState getBlockState(int x, int y, int z) {
        OverlyExtendedBlockStorage chunk = this.getChunkFromBlockPos(x, y, z);
        if (chunk != null) {
            return chunk.get(x & 15, y & 15, z & 15);
        }
        return this.AIR_BLOCK;
    }

    public boolean canSetAt(int x, int y, int z) {
        IBlockState current = this.getBlockState(x, y, z);
        return current.getBlock().isReplaceable(this, new BlockPos(x, y, z));
    }

    public void setAt(int x, int y, int z, IBlockState blockState) {
        OverlyExtendedBlockStorage chunk = this.getChunkFromBlockPos(x, y, z);
        if (chunk != null) {
            chunk.set(x & 15, y & 15, z & 15, blockState);
            if (blockState != Blocks.AIR.getDefaultState()) {
                this.addDirtyPosition(x, y, z);
            } else {
                this.removeDirtyPosition(x, y, z);
            }
        } else if (blockState != Blocks.AIR.getDefaultState()){
            this.createChunkAtBlockPos(x, y, z).set(x & 15, y & 15, z & 15, blockState);
            this.addDirtyPosition(x, y, z);
        }
    }

    public HashMap<Integer, HashMap<Integer, HashMap<Integer, OverlyExtendedBlockStorage>>> getChunks() {
        return this.chunks;
    }

    public Map<Integer, HashMap<Integer, Set<Integer>>> getDirtyPositions() {
        return this.dirtyPositions;
    }

    private void addDirtyPosition(int x, int y, int z) {
        if (this.dirtyPositions.containsKey(x)) {
            if (this.dirtyPositions.get(x).containsKey(y)) {
                this.dirtyPositions.get(x).get(y).add(z);
            } else {
                HashSet <Integer> yPos;
                this.dirtyPositions.get(x).put(y, yPos = new HashSet<Integer>());
                yPos.add(z);
            }
        } else {
            HashSet <Integer> yPos;
            HashMap <Integer, Set<Integer>> xPos;
            this.dirtyPositions.put(x, xPos = new HashMap<>());
            xPos.put(y, yPos = new HashSet<Integer>());
            yPos.add(z);
        }
    }

    private void removeDirtyPosition(int x, int y, int z) {
        if (this.dirtyPositions.containsKey(x)) {
            if (this.dirtyPositions.get(x).containsKey(y)) {
                this.dirtyPositions.get(x).get(y).remove(z);
                if (this.dirtyPositions.get(x).get(y).size() == 0) {
                    this.dirtyPositions.get(x).remove(y);
                    if (this.dirtyPositions.get(x).size() == 0) {
                        this.dirtyPositions.remove(x);
                    }
                }
            }
        }
    }

    @Nullable
    private OverlyExtendedBlockStorage getChunkFromBlockPos(int x, int y, int z) {
        return this.getChunkAt((int) Math.floor(x / 16f), ((int) Math.floor(y / 16f)), ((int) Math.floor(z / 16f)));
    }

    @Nullable
    private OverlyExtendedBlockStorage getChunkAt(int x, int y, int z) {
        try {
            return this.chunks.get(x).get(y).get(z);
        } catch (Exception ignored) {
        }
        return null;
    }

    private OverlyExtendedBlockStorage createChunkAtBlockPos(int x, int y, int z) {
        return this.createChunkAt((int) Math.floor(x / 16f), ((int) Math.floor(y / 16f)), ((int) Math.floor(z / 16f)));
    }

    private OverlyExtendedBlockStorage createChunkAt(int x, int y, int z) {
        HashMap<Integer, HashMap<Integer, OverlyExtendedBlockStorage>> tmpX;
        HashMap<Integer, OverlyExtendedBlockStorage> tmpY;
        if ((tmpX = this.chunks.get(x)) != null) {
            if ((tmpY = tmpX.get(y)) != null) {
                if (tmpY.get(z) != null) {
                    return tmpY.get(z);
                } else {
                    OverlyExtendedBlockStorage tmp;
                    tmpY.put(z, (tmp = new OverlyExtendedBlockStorage(x, y, z, false)));
                    rayTraceHandler.addChunk(tmp);
                    return tmp;
                }
            } else {
                OverlyExtendedBlockStorage tmp;
                tmpX.put(y, (tmpY = new HashMap<Integer, OverlyExtendedBlockStorage>()));
                tmpY.put(z, (tmp = new OverlyExtendedBlockStorage(x, y, z, false)));
                rayTraceHandler.addChunk(tmp);
                return tmp;
            }
        } else {
            OverlyExtendedBlockStorage tmp;
            this.chunks.put(x, (tmpX = new HashMap<Integer, HashMap<Integer, OverlyExtendedBlockStorage>>()));
            tmpX.put(y, (tmpY = new HashMap<Integer, OverlyExtendedBlockStorage>()));
            tmpY.put(z, (tmp = new OverlyExtendedBlockStorage(x, y, z, false)));
            rayTraceHandler.addChunk(tmp);
            return tmp;
        }
    }

    public void toAir(int x, int y, int z) {
        this.setAt(x, y, z, this.AIR_BLOCK);
    }

    public void writeToNBT(NBTTagCompound compound, int boneID) {
        System.out.println("writeToNBT");
        ArrayList<Integer> coords = new ArrayList<>();
        for (Integer x : this.chunks.keySet()) {
            for (Integer y : this.chunks.get(x).keySet()) {
                for (Integer z : this.chunks.get(x).get(y).keySet()) {
                    coords.add(x);
                    coords.add(y);
                    coords.add(z);
                    byte[] abyte = new byte[4096];
                    NibbleArray nibblearray = new NibbleArray();
                    NibbleArray nibblearray1;
                    nibblearray1 = this.chunks.get(x).get(y).get(z).getData().getDataForNBT(abyte, nibblearray);
                    compound.setByteArray("Blocks_" + boneID + "_" + this.size + "_" + x + "_" + y + "_" + z, abyte);
                    compound.setByteArray("Data_" + boneID + "_" + this.size + "_" + x + "_" + y + "_" + z, nibblearray.getData());
                    if (nibblearray1 != null) {
                        compound.setByteArray("Add_" + boneID + "_" + this.size + "_" + x + "_" + y + "_" + z, nibblearray1.getData());
                    }
                }
            }
        }
        int[] c = MathHelper.toIntArray(coords);
        System.out.println("hiii" + Arrays.toString(c));
        compound.setIntArray("coords", c);
    }

    public void readFromNBT(NBTTagCompound compound, int boneID) {
        HashMap<Integer, HashMap<Integer, OverlyExtendedBlockStorage>> tmpX;
        HashMap<Integer, OverlyExtendedBlockStorage> tmpY;
        OverlyExtendedBlockStorage extendedBlockStorage;
        int[] coords = compound.getIntArray("coords");
        System.out.println(Arrays.toString(coords));
        for (int k = 0; k < coords.length; k += 3) {
            extendedBlockStorage = this.createChunkAt(coords[k], coords[k + 1], coords[k + 2]);
            rayTraceHandler.addChunk(extendedBlockStorage);
            byte[] abyte = compound.getByteArray("Blocks_" + boneID + "_" + this.size + "_" + coords[k] + "_" + coords[k + 1] + "_" + coords[k + 2]);
            NibbleArray nibblearray = new NibbleArray(compound.getByteArray("Data_" + boneID + "_" + this.size + "_" + coords[k] + "_" + coords[k + 1] + "_" + coords[k + 2]));
            NibbleArray nibblearray1 = compound.hasKey("Add_" + boneID + "_" + this.size + "_" + coords[k] + "_" + coords[k + 1] + "_" + coords[k + 2], 7) ? new NibbleArray(compound.getByteArray("Add_" + boneID + "_" + this.size + "_" + coords[k] + "_" + coords[k + 1] + "_" + coords[k + 2])) : null;
            extendedBlockStorage.getData().setDataFromNBT(abyte, nibblearray, nibblearray1);
        }
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return null;
    }

    /**
     * public void addTileEntity(TileEntity tileEntityIn) {
     * this.addTileEntity(tileEntityIn.getPos(), tileEntityIn);
     * this.world.addTileEntity(tileEntityIn);
     * }
     * <p>
     * public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
     * if (tileEntityIn.getWorld() != this.world) //Forge don't call unless it's changed, could screw up bad mods.
     * tileEntityIn.setWorld(this.world);
     * tileEntityIn.setPos(pos);
     * <p>
     * if (this.getBlockState(pos).getBlock().hasTileEntity(this.getBlockState(pos))) {
     * if (this.chunkTileEntityMap.containsKey(pos)) {
     * ((TileEntity) this.chunkTileEntityMap.get(pos)).invalidate();
     * }
     * <p>
     * tileEntityIn.validate();
     * this.chunkTileEntityMap.put(pos, tileEntityIn);
     * tileEntityIn.onLoad();
     * }
     * }
     * <p>
     * public void removeTileEntity(BlockPos pos) {
     * TileEntity tileentity = (TileEntity) this.chunkTileEntityMap.remove(pos);
     * <p>
     * if (tileentity != null) {
     * tileentity.invalidate();
     * }
     * }
     *
     * @Nullable private TileEntity createNewTileEntity(BlockPos pos) {
     * IBlockState iblockstate = this.getBlockState(pos);
     * Block block = iblockstate.getBlock();
     * return !block.hasTileEntity(iblockstate) ? null : block.createTileEntity(this.world, iblockstate);
     * }
     * @Nullable public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType p_177424_2_) {
     * TileEntity tileentity = (TileEntity) this.chunkTileEntityMap.get(pos);
     * <p>
     * if (tileentity != null && tileentity.isInvalid()) {
     * chunkTileEntityMap.remove(pos);
     * tileentity = null;
     * }
     * <p>
     * if (tileentity == null) {
     * if (p_177424_2_ == Chunk.EnumCreateEntityType.IMMEDIATE) {
     * tileentity = this.createNewTileEntity(pos);
     * this.world.setTileEntity(pos, tileentity);
     * }
     * }
     * <p>
     * return tileentity;
     * }
     * @Nullable
     * @Override public TileEntity getTileEntity(BlockPos pos) {
     * IBlockState tmp;
     * if ((tmp = this.getAtBoneCoords(pos.getxInBone(), pos.getyInBone(), pos.getzInBone())) instanceof MCBlockProxy) {
     * if (((MCBlockProxy) tmp).hasTileEntity())
     * return ((MCBlockProxy) tmp).getTileEntity();
     * }
     * return null;
     * }
     */

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        return 0;
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
        builder.append("Size: " + this.size + "\n");
        for (Integer x : this.chunks.keySet()) {
            for (Integer y : this.chunks.get(x).keySet()) {
                for (Integer z : this.chunks.get(x).get(y).keySet()) {
                    builder.append("gridX = " + x + ", gridY = " + y + ", gridZ = " + z + ", content = " + this.chunks.get(x).get(y).get(z) + "\n");
                }
            }
        }
        builder.append("<- GRID");
        return builder.toString();
    }

}
