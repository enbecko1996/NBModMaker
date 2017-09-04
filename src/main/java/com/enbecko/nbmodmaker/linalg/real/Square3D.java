package com.enbecko.nbmodmaker.linalg.real;

public abstract class Square3D {
    public static class AutoUpdate extends Quadrilateral3D.AutoUpdate {
        public AutoUpdate(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
            super(v1, v2, v3, v4);
            Vec3 u, v;
            if ((u = this.getU()).dot((v = this.getV())) != 0)
                throw new RuntimeException("The u and v vec of a square have to be orthogonal! u = \n" + u + " v = \n" + v);
        }

        public AutoUpdate(Vec3 onPoint, Vec3 u, Vec3 v) {
            super(onPoint, u, v);
            if (u.dot(v) != 0)
                throw new RuntimeException("The u and v vec of a square have to be orthogonal! u = \n" + u + " v = \n" + v);
        }
    }

    public static class ManualUpdate extends Quadrilateral3D.ManualUpdate {
        public ManualUpdate(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
            super(new Vec3(v1), new Vec3(v2), new Vec3(v3), new Vec3(v4));
            Vec3 u, v;
            if ((u = this.getU()).dot((v = this.getV())) != 0)
                throw new RuntimeException("The u and v vec of a square have to be orthogonal! u = \n" + u + " v = \n" + v);
        }

        public ManualUpdate(Vec3 onPoint, Vec3 u, Vec3 v) {
            super(new Vec3(onPoint), new Vec3(u), new Vec3(v));
            if (u.dot(v) != 0)
                throw new RuntimeException("The u and v vec of a square have to be orthogonal! u = \n" + u + " v = \n" + v);
        }
    }

    public static class Muted extends Quadrilateral3D.Muted {
        public Muted(Vec3 v1, Vec3 v2, Vec3 v3, Vec3 v4) {
            super((Vec3) new Vec3(v1).setMuted(true), (Vec3) new Vec3(v2).setMuted(true), (Vec3) new Vec3(v3).setMuted(true), (Vec3) new Vec3(v4).setMuted(true));
            Vec3 u, v;
            if ((u = this.getU()).dot((v = this.getV())) != 0)
                throw new RuntimeException("The u and v vec of a square have to be orthogonal! u = \n" + u + " v = \n" + v);
        }

        public Muted(Vec3 onPoint, Vec3 u, Vec3 v) {
            super((Vec3) new Vec3(onPoint).setMuted(true), (Vec3) new Vec3(u).setMuted(true), (Vec3) new Vec3(v).setMuted(true));
            if (u.dot(v) != 0)
                throw new RuntimeException("The u and v vec of a square have to be orthogonal! u = \n" + u + " v = \n" + v);
        }
    }

}
