package com.enbecko.nbmodmaker.linalg.real;

public class Line3D {
    Vec4 v1, v2;
    Vec4 u, onPoint, endpoint, tmp = new Vec4();
    float length;
    private boolean isSpannedLine = false;

    private Line3D(Vec4 v1, Vec4 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    private Line3D(Vec4 onPoint, Vec4 u, float length) {
        this.u = u;
        this.u.setW(0);
        this.onPoint = onPoint;
        this.length = length;
        this.isSpannedLine = true;
    }

    public float getLength() {
        return this.length;
    }

    public Vec4 advanceOnVecAndReturnPosition(float length) {
        return (Vec4) new Vec4(this.getU()).mulToThis(length).addToThis(this.getOnPoint());
    }

    public Vec4 getOnPoint() {
        return this.isSpannedLine ? this.onPoint : this.v1;
    }

    public Vec4 getEndPoint() {
        this.tmp.update(this.u);
        if(!this.isSpannedLine)
            return this.v2;
        else {
            this.onPoint.add(this.tmp.normalizeWithoutW(true).mulToThis(this.length), endpoint == null ? (endpoint = new Vec4()) : endpoint);
            return this.endpoint;
        }
    }

    public Vec4 getU() {
        return this.isSpannedLine ? this.u : (Vec4) this.v2.sub(this.v1, u == null ? (u = new Vec4(0, 0, 0, 0)) : u);
    }

    public Vec4 getV1() {
        return !this.isSpannedLine ? this.v1 : this.onPoint;
    }

    public Vec4 getV2() {
        return !this.isSpannedLine ? v2 : (Vec4) this.onPoint.add(this.u, v2 == null ? (v2 = new Vec4()) : v2);
    }

    public static class AutoUpdate extends Line3D {
        public AutoUpdate(Vec4 v1, Vec4 v2) {
            super(v1, v2);
        }

        public AutoUpdate(Vec4 onPoint, Vec4 u, float length) {
            super(onPoint, u, length);
        }
    }

    public static class ManualUpdate extends Line3D {
        public ManualUpdate(Vec4 v1, Vec4 v2) {
            super(new Vec4(v1), new Vec4(v2));
        }

        public ManualUpdate(Vec4 onPoint, Vec4 u, float length) {
            super(new Vec4(onPoint), new Vec4(u), length);
        }
    }

    public static class Muted extends Line3D {
        public Muted(Vec4 v1, Vec4 v2) {
            super(new Vec4(v1), new Vec4(v2));
        }

        public Muted(Vec4 onPoint, Vec4 u, float length) {
            super(new Vec4(onPoint), new Vec4(u), length);
        }
    }
}
