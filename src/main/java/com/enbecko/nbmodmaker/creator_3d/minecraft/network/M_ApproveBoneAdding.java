package com.enbecko.nbmodmaker.creator_3d.minecraft.network;

import com.enbecko.nbmodmaker.creator_3d.minecraft.tileentities.TE_Editor;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class M_ApproveBoneAdding extends M_EditorMessage {
    public int tempBoneID;
    public boolean approve;

    public M_ApproveBoneAdding() {
        super();
    }

    public M_ApproveBoneAdding(BlockPos tePosInWorld, int tempBoneID, boolean approve) {
        super(tePosInWorld);
        this.tempBoneID = tempBoneID;
        this.approve = approve;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            super.fromBytes(buf);
            this.tempBoneID = buf.readInt();
            this.approve = buf.readBoolean();
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading M_ApproveBoneAdding: " + ioe);
            return;
        }
        messageIsValid = true;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(this.tempBoneID);
        buf.writeBoolean(this.approve);
    }

    @Override
    public String toString() {
        return "M_ApproveBoneAdding: {tePos = " + this.tePosInWorld + ", tempBoneID = " + this.tempBoneID + ", approve = " + this.approve + "}";
    }

    public static class Handler implements IMessageHandler<M_ApproveBoneAdding, IMessage> {
        @Override
        public IMessage onMessage(M_ApproveBoneAdding message, MessageContext ctx) {
            System.out.println("Message received (" + ctx.side + "): " + message);
            TileEntity te = Minecraft.getMinecraft().world.getTileEntity(message.tePosInWorld);
            TE_Editor editor;
            if (te instanceof TE_Editor) {
                editor = (TE_Editor)te;
                if (!message.approve) {
                    editor.removeBoneByID(message.tempBoneID);
                }
            }
            return null;
        }
    }
}
