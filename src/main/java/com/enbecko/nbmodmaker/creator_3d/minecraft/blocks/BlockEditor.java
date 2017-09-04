package com.enbecko.nbmodmaker.creator_3d.minecraft.blocks;

import com.enbecko.nbmodmaker.Log;
import com.enbecko.nbmodmaker.Log.LogEnums;
import com.enbecko.nbmodmaker.creator_3d.grids.Bone;
import com.enbecko.nbmodmaker.creator_3d.minecraft.Creator_Main;
import com.enbecko.nbmodmaker.creator_3d.minecraft.tileentities.TE_Editor;
import com.sun.istack.internal.NotNull;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by enbec on 17.08.2017.
 */
public class BlockEditor extends BlockContainer{
    public BlockEditor(Material materialIn) {
        super(materialIn);
        this.setRegistryName("editor_block");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TE_Editor();
    }

    @NotNull
    @Override
    public EnumBlockRenderType getRenderType(IBlockState p_getRenderType_1_) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for renderBlockState
     */
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        // Only execute on the server
        System.out.println("open gui1");
        if (worldIn.isRemote) {
            return true;
        }

        System.out.println("open gui2");
        TileEntity te = worldIn.getTileEntity(pos);
        if (!(te instanceof TE_Editor)) {
            return false;
        }
        System.out.println("open gui3");
        playerIn.openGui(Creator_Main.instance, 1, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
}
