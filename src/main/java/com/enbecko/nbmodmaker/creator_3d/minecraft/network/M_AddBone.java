package com.enbecko.nbmodmaker.creator_3d.minecraft.network;

import com.enbecko.nbmodmaker.creator_3d.minecraft.tileentities.TE_Editor;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class M_AddBone extends M_EditorMessage {
    public float xInBone, yInBone, zInBone;
    public int tempBoneID;
    public String boneName;

    public M_AddBone() {
        super();
    }

    public M_AddBone(BlockPos tePosInWorld, int tempBoneID, float xInBone, float yInBone, float zInBone, String boneName) {
        super(tePosInWorld);
        this.tempBoneID = tempBoneID;
        this.xInBone = xInBone;
        this.yInBone = yInBone;
        this.zInBone = zInBone;
        this.boneName = boneName;
        this.messageIsValid = true;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            super.fromBytes(buf);
            this.tempBoneID = buf.readInt();
            this.xInBone = buf.readFloat();
            this.yInBone = buf.readFloat();
            this.zInBone = buf.readFloat();
            this.boneName = ByteBufUtils.readUTF8String(buf);
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading M_AddBone: " + ioe);
            return;
        }
        messageIsValid = true;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(this.tempBoneID);
        buf.writeFloat(this.xInBone);
        buf.writeFloat(this.yInBone);
        buf.writeFloat(this.zInBone);
        ByteBufUtils.writeUTF8String(buf, this.boneName);
    }

    @Override
    public String toString() {
        return "M_AddBone: {tePos = " + this.tePosInWorld + ", xInBone = " + xInBone + ", yInBone = " + yInBone + " " + "zInBone = " + zInBone + ", boneName = " + boneName + "}";
    }

    public static class Handler implements IMessageHandler<M_AddBone, M_ApproveBoneAdding> {

        @Override
        public M_ApproveBoneAdding onMessage(M_AddBone message, MessageContext ctx) {
            System.out.println("Message received (" + ctx.side + "): " + message);
            EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
            TileEntity te = serverPlayer.getServerWorld().getTileEntity(message.tePosInWorld);
            TE_Editor editor;
            if (!(te instanceof TE_Editor)) {
                System.out.println("sending disapprove");
                return new M_ApproveBoneAdding(message.tePosInWorld, message.tempBoneID, false);
            } else {
                editor = (TE_Editor)te;
                serverPlayer.getServerWorld().addScheduledTask(() -> {
                    editor.addBone(message.xInBone, message.yInBone, message.zInBone, message.boneName, message.tempBoneID);
                });
                return new M_ApproveBoneAdding(message.tePosInWorld, message.tempBoneID, true);
            }
        }
    }
}
