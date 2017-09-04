package com.enbecko.nbmodmaker.creator_3d.grids.raytrace;

import com.enbecko.nbmodmaker.linalg.real.Line3D;
import com.enbecko.nbmodmaker.linalg.real.Vec3;
import net.minecraft.entity.player.EntityPlayer;

public class RayTrace3D extends Line3D.Muted {
    private EntityPlayer source;

    public RayTrace3D(EntityPlayer source, Vec3 onPoint, Vec3 u, float length) {
        super(onPoint, u, length);
        this.source = source;
    }

    public EntityPlayer getSource() {
        return this.source;
    }
}
