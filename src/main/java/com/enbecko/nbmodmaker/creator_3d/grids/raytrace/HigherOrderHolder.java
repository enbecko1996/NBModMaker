package com.enbecko.nbmodmaker.creator_3d.grids.raytrace;

import com.enbecko.nbmodmaker.Log;
import com.enbecko.nbmodmaker.Log.LogEnums;
import com.enbecko.nbmodmaker.creator_3d.grids.Grid;
import com.enbecko.nbmodmaker.creator_3d.grids.OverlyExtendedBlockStorage;
import com.enbecko.nbmodmaker.creator_3d.minecraft.Creator_Main;
import com.enbecko.nbmodmaker.linalg.real.Vec3;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by enbec on 21.02.2017.
 */
public class HigherOrderHolder extends CubicContentHolderGeometry implements ContentHolder <CubicContentHolderGeometry>{
    private CubicContentHolderGeometry[][][] content = new CubicContentHolderGeometry[Creator_Main.contentCubesPerCube][Creator_Main.contentCubesPerCube][Creator_Main.contentCubesPerCube];
    ContentHolder parent;
    protected final List<CubicContentHolderGeometry> rayTraceResult = new ArrayList<CubicContentHolderGeometry>();
    protected double[] distance = new double[Creator_Main.contentCubesPerCube];

    public HigherOrderHolder(Grid parentGrid, Vec3 positionInGridCoords, byte order, boolean isMaxOrder) {
        super(parentGrid, positionInGridCoords, order, isMaxOrder);
    }

    @Override
    public Content getRayTraceResult(RayTrace3D rayTrace3D) {
        List<CubicContentHolderGeometry> holders = this.getContent();
        this.rayTraceResult.clear();
        for (int l = 0; l < distance.length; l++) {
            if (distance[l] != 0)
                distance[l] = 0;
            else
                break;
        }
        Vec3 pos;
        for (CubicContentHolderGeometry holder : holders) {
            if (holder.isInside(rayTrace3D.getOnPoint())) {
                this.rayTraceResult.add(0, holder);
                double tmp = distance[0];
                for (int l = 1; l < distance.length; l++) {
                    if (l > 0 && distance[l - 1] != 0) {
                        double tt = distance[l];
                        distance[l] = tmp;
                        tmp = tt;
                    } else
                        break;
                }
            }
            if ((pos = holder.checkIfCrosses(rayTrace3D)) != null) {
                double d = ((Vec3)pos.subFromThis(rayTrace3D.getOnPoint())).length();
                int k = 0;
                for (; k < distance.length; k++) {
                    if (distance[k] == 0 || distance[k] > d) {
                        if (distance[k] != 0) {
                            double tmp = distance[k];
                            for (int l = k + 1; l < distance.length; l++) {
                                if (l > 0 && distance[l - 1] != 0) {
                                    double tt = distance[l];
                                    distance[l] = tmp;
                                    tmp = tt;
                                } else
                                    break;
                            }
                        }
                        distance[k] = d;
                        break;
                    }
                }
                this.rayTraceResult.add(k, holder);
            }
        }
        for (CubicContentHolderGeometry holder : this.rayTraceResult) {
            Content result;
            if ((result = holder.getRayTraceResult(rayTrace3D)) != null)
                return result;
        }
        return null;
    }

