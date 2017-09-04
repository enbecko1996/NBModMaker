package com.enbecko.nbmodmaker.creator_3d.minecraft.renderer;

import com.enbecko.nbmodmaker.GlobalSettings;
import com.enbecko.nbmodmaker.INBRenderer;
import com.enbecko.nbmodmaker.LocalRenderSetting;
import com.enbecko.nbmodmaker.creator_3d.grids.Bone;
import com.enbecko.nbmodmaker.creator_3d.grids.Grid;
import com.enbecko.nbmodmaker.creator_3d.grids.OverlyExtendedBlockStorage;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.init.Blocks;
import org.lwjgl.opengl.GL11;

import java.nio.Buffer;
import java.util.*;

public class RenderBone implements INBRenderer<Bone> {
    RenderCuboidOutline outlineRenderer;

    public RenderBone() {
        this.outlineRenderer = new RenderCuboidOutline();
        BlockStateRenderer.registerBlockState(Blocks.ACACIA_DOOR.getDefaultState());
    }

    @Override
    public void render(Bone bone, BufferBuilder vertexBuffer, LocalRenderSetting... localRenderSettings) {
        Set<Integer> grids = bone.getGridSizes();
        Iterator<Integer> iter = grids.iterator();
        while (iter.hasNext()) {
            Grid g = bone.getGridBySize(iter.next());
            this.outlineRenderer.render(g.getRayTraceHandler(), vertexBuffer);
            /*Map<Integer, HashMap<Integer, Set<Integer>>> renderPos = g.getDirtyPositions();
            for (int x : renderPos.keySet()) {
                for (int y : renderPos.get(x).keySet()) {
                    for (int z : renderPos.get(x).get(y)) {
                        this.renderBlockState(g.getBlockState(x, y, z), g.getSize(), x, y, z, vertexBuffer);
                    }
                }
            }*/
            HashMap<Integer, HashMap<Integer, HashMap<Integer, OverlyExtendedBlockStorage>>> chunks = g.getChunks();
            for (int x : chunks.keySet()) {
                for (int y : chunks.get(x).keySet()) {
                    for (int z : chunks.get(x).get(y).keySet()) {
                        OverlyExtendedBlockStorage chunk = chunks.get(x).get(y).get(z);
                        for (int i = 0; i < 16; i++) {
                            for (int j = 0; j < 16; j++) {
                                for (int k = 0; k < 16; k++) {
                                    if (chunk.get(i, j, k) != Blocks.AIR.getDefaultState())
                                        this.renderBlockState(chunk.get(i, j, k), g.getSize(), x * 16 + i, y * 16 + j, z * 16 + k, vertexBuffer);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    public void renderBlockState(IBlockState blockState, int gridSize, int x, int y, int z, BufferBuilder buffer, LocalRenderSetting... localRenderSettings) {
        float scale = (float) gridSize / GlobalSettings.unitGridSize();
        GlStateManager.translate(x * scale, y * scale, z * scale);
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.color(1, 1, 1, 1);
        List<TexturedQuad> quads = BlockStateRenderer.getQuadsForState(blockState);
        GlStateManager.scale(scale, scale, scale);
        for (TexturedQuad quad : quads) {
            quad.draw(buffer, 1);
        }
        GlStateManager.scale(1 / scale, 1 / scale, 1 / scale);
        GlStateManager.translate(- x * scale, - y * scale, - z * scale);
    }
}
