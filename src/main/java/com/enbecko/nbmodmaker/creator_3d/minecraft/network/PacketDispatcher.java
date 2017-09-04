package com.enbecko.nbmodmaker.creator_3d.minecraft.network;

import com.enbecko.nbmodmaker.creator_3d.minecraft.Creator_Main;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 *
 * This class will house the SimpleNetworkWrapper instance, which I will name 'dispatcher',
 * as well as give us a logical place from which to register our packets. These two things
 * could be done anywhere, however, even in your Main class, but I will be adding other
 * functionality (see below) that gives this class a bit more utility.
 *
 * While unnecessary, I'm going to turn this class into a 'wrapper' for SimpleNetworkWrapper
 * so that instead of writing "PacketDispatcher.dispatcher.{method}" I can simply write
 * "PacketDispatcher.{method}" All this does is make it quicker to type and slightly shorter;
 * if you do not care about that, then make the 'dispatcher' field public instead of private,
 * or, if you do not want to add a new class just for one field and one static method that
 * you could put anywhere, feel free to put them wherever.
 *
 * For further convenience, I have also added two extra sendToAllAround methods: one which
 * takes an EntityPlayer and one which takes coordinates.
 *
 */
public class PacketDispatcher
{
    private static byte packetId = 0;

    private static final SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(Creator_Main.MODID);

    /**
     * Call this during pre-init or loading and register all of your packets (messages) here
     */
    public static final void registerPackets() {
        PacketDispatcher.registerMessage(M_AddBlockToBone.Handler.class, M_AddBlockToBone.class, Side.SERVER);
        PacketDispatcher.registerMessage(M_AddBlockToBone.Handler.class, M_AddBlockToBone.class, Side.CLIENT);
        PacketDispatcher.registerMessage(M_AddBone.Handler.class, M_AddBone.class, Side.SERVER);
        PacketDispatcher.registerMessage(M_ApproveBoneAdding.Handler.class, M_ApproveBoneAdding.class, Side.CLIENT);
    }

    /**
     * Registers a message and message handler
     */
    private static final void registerMessage(Class handlerClass, Class messageClass, Side side) {
        PacketDispatcher.dispatcher.registerMessage(handlerClass, messageClass, packetId++, side);
    }

    //========================================================//
    // The following methods are the 'wrapper' methods; again,
    // this just makes sending a message slightly more compact
    // and is purely a matter of stylistic preference
    //========================================================//

    /**
     * Send this message to the specified player.
     * See {@link SimpleNetworkWrapper#sendTo(IMessage, EntityPlayerMP)}
     */
    public static final void sendTo(IMessage message, EntityPlayerMP player) {
        PacketDispatcher.dispatcher.sendTo(message, player);
    }

    /**
     * Send this message to everyone within a certain range of a point.
     * See
     */
    public static final void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
        PacketDispatcher.dispatcher.sendToAllAround(message, point);
    }

    /**
     * Sends a message to everyone within a certain range of the coordinates in the same dimension.
     */
    public static final void sendToAllAround(IMessage message, int dimension, double x, double y, double z, double range) {
        PacketDispatcher.sendToAllAround(message, new NetworkRegistry.TargetPoint(dimension, x, y, z, range));
    }

    /**
     * Sends a message to everyone within a certain range of the player provided.
     */
    public static final void sendToAllAround(IMessage message, EntityPlayer player, double range) {
        PacketDispatcher.sendToAllAround(message, player.world.provider.getDimension(), player.posX, player.posY, player.posZ, range);
    }

    /**
     * Send this message to everyone within the supplied dimension.
     * See {@link SimpleNetworkWrapper#sendToDimension(IMessage, int)}
     */
    public static final void sendToDimension(IMessage message, int dimensionId) {
        PacketDispatcher.dispatcher.sendToDimension(message, dimensionId);
    }

    /**
     * Send this message to the server.
     * See {@link SimpleNetworkWrapper#sendToServer(IMessage)}
     */
    public static final void sendToServer(IMessage message) {
        PacketDispatcher.dispatcher.sendToServer(message);
    }
}
