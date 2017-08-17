package com.enbecko.nbmodmaker.creator_3d.minecraft.tileentities;

import com.enbecko.nbmodmaker.creator_3d.grids.Bone;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by enbec on 17.08.2017.
 */
public class TE_Editor extends TileEntity implements ITickable {
    private final List<Bone> bones = new ArrayList<Bone>();

    public TE_Editor() {

    }

    @Override
    public void update() {

    }
}
