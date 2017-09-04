package com.enbecko.nbmodmaker.creator_3d.minecraft.renderer;

import com.enbecko.nbmodmaker.GlobalSettings;
import com.enbecko.nbmodmaker.INBRenderer;
import com.enbecko.nbmodmaker.LocalRenderSetting;
import com.enbecko.nbmodmaker.OpenGLHelperEnbecko;
import com.enbecko.nbmodmaker.creator_3d.grids.GridRayTraceHandler;
import com.enbecko.nbmodmaker.creator_3d.grids.raytrace.ContentHolder;
import com.enbecko.nbmodmaker.creator_3d.grids.raytrace.CuboidContent;
import com.enbecko.nbmodmaker.creator_3d.grids.raytrace.FirstOrderHolder;
import com.enbecko.nbmodmaker.linalg.real.Vec3;
import com.enbecko.nbmodmaker.linalg.real.Vec4;
import net.minecraft.client.renderer.BufferBuilder;

import java.util.List;

public class RenderCuboidOutline implements INBRenderer <CuboidContent> {
    Vec3 v1 = new Vec3(), v2 = new Vec3(), v3 = new Vec3(), v4 = new Vec3(), v5 = new Vec3(), v6 = new Vec3(), v7 = new Vec3(), v8 = new Vec3();
    Vec4 col = new Vec4();

    @Override
    public void render(CuboidContent cuboid, BufferBuilder vertexBuffer, LocalRenderSetting... localRenderSettings) {
        float size = cuboid.getParentGrid().getSize(), unit = GlobalSettings.unitGridSize();
        float diff = 16 * size / unit;
        this.v1.update(cuboid.getMinX(), cuboid.getMinY(), cuboid.getMinZ()).mulToThis(diff);
        this.v2.update(cuboid.getMaxX(), cuboid.getMinY(), cuboid.getMinZ()).mulToThis(diff);
        this.v3.update(cuboid.getMaxX(), cuboid.getMaxY(), cuboid.getMinZ()).mulToThis(diff);
        this.v4.update(cuboid.getMinX(), cuboid.getMaxY(), cuboid.getMinZ()).mulToThis(diff);

        this.v5.update(cuboid.getMinX(), cuboid.getMinY(), cuboid.getMaxZ()).mulToThis(diff);
        this.v6.update(cuboid.getMaxX(), cuboid.getMinY(), cuboid.getMaxZ()).mulToThis(diff);
        this.v7.update(cuboid.getMaxX(), cuboid.getMaxY(), cuboid.getMaxZ()).mulToThis(diff);
        this.v8.update(cuboid.getMinX(), cuboid.getMaxY(), cuboid.getMaxZ()).mulToThis(diff);

        if (cuboid instanceof GridRayTraceHandler)
            this.col.update(1, 0, 0, 1);
        else if (cuboid instanceof FirstOrderHolder)
            this.col.update(0, 1, 0, 1);
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
