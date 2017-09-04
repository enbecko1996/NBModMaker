package com.enbecko.nbmodmaker.creator_3d.minecraft;

import com.enbecko.nbmodmaker.creator_3d.minecraft.gui.GuiProxy;
import com.enbecko.nbmodmaker.creator_3d.minecraft.renderer.Render_EditorBlock;
import com.enbecko.nbmodmaker.creator_3d.minecraft.tileentities.TE_Editor;
import net.minecraft.block.Block;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import static com.enbecko.nbmodmaker.creator_3d.minecraft.Creator_Main.instance;

/**
 * Created by enbec on 03.03.2017.
 */
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        this.bindRenderers();
        this.registerKeyBindings();
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }

    public void registerKeyBindings() {
    }

    private void bindRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TE_Editor.class, new Render_EditorBlock());
    }

    private void bindItemModel(Block block) {
    }
}
