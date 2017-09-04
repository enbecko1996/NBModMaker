package com.enbecko.nbmodmaker.creator_3d.minecraft.tileentities;

import com.enbecko.nbmodmaker.MathHelper;
import com.enbecko.nbmodmaker.creator_3d.grids.Bone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by enbec on 17.08.2017.
 */
public class TE_Editor extends TileEntity implements ITickable {
    private final List<Bone> bones = new ArrayList<Bone>();
    private final HashMap<Integer, Bone> bonesIDMap = new HashMap<>();
    private int counter;
    private final int MAX_BONE_IDS = 64;
    public static final int SIZE = 9;

    // This item handler will hold our nine inventory slots
    private ItemStackHandler itemStackHandler = new ItemStackHandler(SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            // We need to tell the tile entity that something has changed so
            // that the chest contents is persisted
            TE_Editor.this.markDirty();
        }
    };

    public TE_Editor() {

    }

    public boolean canSetAtCoordsInGrid(int boneID, int gridSize, int x, int y, int z) {
        Bone b = this.getBoneByID(boneID);
        return b != null && b.canSetAtCoordsInGrid(gridSize, x, y, z);
    }

    public IBlockState getAtCoordsInGrid(int boneID, int gridSize, int x, int y, int z) {
        Bone b = this.getBoneByID(boneID);
        return b != null ? b.getAtGridCoords(x, y, z, gridSize) : Blocks.AIR.getDefaultState();
    }

    public void setAtCoordsInGrid(int boneID, int gridSize, int x, int y, int z, IBlockState blockState) {
        Bone b = this.getBoneByID(boneID);
        if (b != null)
            b.setAtCoordsInGrid(x, y, z, gridSize, blockState);
    }

    public Bone addBone(float x, float y, float z, String boneName, int forceID) {
        return this.addBone(x, y, z, new Bone(boneName), forceID);
    }

    public Bone addBone(float x, float y, float z, String boneName) {
        return this.addBone(x, y, z, new Bone(boneName), -1);
    }

    private Bone addBone(float x, float y, float z, Bone bone, int id) {
        this.bones.add(bone);
        return this.registerBone(this.bones.get(this.bones.size() - 1), id < 0 ? this.getFreeBoneID() : id);
    }

    public Set<Integer> getBoneIDs() {
        return this.bonesIDMap.keySet();
    }

    private int getFreeBoneID() {
        for (int k = 0; k < this.MAX_BONE_IDS; k++) {
            if (!this.bonesIDMap.containsKey(k)) {
                return k;
            }
        }
        return -1;
    }

    private Bone registerBone(Bone bone, int id) {
        if (this.bones.contains(bone)) {
            if (!this.bonesIDMap.containsKey(id)) {
                this.bonesIDMap.put(id, bone);
                bone.setBoneID(id);
            } else
                throw new RuntimeException("1Bone with ID " + id + " is already registered in " + this);
        } else {
            this.bones.add(bone);
            if (!this.bonesIDMap.containsKey(id)) {
                this.bonesIDMap.put(id, bone);
                bone.setBoneID(id);
            } else
                throw new RuntimeException("2Bone with ID " + id + " is already registered in " + this);
        }
        return bone;
    }

    public boolean removeBoneByID(int id) {
        return this.bonesIDMap.containsKey(id) && this.removeBone(this.getBoneByID(id));
    }

    private boolean removeBone(Bone bone) {
        if (bone != null) {
            if (this.bones.contains(bone)) {
                if (this.bonesIDMap.containsKey(bone.getBoneID())) {
                    this.bonesIDMap.remove(bone.getBoneID(), bone);
                }
                this.bones.remove(bone);
                return true;
            }
        }
        return false;
    }

    @Nullable
    public Bone getBoneByID(int id) {
        if (this.bonesIDMap.containsKey(id))
            return this.bonesIDMap.get(id);
        return null;
    }

    @Override
    public void update() {
        if (counter <= 100)
            counter++;
        else {
            counter = 0;
            System.out.println("update " + this.world + " " + this.bones.size());
            for (Bone bone : this.bones)
                System.out.println(bone);
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemStackHandler);
        }
        return super.getCapability(capability, facing);
    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        // If we are too far away from this tile entity you cannot use it
        return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }

    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        System.out.println("readNBT_TE_Editor");
        int[] boneIDs = compound.getIntArray("boneIDs");
        for (int id : boneIDs) {
            Bone b;
            String name = compound.getString("bone_name_" + id);
            this.bones.add(b = new Bone(name));
            this.registerBone(b, id);
            b.readFromNBT(compound);
        }
        if (compound.hasKey("items")) {
            itemStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("items"));
        }
    }


    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        List<Integer> boneIDs = new ArrayList<>();
        for (Bone bone : this.bones) {
            boneIDs.add(bone.getBoneID());
            compound.setString("bone_name_" + bone.getBoneID(), bone.getBoneName());
            bone.writeToNBT(compound);
        }
        int[] b = MathHelper.toIntArray(boneIDs);
        compound.setIntArray("boneIDs", b);
        compound.setTag("items", itemStackHandler.serializeNBT());
        return compound;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        System.out.println("getDescriptionPacket");
        NBTTagCompound tagCompound = new NBTTagCompound();
        writeToNBT(tagCompound);
        return new SPacketUpdateTileEntity(this.getPos(), 1, tagCompound);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        System.out.println("onDataPacket");
        NBTTagCompound tag = pkt.getNbtCompound();
        readFromNBT(tag);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        System.out.println("getUpdateTag");
        return this.writeToNBT(new NBTTagCompound());
    }
}
