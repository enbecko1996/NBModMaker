package com.enbecko.nbmodmaker.creator_3d.minecraft.network;

import com.enbecko.nbmodmaker.creator_3d.minecraft.tileentities.TE_Editor;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.registries.GameData;

public class M_AddBlockToBone extends M_EditorMessage {
    public int xInGrid, yInGrid, zInGrid, gridSize, blockState, boneID;

    public M_AddBlockToBone(BlockPos tePosInWorld, int boneID, int gridSize, int xInGrid, int yInGrid, int zInGrid, IBlockState blockState) {
        super(tePosInWorld);
        this.xInGrid = xInGrid;
        this.yInGrid = yInGrid;
        this.zInGrid = zInGrid;
        this.boneID = boneID;
        this.gridSize = gridSize;
        this.blockState = GameData.getBlockStateIDMap().get(blockState);
    }

    // for use by the message handler only.
    public M_AddBlockToBone() {
        super();
    }

    public IBlockState getBlockState() {
        return GameData.getBlockStateIDMap().getByValue(this.blockState);
    }

    public M_AddBlockToBone setBlockState(IBlockState state) {
        this.blockState = GameData.getBlockStateIDMap().get(state);
        return this;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (!messageIsValid)
            return;
        super.toBytes(buf);
        buf.writeInt(this.boneID);
        buf.writeInt(this.gridSize);
        buf.writeInt(this.xInGrid);
        buf.writeInt(this.yInGrid);
        buf.writeInt(this.zInGrid);
        buf.writeInt(this.blockState);

        // these methods may also be of use for your code:
        // for Itemstacks - ByteBufUtils.writeItemStack()
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            super.fromBytes(buf);
            this.boneID = buf.readInt();
            this.gridSize = buf.readInt();
            this.xInGrid = buf.readInt();
            this.yInGrid = buf.readInt();
            this.zInGrid = buf.readInt();
            this.blockState = buf.readInt();

            // these methods may also be of use for your code:
            // for Itemstacks - ByteBufUtils.readItemStack()
            // for NBT tags ByteBufUtils.readTag();
            // for Strings: ByteBufUtils.readUTF8String();

        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading M_AddBlockToBone: " + ioe);
            return;
        }
        messageIsValid = true;
    }

    @Override
    public String toString() {
        return "M_AddBlockToBone: {tePos = " + this.tePosInWorld + ", boneID = " + this.boneID + ", gridSize = " + this.gridSize + " " + "xInGrid = " + xInGrid + ", yInGrid = " + yInGrid + ", zInGrid = " + zInGrid + " " + this.getBlockState() + "}";
    }

    public static class Handler implements IMessageHandler<M_AddBlockToBone, M_AddBlockToBone> {
        public Handler() {

        }

        @Override
        public M_AddBlockToBone onMessage(M_AddBlockToBone message, MessageContext ctx) {
            System.out.println("Message received (" + ctx.side + "): " + message);
            switch (ctx.side) {
                case SERVER:
                    EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
                    TileEntity te = serverPlayer.getServerWorld().getTileEntity(message.tePosInWorld);
                    TE_Editor editor;
                    if (!(te instanceof TE_Editor))
                        return message.setBlockState(Blocks.AIR.getDefaultState());
                    else {
                        editor = (TE_Editor) te;
                        System.out.println("at least the TE is here " + editor.canSetAtCoordsInGrid(message.boneID, message.gridSize, message.xInGrid, message.yInGrid, message.zInGrid));
                        if (!editor.canSetAtCoordsInGrid(message.boneID, message.gridSize, message.xInGrid, message.yInGrid, message.zInGrid))
                            return message.setBlockState(editor.getAtCoordsInGrid(message.boneID, message.gridSize, message.xInGrid, message.yInGrid, message.zInGrid));
                    }
                    serverPlayer.getServerWorld().addScheduledTask(() -> {
                        System.out.println("Setting in Grid (Server)");
                        editor.setAtCoordsInGrid(message.boneID, message.gridSize, message.xInGrid, message.yInGrid, message.zInGrid, message.getBlockState());
                        editor.markDirty();
                    });
                    return message;
                case CLIENT:
                    TileEntity teClient = Minecraft.getMinecraft().world.getTileEntity(message.tePosInWorld);
                    TE_Editor editorClient;
                    if (!(teClient instanceof TE_Editor))
                        return message.setBlockState(Blocks.AIR.getDefaultState());
                    else {
                        editorClient = (TE_Editor) teClient;
                        editorClient.setAtCoordsInGrid(message.boneID, message.gridSize, message.xInGrid, message.yInGrid, message.zInGrid, message.getBlockState());
                    }
                    return null;
            }
            return null;
        }
    }
}
