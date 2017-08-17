package com.enbecko.nbmodmaker.creator_3d.objects;

import com.enbecko.nbmodmaker.creator_3d.grids.Bone;
import com.enbecko.nbmodmaker.creator_3d.grids.Grid;
import com.enbecko.nbmodmaker.linalg.real.Vec4;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * Created by enbec on 16.08.2017.
 */
public class MCBlockProxy implements IGridded {
    private IBlockState blockState;
    TileEntity tileEntity;

    public MCBlockProxy(Bone parentBone, Grid parentGrid, Vec4 posInGrid, IBlockState state) {
    }

    @SideOnly(Side.CLIENT)
    public void render(VertexBuffer buffer) {

    }

    public IBlockState getBlockState() {
        return this.blockState;
    }

    public boolean hasTileEntity() {
        return this.blockState.getBlock().hasTileEntity();
    }

    public TileEntity getTileEntity() {
        return this.tileEntity;
    }

    public String toString() {
        return "MCBlockProxy: ";// + this.getGeometryInfo();
    }

}
