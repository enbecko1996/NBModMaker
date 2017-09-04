package com.enbecko.nbmodmaker;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.VertexBuffer;

public interface INBRenderer <T> {

    void render(T toRender, BufferBuilder vertexBuffer, LocalRenderSetting ... localRenderSettings);
}
