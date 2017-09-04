package com.enbecko.nbmodmaker.creator_3d.minecraft.renderer;

import com.enbecko.nbmodmaker.OpenGLHelperEnbecko;
import com.enbecko.nbmodmaker.creator_3d.grids.Bone;
import com.enbecko.nbmodmaker.creator_3d.grids.Grid;
import com.enbecko.nbmodmaker.creator_3d.grids.raytrace.Content;
import com.enbecko.nbmodmaker.creator_3d.grids.raytrace.RenderCuboidOutline;
import com.enbecko.nbmodmaker.creator_3d.minecraft.Creator_Main;
import com.enbecko.nbmodmaker.creator_3d.minecraft.tileentities.TE_Editor;
import com.enbecko.nbmodmaker.linalg.real.Vec4;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by enbec on 17.08.2017.
 */
public class Render_EditorBlock extends TileEntitySpecialRenderer <TE_Editor> {
    RenderCuboidOutline outlineRenderer;
    ResourceLocation blank = new ResourceLocation(Creator_Main.MODID + ":textures/blank.png");

    public Render_EditorBlock() {
        this.outlineRenderer = new RenderCuboidOutline();
    }

    public void render(TE_Editor te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        this.setLightmapDisabled(true);
        Minecraft.getMinecraft().getTextureManager().bindTexture(blank);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        Set<Integer> bones = te.getBoneIDs();
        Iterator<Integer> bIter = bones.iterator();
        while (bIter.hasNext()) {
            Bone b = te.getBoneByID(bIter.next());
            Set<Integer> grids = b.getGridSizes();
            Iterator<Integer> iter = grids.iterator();
            while (iter.hasNext()) {
                Grid g = b.getGridBySize(iter.next());
                this.outlineRenderer.render(g.getRayTraceHandler(), Tessellator.getInstance().getBuffer());
                //System.out.println(Arrays.toString(g.getRayTraceHandler().getContent().toArray(new Content[g.getRayTraceHandler().getContentCount()])));
            }
        }
        GlStateManager.popMatrix();
    }
}
