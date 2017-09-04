package com.enbecko.nbmodmaker.linalg.real;

import com.enbecko.nbmodmaker.creator_3d.grids.raytrace.RayTrace3D;

import javax.annotation.Nullable;

public abstract class Quadrilateral3D {
    Vec4 v1, v2, v3, v4;
    Vec4 tmpRhs = new Vec4();
    private Vec4 onPoint, u, v;
    boolean isSpannedSurface = false, isParallelogram = false;
    private final float PARALLELOGRAM_THRESHOLD = 0.001f;
    private Matrix_Intersect intersectMatrix;

    public Quadrilateral3D(Vec4 v1, Vec4 v2, Vec4 v3, Vec4 v4) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;
        this.updateSymmetry();
    }

    public Quadrilateral3D(Vec4 onPoint, Vec4 u, Vec4 v) {
        this.u = u;
        this.u.setW(0);
        this.v = v;
        this.v.setW(0);
        this.isSpannedSurface = true;
        this.updateSymmetry();
    }

    public static void main(String[] args) {
        Quadrilateral3D q = new Quadrilateral3D.ManualUpdate(new Vec4(0, 0, 0), new Vec4(1, 0, 0), new Vec4(1, 1, 0), new Vec4(0, 1, 0));
        RayTrace3D rayTrace3D = new RayTrace3D(null, new Vec4(0.5f, 0.5f, -1), new Vec4(0, 0, 1), 100);

        long time = System.currentTimeMillis();
        int iteration = 10000000;
        for (int i = 0; i < iteration; i++)
            q.isCrossedBy(rayTrace3D);
        long wasted = System.currentTimeMillis() - time;
        System.out.println(wasted / (double) iteration + "\n" + q.isCrossedBy(rayTrace3D));
    }

    public void updateSymmetry() {
        System.out.println("hello");
        if (this.isSpannedSurface)
            this.isParallelogram = true;
        else {
            Vec4 tmp1;
            this.getV3().sub(this.getV4(), (tmp1 = new Vec4()));
            Vec4 tmp2;
            this.getV3().sub(this.getV2(), (tmp2 = new Vec4()));
            this.isParallelogram = ((Vec4) tmp1.subFromThis(this.getU())).lengthOfVec3(false) <= this.PARALLELOGRAM_THRESHOLD && ((Vec4) tmp2.subFromThis(this.getV())).lengthOfVec3(false) <= this.PARALLELOGRAM_THRESHOLD;
        }
    }

    @Nullable
    public Vec4 isCrossedBy(Line3D line3D) {
        this.updateSymmetry();
        if (this.isParallelogram) {
            if (this.intersectMatrix == null)
                this.intersectMatrix = Matrix_Intersect.MATRIXFACTORY.makeMatrixFromColumns(this.getU(), this.getV(), (Vec4) line3D.getU().mulToThis(-1));
            else
                this.intersectMatrix.setColumn(2, (Vec4) line3D.getU().mulToThis(-1));
            line3D.getU().mulToThis(-1);
            tmpRhs.update(line3D.getOnPoint()).subFromThis(this.getOnPoint());
            intersectMatrix.doLUDecomposition();
            Vec4 r_s_t;
            intersectMatrix.solveLGS_fromLU(tmpRhs, (r_s_t = new Vec4()));
            if (r_s_t.getZ() > 0 && r_s_t.getZ() < line3D.getLength() && r_s_t.getX() >= 0 && r_s_t.getX() <= 1 && r_s_t.getY() >= 0 && r_s_t.getY() <= 1) {
                return line3D.advanceOnVecAndReturnPosition(r_s_t.getZ());
            }
        }
        return null;
    }

    public Vec4 getOnPoint() {
        return this.isSpannedSurface ? this.onPoint : this.v1;
    }

    public Vec4 getU() {
        return this.isSpannedSurface ? this.u : (Vec4) this.v2.sub(this.v1, u == null ? (u = new Vec4(0, 0, 0, 0)) : u);
    }

    public Vec4 getV() {
        return this.isSpannedSurface ? this.v : (Vec4) this.v4.sub(this.v1, v == null ? (v = new Vec4(0, 0, 0, 0)) : v);
    }

    public Vec4 getV1() {
        return !this.isSpannedSurface ? this.v1 : this.onPoint;
    }

    public Vec4 getV2() {
        return !this.isSpannedSurface ? v2 : (Vec4) this.onPoint.add(this.u, v2 == null ? (v2 = new Vec4()) : v2);
    }

    public Vec4 getV3() {
        return !this.isSpannedSurface ? v3 : (Vec4) this.onPoint.add(this.u, v3 == null ? (v3 = new Vec4()) : v3).add(this.v, this.v3);
    }

    public Vec4 getV4() {
        return !this.isSpannedSurface ? v4 : (Vec4) this.onPoint.add(this.v, v4 == null ? (v4 = new Vec4()) : v4);
    }

    public static class AutoUpdate extends Quadrilateral3D {
        public AutoUpdate(Vec4 v1, Vec4 v2, Vec4 v3, Vec4 v4) {
            super(v1, v2, v3, v4);
        }

        public AutoUpdate(Vec4 onPoint, Vec4 u, Vec4 v) {
            super(onPoint, u, v);
        }

        @Override
        public void updateSymmetry() {
            super.updateSymmetry();
        }
    }

    public static class ManualUpdate extends Quadrilateral3D {
        public ManualUpdate(Vec4 v1, Vec4 v2, Vec4 v3, Vec4 v4) {
            super(new Vec4(v1), new Vec4(v2), new Vec4(v3), new Vec4(v4));
            super.updateSymmetry();
        }

        public ManualUpdate(Vec4 onPoint, Vec4 u, Vec4 v) {
            super(new Vec4(onPoint), new Vec4(u), new Vec4(v));
            super.updateSymmetry();
        }

        @Override
        public void updateSymmetry() {
        }

        public void setOnPoint(Vec4 onPoint) {
            if (this.isSpannedSurface)
                ((Vec4) this.getOnPoint().setMuted(false)).update(onPoint).setMuted(true);
            else
                throw new RuntimeException("Can't set onPoint when this is not a spanned surface " + this);
            super.updateSymmetry();
        }

        public void setU(Vec4 u) {
            if (this.isSpannedSurface)
                ((Vec4) this.getU().setMuted(false)).update(u).setMuted(true);
            else
                throw new RuntimeException("Can't set u when this is not a spanned surface " + this);
            super.updateSymmetry();
        }

        public void setV(Vec4 v) {
            if (this.isSpannedSurface)
                ((Vec4) this.getV().setMuted(false)).update(v).setMuted(true);
            else
                throw new RuntimeException("Can't set v when this is not a spanned surface " + this);
            super.updateSymmetry();
        }

        public void setV1(Vec4 v1) {
            if (!this.isSpannedSurface)
                ((Vec4) this.getV1().setMuted(false)).update(v1).setMuted(true);
            else
                throw new RuntimeException("Can't set v1 when this is a spanned surface " + this);
            super.updateSymmetry();
        }

        public void setV2(Vec4 v2) {
            if (!this.isSpannedSurface)
                ((Vec4) this.getV2().setMuted(false)).update(v2).setMuted(true);
            else
                throw new RuntimeException("Can't set v2 when this is a spanned surface " + this);
            super.updateSymmetry();
        }

        public void setV3(Vec4 v3) {
            if (!this.isSpannedSurface)
                ((Vec4) this.getV3().setMuted(false)).update(v3).setMuted(true);
            else
                throw new RuntimeException("Can't set v3 when this is a spanned surface " + this);
            super.updateSymmetry();
        }

        public void setV4(Vec4 v4) {
            if (!this.isSpannedSurface)
                ((Vec4) this.getV4().setMuted(false)).update(v4).setMuted(true);
            else
                throw new RuntimeException("Can't set v4 when this is a spanned surface " + this);
            super.updateSymmetry();
        }
    }

    public static class Muted extends Quadrilateral3D {
        public Muted(Vec4 v1, Vec4 v2, Vec4 v3, Vec4 v4) {
            super((Vec4) new Vec4(v1).setMuted(true), (Vec4) new Vec4(v2).setMuted(true), (Vec4) new Vec4(v3).setMuted(true), (Vec4) new Vec4(v4).setMuted(true));
        }

        public Muted(Vec4 onPoint, Vec4 u, Vec4 v) {
            super((Vec4) new Vec4(onPoint).setMuted(true), (Vec4) new Vec4(u).setMuted(true), (Vec4) new Vec4(v).setMuted(true));
        }

        @Override
        public void updateSymmetry() {
        }
    }
}
