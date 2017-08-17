package com.enbecko.nbmodmaker.creator_3d.minecraft;

import com.enbecko.nbmodmaker.creator_3d.minecraft.renderer.Render_EditorBlock;
import com.enbecko.nbmodmaker.creator_3d.minecraft.tileentities.TE_Editor;
import net.minecraft.block.Block;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by enbec on 03.03.2017.
 */
public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent e) {

    }

    @Override
    public void init(FMLInitializationEvent e) {
        this.bindRenderers();
        this.registerKeyBindings();
    }

    public void registerKeyBindings() {
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
    }

    private void bindRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TE_Editor.class, new Render_EditorBlock());
    }

    private void bindItemModel(Block block) {
    }
}
