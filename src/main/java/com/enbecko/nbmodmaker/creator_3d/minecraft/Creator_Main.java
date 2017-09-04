package com.enbecko.nbmodmaker.creator_3d.minecraft;

import com.enbecko.nbmodmaker.GlobalSettings;
import com.enbecko.nbmodmaker.creator_3d.minecraft.blocks.BlockEditor;
import com.enbecko.nbmodmaker.creator_3d.minecraft.tileentities.TE_Editor;
import com.google.common.base.Preconditions;
import com.sun.istack.internal.NotNull;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

@Mod(modid = Creator_Main.MODID, name = Creator_Main.MODNAME, version = Creator_Main.MODVERSION, useMetadata = true)
public class Creator_Main
{
    public static final String MODID = "creator3d";
    public static final String MODNAME = "3D_Creator";
    public static final String MODVERSION = "0.0.1";

    public static final int contentCubesPerCube = 4;

    public static Logger log;

    @Mod.Instance
    public static Creator_Main instance;

    @SidedProxy(clientSide="com.enbecko.nbmodmaker.creator_3d.minecraft.ClientProxy", serverSide="com.enbecko.nbmodmaker.creator_3d.minecraft.ServerProxy")
    public static CommonProxy proxy;

    private static final CreativeTabs modMakerTab = new CreativeTabs("Enbecko's Mod Maker") {
        @NotNull
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(Item.getItemFromBlock(EDITOR_BLOCK));
        }
    };

    public static BlockEditor EDITOR_BLOCK = (BlockEditor) new BlockEditor(Material.ANVIL).setUnlocalizedName("editorblock").setCreativeTab(modMakerTab);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        log = event.getModLog();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);

        GlobalSettings.setDebugMode();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }

    @Mod.EventBusSubscriber(modid = Creator_Main.MODID)
    public static class RegistrationHandler {
        public static final Set<ItemBlock> ITEM_BLOCKS = new HashSet<>();

        /**
         * Register this mod's {@link Block}s.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void registerBlocks(final RegistryEvent.Register<Block> event) {
            final IForgeRegistry<Block> registry = event.getRegistry();

            final Block[] blocks = {
                    EDITOR_BLOCK,
            };

            registry.registerAll(blocks);
        }

        @SubscribeEvent
        public static void registerItemBlocks(final RegistryEvent.Register<Item> event) {
            final ItemBlock[] items = {
                    new ItemBlock(EDITOR_BLOCK)
            };

            final IForgeRegistry<Item> registry = event.getRegistry();

            for (final ItemBlock item : items) {
                final Block block = item.getBlock();
                final ResourceLocation registryName = Preconditions.checkNotNull(block.getRegistryName(), "Block %s has null registry name", block);
                registry.register(item.setRegistryName(registryName));
                ITEM_BLOCKS.add(item);
            }

            registerTileEntities();
        }
    }

    private static void registerTileEntities() {
        GameRegistry.registerTileEntity(TE_Editor.class, "te_editorblock");
    }
}
