package com.enbecko.nbmodmaker.creator_3d.minecraft.renderer;

import com.enbecko.nbmodmaker.creator_3d.grids.Bone;
import com.enbecko.nbmodmaker.creator_3d.grids.Grid;
import com.enbecko.nbmodmaker.creator_3d.minecraft.Creator_Main;
import com.enbecko.nbmodmaker.creator_3d.minecraft.tileentities.TE_Editor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by enbec on 17.08.2017.
 */
public class Render_EditorBlock extends TileEntitySpecialRenderer <TE_Editor> {
    RenderBone boneRenderer;
    ResourceLocation blank = new ResourceLocation(Creator_Main.MODID + ":textures/blank.png");

    public Render_EditorBlock() {
        this.boneRenderer = new RenderBone();
    }

    public void render(TE_Editor te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        this.setLightmapDisabled(true);
        Minecraft.getMinecraft().getTextureManager().bindTexture(blank);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        Set<Integer> bones = te.getBoneIDs();
        for (Integer bone : bones) {
            Bone b = te.getBoneByID(bone);
            this.boneRenderer.render(b, Tessellator.getInstance().getBuffer());
        }
        GlStateManager.popMatrix();
    }
}
