package com.enbecko.nbmodmaker.creator_3d.grids;

import com.enbecko.nbmodmaker.Log;
import com.enbecko.nbmodmaker.Log.LogEnums;
import com.enbecko.nbmodmaker.creator_3d.grids.raytrace.*;
import com.enbecko.nbmodmaker.creator_3d.minecraft.Creator_Main;
import com.enbecko.nbmodmaker.linalg.real.Vec3;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GridRayTraceHandler extends CuboidContent implements ContentHolder<CubicContentHolderGeometry> {
    private List<CubicContentHolderGeometry> theHighestOrderHolders = new ArrayList<>();
    private byte theHighestOrder = 1;
    private Grid theGrid;

    GridRayTraceHandler(Grid theGrid) {
        super(theGrid, new Vec3(), 0, 0, 0);
        this.theGrid = theGrid;
    }

    @Override
    public int getContentCount() {
        return this.theHighestOrderHolders.size();
    }

    @Override
    public List getContent() {
        return this.theHighestOrderHolders;
    }


    public boolean addChunk(OverlyExtendedBlockStorage chunk) {
        for (CubicContentHolderGeometry aContent : this.theHighestOrderHolders) {
            if (aContent.isInside(chunk.getPositionInGridCoords())) {
                if (aContent instanceof ContentHolder) {
                    Log.d(LogEnums.CONTENTHOLDER, this + " \nadd in already existing child: " + aContent + " content = " + chunk);
                    return ((ContentHolder) aContent).addChunk(chunk);
                }
            }
        }
        int size = this.getContentHolderSizeFromOrder(this.theHighestOrder);
        Vec3 chunkP = chunk.getPositionInGridCoords();
        int x = (int) Math.floor(chunkP.getX() / size), y = (int) Math.floor(chunkP.getY() / size), z = (int) Math.floor(chunkP.getZ() / size);
        Vec3 pos1 = new Vec3(x * size, y * size, z * size);
        switch (this.theHighestOrder) {
            case 1:
                FirstOrderHolder firstOrderHolder = (FirstOrderHolder) new FirstOrderHolder(this.theGrid, pos1, true, chunk);
                firstOrderHolder.addParent(this);
                this.addNewChild(firstOrderHolder);
                Log.d(LogEnums.CONTENTHOLDER, this + " \nadd in new firstorderholder: " + firstOrderHolder + " content = " + chunk);
                return true;
            default:
                if (this.theHighestOrder < 1)
                    throw new RuntimeException("highest order of gridraytracehandler must be >= 1");
                HigherOrderHolder higherOrderHolder = (HigherOrderHolder) new HigherOrderHolder(this.theGrid, pos1, this.theHighestOrder, true);
                higherOrderHolder.addParent(this);
                this.addNewChild(higherOrderHolder);
                Log.d(LogEnums.CONTENTHOLDER, this + " \nadd in new higherorderholder: " + higherOrderHolder + " content = " + chunk);
                return higherOrderHolder.addChunk(chunk);
        }
    }

    public int getContentHolderSizeFromOrder(int order) {
        return (int) Math.pow(Creator_Main.contentCubesPerCube, order);
    }

    @Override
    public boolean addNewChild(@Nonnull CubicContentHolderGeometry newHolder) {
        if (newHolder.getOrder() != this.theHighestOrder)
            throw new RuntimeException("Can't add newHolder to Octant with wrong order " + this.theHighestOrder + ", " + newHolder.getOrder());
        if (!this.theHighestOrderHolders.contains(newHolder)) {
            Log.d(LogEnums.CONTENTHOLDER, "add new Child " + newHolder);
            /*
                Add THIS for increasing the order.
             */
            /*
                boolean increaseOrder = false;
                if (this.theHighestOrderHolders.size() > 0) {
                int size = (int) Math.pow(Creator_Main.contentCubesPerCube, this.theHighestOrder + 1);
                Vec3 newHoldPos = newHolder.getPositionInGridCoords();
                Vec3 pos = new Vec3((int) Math.floor(newHoldPos.getX() / size), (int) Math.floor(newHoldPos.getY() / size), (int) Math.floor(newHoldPos.getZ() / size));
                Vec3 pos1 = (Vec3) new Vec3(pos).mulToThis(size);
                HigherOrderHolder test = (HigherOrderHolder) new HigherOrderHolder(this.theGrid, pos1, (byte) (this.theHighestOrder + 1), true);
                List<HigherOrderHolder> tmpContent = new ArrayList<HigherOrderHolder>();
                boolean oneOutside = false, oneInside = false;
                for (CubicContentHolderGeometry tmp : this.theHighestOrderHolders) {
                    if (test.isInside(tmp.getPositionInGridCoords())) {
                        oneInside = true;
                    } else {
                        oneOutside = true;
                    }
                    if (oneInside && oneOutside) {
                        test.addNewChild(newHolder);
                        test.addParent(this);
                        newHolder.addParent(test);
                        newHolder.setMaxOrder(false);
                        tmpContent.add(test);
                        increaseOrder = true;
                        break;
                    }
                }
                if (increaseOrder) {
                    Log.d(LogEnums.CONTENTHOLDER, "increase order " + Arrays.toString(this.theHighestOrderHolders.toArray()));
                    this.theHighestOrder++;
                    float xMin = test.getMinX(), yMin = test.getMinY(), zMin = test.getMinZ(),
                            xMax = test.getMaxX(), yMax = test.getMaxY(), zMax = test.getMaxZ();
                    for (CubicContentHolderGeometry tmp : this.theHighestOrderHolders) {
                        boolean hasParent = false;
                        for (HigherOrderHolder newCont : tmpContent) {
                            if (newCont.isInside(tmp.getPositionInGridCoords())) {
                                Log.d(LogEnums.CONTENTHOLDER, (newCont == test) + "  " + tmp + ", " + newHolder + " " + Arrays.toString(newCont.getContent().toArray()));
                                newCont.addNewChild(tmp);
                                tmp.addParent(newCont);
                                hasParent = true;
                                break;
                            }
                        }
                        if (!hasParent) {
                            pos.update((int) Math.floor(tmp.getPositionInGridCoords().getX() / size), (int) Math.floor(tmp.getPositionInGridCoords().getY() / size), (int) Math.floor(tmp.getPositionInGridCoords().getZ() / size));
                            pos1.update(pos).mulToThis(size);
                            HigherOrderHolder increasedHolder = new HigherOrderHolder(this.theGrid, pos1, this.theHighestOrder, true);
                            tmpContent.add(increasedHolder);
                            increasedHolder.addNewChild(tmp);
                            increasedHolder.addParent(this);
                            tmp.addParent(increasedHolder);
                        }
                    }
                    synchronized (this.theHighestOrderHolders) {
                        this.theHighestOrderHolders.clear();
                        this.theHighestOrderHolders.addAll(tmpContent);
                        this.makeSizeFromNewContentList();
                        tmpContent.clear();
                    }
                } else {
                    this.theHighestOrderHolders.add(newHolder);
                    this.updateSizeForNewChild(newHolder);
                    return true;
                }
            } else*/
            {
                this.theHighestOrderHolders.add(newHolder);
                this.updateSizeForNewChild(newHolder);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeChild(@Nonnull CubicContentHolderGeometry content) {
        if (content.getOrder() != this.theHighestOrder)
            throw new RuntimeException("Can't remove content from Octant with wrong order " + this.theHighestOrder + ", " + content.getOrder());
        if (this.theHighestOrderHolders.contains(content)) {
            this.theHighestOrderHolders.remove(content);
            if (this.theHighestOrderHolders.size() <= 0) {
                this.update(0, 0, 0, 0, 0, 0);
                return true;
            } else if (this.theHighestOrderHolders.size() == 1) {
                while (this.theHighestOrderHolders.size() == 1 && this.theHighestOrderHolders.get(0) instanceof HigherOrderHolder) {
                    HigherOrderHolder higherOrderHolder = (HigherOrderHolder) this.theHighestOrderHolders.get(0);
                    synchronized (this.theHighestOrderHolders) {
                        this.theHighestOrderHolders.clear();
                        this.theHighestOrderHolders.addAll(higherOrderHolder.getContent());
                        for (CubicContentHolderGeometry holder : this.theHighestOrderHolders)
                            holder.setMaxOrder(true).addParent(this);
                        this.makeSizeFromNewContentList();
                    }
                }
                if (this.theHighestOrderHolders.size() == 1 && this.theHighestOrderHolders.get(0) instanceof FirstOrderHolder) {
                    this.makeSizeFromNewContentList();
                }
            } else if (content.getMinX() == this.getMinX() || content.getMinY() == this.getMinY() || content.getMinZ() == this.getMinZ() ||
                    content.getMaxX() == this.getMaxX() || content.getMaxY() == this.getMaxY() || content.getMaxZ() == this.getMaxZ()) {
                float xMin = Float.POSITIVE_INFINITY, yMin = Float.POSITIVE_INFINITY, zMin = Float.POSITIVE_INFINITY,
                        xMax = Float.NEGATIVE_INFINITY, yMax = Float.NEGATIVE_INFINITY, zMax = Float.NEGATIVE_INFINITY;
                for (CubicContentHolderGeometry cur : this.theHighestOrderHolders) {
                    if (cur.getMaxX() > xMax)
                        xMax = cur.getMaxX();
                    if (cur.getMaxY() > yMax)
                        yMax = cur.getMaxY();
                    if (cur.getMaxZ() > zMax)
                        zMax = cur.getMaxZ();
                    if (cur.getMinX() < xMin)
                        xMin = cur.getMinX();
                    if (cur.getMinY() < yMin)
                        yMin = cur.getMinY();
                    if (cur.getMinZ() < zMin)
                        zMin = cur.getMinZ();
                }
                this.update(xMin, yMin, zMin, xMax, yMax, zMax);
            }
            return true;
        }
        return false;
    }

    public void updateSizeForNewChild(CubicContentHolderGeometry child) {
        float xMin = this.getMinX(), yMin = this.getMinY(), zMin = this.getMinZ(), xMax = this.getMaxX(), yMax = this.getMaxY(), zMax = this.getMaxZ();
        if (this.theHighestOrderHolders.size() == 1) {
            xMin = child.getMinX();
            yMin = child.getMinY();
            zMin = child.getMinZ();
            xMax = child.getMaxX();
            yMax = child.getMaxY();
            zMax = child.getMaxZ();
        } else {
            if (child.getMaxX() > xMax)
                xMax = child.getMaxX();
            if (child.getMaxY() > yMax)
                yMax = child.getMaxY();
            if (child.getMaxZ() > zMax)
                zMax = child.getMaxZ();
            if (child.getMinX() < xMin)
                xMin = child.getMinX();
            if (child.getMinY() < yMin)
                yMin = child.getMinY();
            if (child.getMinZ() < zMin)
                zMin = child.getMinZ();
        }
        this.update(xMin, yMin, zMin, xMax, yMax, zMax);
    }

    public void makeSizeFromNewContentList() {
        float xMin = this.getMinX(), yMin = this.getMinY(), zMin = this.getMinZ(), xMax = this.getMaxX(), yMax = this.getMaxY(), zMax = this.getMaxZ();
        if (this.theHighestOrderHolders.size() == 1) {
            CubicContentHolderGeometry child = this.theHighestOrderHolders.get(0);
            xMin = child.getMinX();
            yMin = child.getMinY();
            zMin = child.getMinZ();
            xMax = child.getMaxX();
            yMax = child.getMaxY();
            zMax = child.getMaxZ();
        } else if (this.theHighestOrderHolders.size() > 1) {
            xMin = Float.POSITIVE_INFINITY;
            yMin = Float.POSITIVE_INFINITY;
            zMin = Float.POSITIVE_INFINITY;
            xMax = Float.NEGATIVE_INFINITY;
            yMax = Float.NEGATIVE_INFINITY;
            zMax = Float.NEGATIVE_INFINITY;
            for (CubicContentHolderGeometry tmpChild : this.theHighestOrderHolders) {
                if (tmpChild.getMaxX() > xMax)
                    xMax = tmpChild.getMaxX();
                if (tmpChild.getMaxY() > yMax)
                    yMax = tmpChild.getMaxY();
                if (tmpChild.getMaxZ() > zMax)
                    zMax = tmpChild.getMaxZ();
                if (tmpChild.getMinX() < xMin)
                    xMin = tmpChild.getMinX();
                if (tmpChild.getMinY() < yMin)
                    yMin = tmpChild.getMinY();
                if (tmpChild.getMinZ() < zMin)
                    zMin = tmpChild.getMinZ();
            }
        } else {
            xMin /= Math.abs(xMin);
            xMax /= Math.abs(xMax);
            yMin /= Math.abs(yMin);
            yMax /= Math.abs(yMax);
            zMin /= Math.abs(zMin);
            zMax /= Math.abs(zMax);
        }
        this.update(xMin, yMin, zMin, xMax, yMax, zMax);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void askForOrderDegrade(@Nonnull CubicContentHolderGeometry asker, CubicContentHolderGeometry degradeTo) {
        List<CubicContentHolderGeometry> tmp = this.getContent();
        if (tmp.contains(asker)) {
            if (tmp.size() == 1) {
                tmp.remove(asker);
                tmp.add(degradeTo);
                degradeTo.addParent(this);
                int size = degradeTo.getSize();
                this.update(degradeTo.getPositionInGridCoords(), size, size, size);
                this.theHighestOrder = degradeTo.getOrder();
            } else {
                List<CubicContentHolderGeometry> tmpContent = new ArrayList<CubicContentHolderGeometry>();
                boolean degrade = false;
                List<CubicContentHolderGeometry> tmpContent2 = new ArrayList<CubicContentHolderGeometry>();
                maxDegrade:
                while (this.theHighestOrder > degradeTo.getOrder()) {
                    for (CubicContentHolderGeometry holder : tmp) {
                        if (holder instanceof HigherOrderHolder) {
                            List<CubicContentHolderGeometry> childs = ((HigherOrderHolder) holder).getContent();
                            if (childs.size() == 1) {
                                tmpContent2.add(childs.get(0));
                            } else {
                                break maxDegrade;
                            }
                        }
                    }
                    degrade = true;
                    tmpContent.addAll(tmpContent2);
                    tmpContent2.clear();
                    tmp = tmpContent;
                    this.theHighestOrder--;
                }
                if (degrade) {
                    synchronized (this.theHighestOrderHolders) {
                        this.theHighestOrderHolders.clear();
                        this.theHighestOrderHolders.addAll(tmpContent);
                        this.makeSizeFromNewContentList();
                        for (CubicContentHolderGeometry holder : this.theHighestOrderHolders)
                            holder.setMaxOrder(true).addParent(this);
                        tmpContent.clear();
                        tmpContent2.clear();
                    }
                }
            }
        } else {
            throw new RuntimeException("Someone asking for Degrade which is none of my childs. " + this + ", " + asker);
        }
    }
}
