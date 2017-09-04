package com.enbecko.nbmodmaker.creator_3d.grids.raytrace;

import com.enbecko.nbmodmaker.INBRenderer;
import com.enbecko.nbmodmaker.LocalRenderSetting;
import com.enbecko.nbmodmaker.OpenGLHelperEnbecko;
import com.enbecko.nbmodmaker.creator_3d.grids.GridRayTraceHandler;
import com.enbecko.nbmodmaker.linalg.real.Vec4;
import net.minecraft.client.renderer.BufferBuilder;

import java.util.List;

public class RenderCuboidOutline implements INBRenderer <CuboidContent> {
    Vec4 v1 = new Vec4(), v2 = new Vec4(), v3 = new Vec4(), v4 = new Vec4(), v5 = new Vec4(), v6 = new Vec4(), v7 = new Vec4(), v8 = new Vec4();
    Vec4 col = new Vec4();

    @Override
    public void render(CuboidContent cuboid, BufferBuilder vertexBuffer, LocalRenderSetting... localRenderSettings) {
        this.v1.update(cuboid.getMinX(), cuboid.getMinY(), cuboid.getMinZ());
        this.v2.update(cuboid.getMaxX(), cuboid.getMinY(), cuboid.getMinZ());
        this.v3.update(cuboid.getMaxX(), cuboid.getMaxY(), cuboid.getMinZ());
        this.v4.update(cuboid.getMinX(), cuboid.getMaxY(), cuboid.getMinZ());

        this.v5.update(cuboid.getMinX(), cuboid.getMinY(), cuboid.getMaxZ());
        this.v6.update(cuboid.getMaxX(), cuboid.getMinY(), cuboid.getMaxZ());
        this.v7.update(cuboid.getMaxX(), cuboid.getMaxY(), cuboid.getMaxZ());
        this.v8.update(cuboid.getMinX(), cuboid.getMaxY(), cuboid.getMaxZ());

        if (cuboid instanceof GridRayTraceHandler)
            this.col.update(1, 0, 0, 1);
        else
            this.col.update(1, 1, 1, 1);
        OpenGLHelperEnbecko.drawLine(this.v1, this.v2, this.col);
        OpenGLHelperEnbecko.drawLine(this.v1, this.v4, this.col);
        OpenGLHelperEnbecko.drawLine(this.v1, this.v5, this.col);

        OpenGLHelperEnbecko.drawLine(this.v7, this.v8, this.col);
        OpenGLHelperEnbecko.drawLine(this.v7, this.v6, this.col);
        OpenGLHelperEnbecko.drawLine(this.v7, this.v3, this.col);

        OpenGLHelperEnbecko.drawLine(this.v2, this.v3, this.col);
        OpenGLHelperEnbecko.drawLine(this.v2, this.v6, this.col);

        OpenGLHelperEnbecko.drawLine(this.v4, this.v8, this.col);
        OpenGLHelperEnbecko.drawLine(this.v4, this.v3, this.col);

        OpenGLHelperEnbecko.drawLine(this.v5, this.v6, this.col);
        OpenGLHelperEnbecko.drawLine(this.v5, this.v8, this.col);
        if (cuboid instanceof ContentHolder) {
            List contentList = ((ContentHolder) cuboid).getContent();
            for (int k = 0; k < ((ContentHolder) cuboid).getContentCount(); k++) {
                if (contentList.get(k) instanceof CuboidContent)
                    this.render((CuboidContent) contentList.get(k), vertexBuffer, localRenderSettings);
            }
        }
    }
}
