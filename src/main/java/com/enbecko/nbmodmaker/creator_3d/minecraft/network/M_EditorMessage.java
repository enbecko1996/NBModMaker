package com.enbecko.nbmodmaker.creator_3d.minecraft.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class M_EditorMessage implements IMessage {
    public BlockPos tePosInWorld;
    boolean messageIsValid;

    public M_EditorMessage() {
        this.messageIsValid = false;
    }

    public M_EditorMessage(BlockPos tePos) {
        this.tePosInWorld = tePos;
        this.messageIsValid = true;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (!messageIsValid)
            return;
        buf.writeInt(this.tePosInWorld.getX());
        buf.writeInt(this.tePosInWorld.getY());
        buf.writeInt(this.tePosInWorld.getZ());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            int x = buf.readInt();
            int y = buf.readInt();
            int z = buf.readInt();
            this.tePosInWorld = new BlockPos(x, y, z);
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading M_AddBlockToBone: " + ioe);
            return;
        }
        messageIsValid = true;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }
}
