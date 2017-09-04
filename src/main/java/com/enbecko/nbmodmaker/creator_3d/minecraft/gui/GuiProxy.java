package com.enbecko.nbmodmaker.creator_3d.minecraft.gui;

import com.enbecko.nbmodmaker.creator_3d.minecraft.tileentities.TE_Editor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiProxy implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        System.out.println("open gui server");
        if (te instanceof TE_Editor) {
            return new TestContainer(player.inventory, (TE_Editor) te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        System.out.println("open gui client");
        if (te instanceof TE_Editor) {
            TE_Editor containerTileEntity = (TE_Editor) te;
            return new TestContainerGui(containerTileEntity, new TestContainer(player.inventory, containerTileEntity));
        }
        return null;
    }
}