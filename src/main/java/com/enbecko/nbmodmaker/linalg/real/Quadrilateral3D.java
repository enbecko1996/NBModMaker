package com.enbecko.nbmodmaker.linalg.real;

import com.enbecko.nbmodmaker.creator_3d.grids.raytrace.RayTrace3D;

import javax.annotation.Nullable;

public abstract class Quadrilateral3D {
    Vec3 v1, v2, v3, v4;
    Vec3 tmpRhs = new Vec3();
    private Vec3 onPoint, u, v;
    boolean isSpannedSurface = false, isParallelogram = false;
    private final float PARALLELOGRAM_THRESHOLD = 0.001f;
    private Matrix_Intersect intersectMatrix;

    public Quadrilateral3D(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;
        this.updateSymmetry();
    }

    public Quadrilateral3D(Vec3 onPoint, Vec3 u, Vec3 v) {
        this.u = u;
        this.v = v;
        this.isSpannedSurface = true;
        this.updateSymmetry();
    }

    public static void main(String[] args) {
        Quadrilateral3D q = new Quadrilateral3D.ManualUpdate(new Vec3(0, 0, 0), new Vec3(1, 0, 0), new Vec3(1, 1, 0), new Vec3(0, 1, 0));
        RayTrace3D rayTrace3D = new RayTrace3D(null, new Vec3(0.5f, 0.5f, -1), new Vec3(0, 0, 1), 100);

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
            Vec3 tmp1;
            this.getV3().sub(this.getV4(), (tmp1 = new Vec3()));
            Vec3 tmp2;
            this.getV3().sub(this.getV2(), (tmp2 = new Vec3()));
            this.isParallelogram = ((Vec3) tmp1.subFromThis(this.getU())).length() <= this.PARALLELOGRAM_THRESHOLD && ((Vec3) tmp2.subFromThis(this.getV())).length() <= this.PARALLELOGRAM_THRESHOLD;
        }
    }

    @Nullable
    public Vec3 isCrossedBy(Line3D line3D) {
        this.updateSymmetry();
        if (this.isParallelogram) {
            if (this.intersectMatrix == null)
                this.intersectMatrix = Matrix_Intersect.MATRIXFACTORY.makeMatrixFromColumns(this.getU(), this.getV(), (Vec3) line3D.getU().mulToThis(-1));
            else
                this.intersectMatrix.setColumn(2, (Vec3) line3D.getU().mulToThis(-1));
            line3D.getU().mulToThis(-1);
            tmpRhs.update(line3D.getOnPoint()).subFromThis(this.getOnPoint());
            intersectMatrix.doLUDecomposition();
            Vec3 r_s_t;
            intersectMatrix.solveLGS_fromLU(tmpRhs, (r_s_t = new Vec3()));
            if (r_s_t.getZ() > 0 && r_s_t.getZ() < line3D.getLength() && r_s_t.getX() >= 0 && r_s_t.getX() <= 1 && r_s_t.getY() >= 0 && r_s_t.getY() <= 1) {
                return line3D.advanceOnVecAndReturnPosition(r_s_t.getZ());
            }
        }
        return null;
    }

    public Vec3 getOnPoint() {
        return this.isSpannedSurface ? this.onPoint : this.v1;
    }

    public Vec3 getU() {
        return this.isSpannedSurface ? this.u : (Vec3) this.v2.sub(this.v1, u == null ? (u = new Vec3(0, 0, 0)) : u);
    }

    public Vec3 getV() {
        return this.isSpannedSurface ? this.v : (Vec3) this.v4.sub(this.v1, v == null ? (v = new Vec3(0, 0, 0)) : v);
    }

    public Vec3 getV1() {
        return !this.isSpannedSurface ? this.v1 : this.onPoint;
    }

    public Vec3 getV2() {
        return !this.isSpannedSurface ? v2 : (Vec3) this.onPoint.add(this.u, v2 == null ? (v2 = new Vec3()) : v2);
    }

    public Vec3 getV3() {
        return !this.isSpannedSurface ? v3 : (Vec3) this.onPoint.add(this.u, v3 == null ? (v3 = new Vec3()) : v3).add(this.v, this.v3);
    }

    public Vec3 getV4() {
        return !this.isSpannedSurface ? v4 : (Vec3) this.onPoint.add(this.v, v4 == null ? (v4 = new Vec3()) : v4);
    }

    public static class AutoUpdate extends Quadrilateral3D {
        public AutoUpdate(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
            super(v1, v2, v3, v4);
        }

        public AutoUpdate(Vec3 onPoint, Vec3 u, Vec3 v) {
            super(onPoint, u, v);
        }

        @Override
        public void updateSymmetry() {
            super.updateSymmetry();
        }
    }

    public static class ManualUpdate extends Quadrilateral3D {
        public ManualUpdate(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
            super(new Vec3(v1), new Vec3(v2), new Vec3(v3), new Vec3(v4));
            super.updateSymmetry();
        }

        public ManualUpdate(Vec3 onPoint, Vec3 u, Vec3 v) {
            super(new Vec3(onPoint), new Vec3(u), new Vec3(v));
            super.updateSymmetry();
        }

        @Override
        public void updateSymmetry() {
        }

        public void setOnPoint(Vec3 onPoint) {
            if (this.isSpannedSurface)
                ((Vec3) this.getOnPoint().setMuted(false)).update(onPoint).setMuted(true);
            else
                throw new RuntimeException("Can't set onPoint when this is not a spanned surface " + this);
            super.updateSymmetry();
        }

        public void setU(Vec3 u) {
            if (this.isSpannedSurface)
                ((Vec3) this.getU().setMuted(false)).update(u).setMuted(true);
            else
                throw new RuntimeException("Can't set u when this is not a spanned surface " + this);
            super.updateSymmetry();
        }

        public void setV(Vec3 v) {
            if (this.isSpannedSurface)
                ((Vec3) this.getV().setMuted(false)).update(v).setMuted(true);
            else
                throw new RuntimeException("Can't set v when this is not a spanned surface " + this);
            super.updateSymmetry();
        }

        public void setV1(Vec3 v1) {
            if (!this.isSpannedSurface)
                ((Vec3) this.getV1().setMuted(false)).update(v1).setMuted(true);
            else
                throw new RuntimeException("Can't set v1 when this is a spanned surface " + this);
            super.updateSymmetry();
        }

        public void setV2(Vec3 v2) {
            if (!this.isSpannedSurface)
                ((Vec3) this.getV2().setMuted(false)).update(v2).setMuted(true);
            else
                throw new RuntimeException("Can't set v2 when this is a spanned surface " + this);
            super.updateSymmetry();
        }

        public void setV3(Vec3 v3) {
            if (!this.isSpannedSurface)
                ((Vec3) this.getV3().setMuted(false)).update(v3).setMuted(true);
            else
                throw new RuntimeException("Can't set v3 when this is a spanned surface " + this);
            super.updateSymmetry();
        }

        public void setV4(Vec3 v4) {
            if (!this.isSpannedSurface)
                ((Vec3) this.getV4().setMuted(false)).update(v4).setMuted(true);
            else
                throw new RuntimeException("Can't set v4 when this is a spanned surface " + this);
            super.updateSymmetry();
        }
    }

    public static class Muted extends Quadrilateral3D {
        public Muted(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
            super((Vec3) new Vec3(v1).setMuted(true), (Vec3) new Vec3(v2).setMuted(true), (Vec3) new Vec3(v3).setMuted(true), (Vec3) new Vec3(v4).setMuted(true));
        }

        public Muted(Vec3 onPoint, Vec3 u, Vec3 v) {
            super((Vec3) new Vec3(onPoint).setMuted(true), (Vec3) new Vec3(u).setMuted(true), (Vec3) new Vec3(v).setMuted(true));
        }

        @Override
        public void updateSymmetry() {
        }
    }
}
