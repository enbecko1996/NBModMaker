package com.enbecko.nbmodmaker.linalg.real;

public class Line3D {
    Vec3 v1, v2;
    Vec3 u, onPoint, endpoint, tmp = new Vec3();
    float length;
    private boolean isSpannedLine = false;

    private Line3D(Vec3 v1, Vec3 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    private Line3D(Vec3 onPoint, Vec3 u, float length) {
        this.u = u;
        this.onPoint = onPoint;
        this.length = length;
        this.isSpannedLine = true;
    }

    public float getLength() {
        return this.length;
    }

    public Vec3 advanceOnVecAndReturnPosition(float length) {
        return (Vec3) new Vec3(this.getU()).mulToThis(length).addToThis(this.getOnPoint());
    }

    public Vec3 getOnPoint() {
        return this.isSpannedLine ? this.onPoint : this.v1;
    }

    public Vec3 getEndPoint() {
        this.tmp.update(this.u);
        if(!this.isSpannedLine)
            return this.v2;
        else {
            this.onPoint.add(this.tmp.normalize().mulToThis(this.length), endpoint == null ? (endpoint = new Vec3()) : endpoint);
            return this.endpoint;
        }
    }

    public Vec3 getU() {
        return this.isSpannedLine ? this.u : (Vec3) this.v2.sub(this.v1, u == null ? (u = new Vec3(0, 0, 0)) : u);
    }

    public Vec3 getV1() {
        return !this.isSpannedLine ? this.v1 : this.onPoint;
    }

    public Vec3 getV2() {
        return !this.isSpannedLine ? v2 : (Vec3) this.onPoint.add(this.u, v2 == null ? (v2 = new Vec3()) : v2);
    }

    public static class AutoUpdate extends Line3D {
        public AutoUpdate(Vec3 v1, Vec3 v2) {
            super(v1, v2);
        }

        public AutoUpdate(Vec3 onPoint, Vec3 u, float length) {
            super(onPoint, u, length);
        }
    }

    public static class ManualUpdate extends Line3D {
        public ManualUpdate(Vec3 v1, Vec3 v2) {
            super(new Vec3(v1), new Vec3(v2));
        }

        public ManualUpdate(Vec3 onPoint, Vec3 u, float length) {
            super(new Vec3(onPoint), new Vec3(u), length);
        }
    }

    public static class Muted extends Line3D {
        public Muted(Vec3 v1, Vec3 v2) {
            super(new Vec3(v1), new Vec3(v2));
        }

        public Muted(Vec3 onPoint, Vec3 u, float length) {
            super(new Vec3(onPoint), new Vec3(u), length);
        }
    }
}
