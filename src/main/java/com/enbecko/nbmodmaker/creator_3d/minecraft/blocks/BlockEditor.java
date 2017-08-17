package com.enbecko.nbmodmaker.creator_3d.minecraft.blocks;

import com.enbecko.nbmodmaker.creator_3d.minecraft.tileentities.TE_Editor;
import com.sun.istack.internal.NotNull;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by enbec on 17.08.2017.
 */
public class BlockEditor extends BlockContainer{
    public BlockEditor(Material materialIn) {
        super(materialIn);
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
}
