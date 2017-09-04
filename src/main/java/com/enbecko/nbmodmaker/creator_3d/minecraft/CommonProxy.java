package com.enbecko.nbmodmaker.creator_3d.minecraft;

import com.enbecko.nbmodmaker.creator_3d.minecraft.gui.GuiProxy;
import com.enbecko.nbmodmaker.creator_3d.minecraft.network.PacketDispatcher;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import static com.enbecko.nbmodmaker.creator_3d.minecraft.Creator_Main.instance;

/**
 * Created by enbec on 03.03.2017.
 */
@Mod.EventBusSubscriber
public class CommonProxy {
    public void preInit(FMLPreInitializationEvent e) {
        PacketDispatcher.registerPackets();
    }

    public void init(FMLInitializationEvent e) {
        System.out.println("init gui");
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiProxy());
    }

    public void postInit(FMLPostInitializationEvent e) {

    }
}