    @Override
    public int getContentCount() {
        int out = 0;
        for (int k = 0; k < Creator_Main.contentCubesPerCube; k++) {
            for (int l = 0; l < Creator_Main.contentCubesPerCube; l++) {
                for (int m = 0; m < Creator_Main.contentCubesPerCube; m++) {
                    if (this.content[k][l][m] != null)
                        out++;
                }
            }
        }
        return out;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CubicContentHolderGeometry> getContent() {
        List out = new ArrayList<CubicContentHolderGeometry>();
        for (int k = 0; k < Creator_Main.contentCubesPerCube; k++) {
            for (int l = 0; l < Creator_Main.contentCubesPerCube; l++) {
                for (int m = 0; m < Creator_Main.contentCubesPerCube; m++) {
                    if (this.content[k][l][m] != null)
                        out.add(this.content[k][l][m]);
                }
            } 
        }
        return out;
    }

    @Override
    public boolean addChunk(OverlyExtendedBlockStorage chunk) {
        if (this.isInside(chunk.getPositionInGridCoords())) {
            for (int k = 0; k < Creator_Main.contentCubesPerCube; k++) {
                for (int l = 0; l < Creator_Main.contentCubesPerCube; l++) {
                    for (CubicContentHolderGeometry aContent : this.content[k][l]) {
                        if (aContent != null) {
                            if (aContent.isInside(chunk.getPositionInGridCoords())) {
                                if (aContent instanceof ContentHolder) {
                                    Log.d(LogEnums.CONTENTHOLDER, this + " \nadd in already existing child: " + aContent + " content = " + chunk);
                                    return ((ContentHolder) aContent).addChunk(chunk);
                                }
                            }
                        }
                    }
                }
            }
            Vec3 pos = (Vec3) new Vec3(chunk.getPositionInGridCoords()).subFromThis(this.positionInGridCoords);
            int belowOrderSize = (this.getSize() / Creator_Main.contentCubesPerCube);
            int k = (int) pos.getX() / belowOrderSize, l = (int) pos.getY() / belowOrderSize, m = (int) pos.getZ() / belowOrderSize;
            Vec3 pos1 = (Vec3) this.positionInGridCoords.add(new Vec3(k * belowOrderSize, l * belowOrderSize, m * belowOrderSize), new Vec3());
            if (k < 0 || k >= Creator_Main.contentCubesPerCube || l < 0 || l >= Creator_Main.contentCubesPerCube || m < 0 || m >= Creator_Main.contentCubesPerCube)
                throw new RuntimeException("This: " + chunk + " doesn't belong here: " + this + ", k = " + k + ", l = " + l + ", m = " + m + ", " + pos);
            switch (this.getOrder()) {
                case 2:
                    FirstOrderHolder firstOrderHolder = (FirstOrderHolder) new FirstOrderHolder(this.getParentGrid(), pos1, true, chunk);
                    firstOrderHolder.addParent(this);
                    this.addNewChild(firstOrderHolder);
                    return firstOrderHolder.addChunk(chunk);
                default:
                    HigherOrderHolder higherOrderHolder = (HigherOrderHolder) new HigherOrderHolder(this.getParentGrid(), pos1, (byte) (this.getOrder() - 1), false);
                    higherOrderHolder.addParent(this);
                    this.addNewChild(higherOrderHolder);
                    return higherOrderHolder.addChunk(chunk);
            }
        } else
            throw new RuntimeException(chunk + " is not in here: " + this);
    }

    @Override
    public boolean addNewChild(@Nonnull CubicContentHolderGeometry content) {
        if (content.getOrder() != this.getOrder() - 1)
            throw new RuntimeException("The ContentHolder you want to add must have a order which is one lower than this's " + this + ", " + content);
        Vec3 pos = (Vec3) new Vec3(content.getPositionInGridCoords()).subFromThis(this.positionInGridCoords);
        int size = content.getSize();
        int k = (int) pos.getX() / size, l = (int) pos.getY() / size, m = (int) pos.getZ() / size;
        if (k < 0 || k >= Creator_Main.contentCubesPerCube || l < 0 || l >= Creator_Main.contentCubesPerCube || m < 0 || m >= Creator_Main.contentCubesPerCube)
            throw new RuntimeException("This: " + content + " doesn't belong here: " + this);
        if (this.content[k][l][m] == null) {
            this.content[k][l][m] = content;
            return true;
        } else {
            throw new RuntimeException("Can't put a CubicContentHolder in HigherOrderHolder where there already is one " + this +", " + this.content[k][l][m] + ", " + content +  ", k = " + k + ", l = " + l + ", m = " + m);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean removeChild(@Nonnull CubicContentHolderGeometry content) {
        Vec3 pos = (Vec3) new Vec3(content.getPositionInGridCoords()).subFromThis(this.positionInGridCoords);
        int size = content.getSize();
        int k = (int) (Math.floor(pos.getX() / size)), l = (int) (Math.floor(pos.getY() / size)), m = (int) (Math.floor(pos.getZ() / size));
        if (k < 0 || k >= Creator_Main.contentCubesPerCube || l < 0 || l >= Creator_Main.contentCubesPerCube || m < 0 || m >= Creator_Main.contentCubesPerCube)
            throw new RuntimeException("This: " + content + " doesn't belong here: " + this);
        if (this.content[k][l][m] == content) {
            this.content[k][l][m] = null;
            List<CubicContentHolderGeometry> tmp = this.getContent();
            if (tmp.size() == 1) {
                this.getParent().askForOrderDegrade(this, tmp.get(0));
            }
            else if (tmp.size() == 0) {
                this.getParent().removeChild(this);
            }
            return true;
        } else {
            throw new RuntimeException("Can't delete a CubicContentHolder in HigherOrderHolder where it isn't " + this + ", " + content + ", " + this.content[k][l][m] + ", k = " + k + ", l = " + l + ", m = " + m);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void askForOrderDegrade(@Nonnull CubicContentHolderGeometry asker, CubicContentHolderGeometry degradeTo) {
        List<CubicContentHolderGeometry> tmp = this.getContent();
        if (tmp.contains(asker)) {
            if (tmp.size() == 1) {
                this.getParent().askForOrderDegrade(this, degradeTo);
            }
        } else {
            throw new RuntimeException("Someone asking for Degrade which is none of my childs. " + this +", " + asker);
        }
    }

    @Override
    public int getParentCount() {
        return 1;
    }

    @Override
    public ContentHolder getParent(int pos) {
        return this.parent;
    }

    public ContentHolder getParent() {
        return this.parent;
    }

    @Override
    public boolean addParent(ContentHolder higherOrderHolder) {
        if (higherOrderHolder != null) {
            this.parent = higherOrderHolder;
            return true;
        } else
            throw new RuntimeException("Can't make a null parent " + this);
    }

    public void setParent(HigherOrderHolder contentHolder) {
        this.parent = contentHolder;
    }

    /**
    @Override
    @SideOnly(Side.CLIENT)
    public void renderBlockState(VertexBuffer buffer, LocalRenderSetting... localRenderSettings) {
        if (GlobalRenderSetting.getRenderMode() == GlobalRenderSetting.RenderMode.DEBUG)
            super.renderBlockState(buffer);
        for (int k = 0; k < Creator_Main.contentCubesPerCube; k++) {
            for (int l = 0; l < Creator_Main.contentCubesPerCube; l++) {
                for (int m = 0; m < Creator_Main.contentCubesPerCube; m++) {
                    if (this.content[k][l][m] != null)
                        this.content[k][l][m].renderBlockState(buffer, localRenderSettings);
                }
            }
        }
    }


    @Override
    public void renderContentWithExceptions(VertexBuffer buffer, @Nullable List<Content> exceptions, LocalRenderSetting... localRenderSettings) {
        if (GlobalRenderSetting.getRenderMode() == GlobalRenderSetting.RenderMode.DEBUG)
            super.renderBlockState(buffer);
        for (int k = 0; k < Creator_Main.contentCubesPerCube; k++) {
            for (int l = 0; l < Creator_Main.contentCubesPerCube; l++) {
                for (int m = 0; m < Creator_Main.contentCubesPerCube; m++) {
                    if (this.content[k][l][m] != null)
                        this.content[k][l][m].renderContentWithExceptions(buffer, exceptions, localRenderSettings);
                }
            }
        }
    }*/
}


