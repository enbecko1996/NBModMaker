package com.enbecko.nbmodmaker.creator_3d.minecraft;

import com.enbecko.nbmodmaker.creator_3d.minecraft.blocks.BlockEditor;
import com.enbecko.nbmodmaker.creator_3d.minecraft.tileentities.TE_Editor;
import com.sun.istack.internal.NotNull;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

import java.util.Set;

@Mod(modid = Creator_Main.MODID, version = Creator_Main.VERSION)
public class Creator_Main
{
    public static final String MODID = "3dcreator";
    public static final String VERSION = "1.0";

    @SidedProxy(clientSide="com.enbecko.nbmodmaker.creator_3d.minecraft.ClientProxy", serverSide="com.enbecko.nbmodmaker.creator_3d.minecraft.CommonProxy")
    public static CommonProxy proxy;

    public static BlockEditor EDITOR_BLOCK;

    private static final CreativeTabs modMakerTab = new CreativeTabs("Enbecko's Mod Maker") {
        @NotNull
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Item.getItemFromBlock(EDITOR_BLOCK));
        }
    };


    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);

        EDITOR_BLOCK = (BlockEditor) new BlockEditor(Material.ANVIL).setUnlocalizedName("editorblock").setCreativeTab(modMakerTab);
    }

    @Mod.EventBusSubscriber(modid = Creator_Main.MODID)
    public static class RegistrationHandler {
        /**
         * Register this mod's {@link Block}s.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void registerBlocks(final RegistryEvent.Register<Block> event) {
            final IForgeRegistry<Block> registry = event.getRegistry();

            final Block[] blocks = {
                    EDITOR_BLOCK
            };

            registry.registerAll(blocks);

            registerTileEntities();
        }
    }

    private static void registerTileEntities() {
        GameRegistry.registerTileEntity(TE_Editor.class, "te_editorblock");
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }
}
