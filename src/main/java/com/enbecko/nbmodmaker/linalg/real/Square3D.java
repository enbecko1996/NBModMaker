package com.enbecko.nbmodmaker.linalg.real;

public abstract class Square3D {
    public static class AutoUpdate extends Quadrilateral3D.AutoUpdate {
        public AutoUpdate(Vec4 v1, Vec4 v2, Vec4 v3, Vec4 v4) {
            super(v1, v2, v3, v4);
            Vec4 u, v;
            if ((u = this.getU()).dot3D(true, (v = this.getV()), true) != 0)
                throw new RuntimeException("The u and v vec of a square have to be orthogonal! u = \n" + u + " v = \n" + v);
        }

        public AutoUpdate(Vec4 onPoint, Vec4 u, Vec4 v) {
            super(onPoint, u, v);
            if (u.dot3D(true, v, true) != 0)
                throw new RuntimeException("The u and v vec of a square have to be orthogonal! u = \n" + u + " v = \n" + v);
        }
    }

    public static class ManualUpdate extends Quadrilateral3D.ManualUpdate {
        public ManualUpdate(Vec4 v1, Vec4 v2, Vec4 v3, Vec4 v4) {
            super(new Vec4(v1), new Vec4(v2), new Vec4(v3), new Vec4(v4));
            Vec4 u, v;
            if ((u = this.getU()).dot3D(true, (v = this.getV()), true) != 0)
                throw new RuntimeException("The u and v vec of a square have to be orthogonal! u = \n" + u + " v = \n" + v);
        }

        public ManualUpdate(Vec4 onPoint, Vec4 u, Vec4 v) {
            super(new Vec4(onPoint), new Vec4(u), new Vec4(v));
            if (u.dot3D(true, v, true) != 0)
                throw new RuntimeException("The u and v vec of a square have to be orthogonal! u = \n" + u + " v = \n" + v);
        }
    }

    public static class Muted extends Quadrilateral3D.Muted {
        public Muted(Vec4 v1, Vec4 v2, Vec4 v3, Vec4 v4) {
            super((Vec4) new Vec4(v1).setMuted(true), (Vec4) new Vec4(v2).setMuted(true), (Vec4) new Vec4(v3).setMuted(true), (Vec4) new Vec4(v4).setMuted(true));
            Vec4 u, v;
            if ((u = this.getU()).dot3D(true, (v = this.getV()), true) != 0)
                throw new RuntimeException("The u and v vec of a square have to be orthogonal! u = \n" + u + " v = \n" + v);
        }

        public Muted(Vec4 onPoint, Vec4 u, Vec4 v) {
            super((Vec4) new Vec4(onPoint).setMuted(true), (Vec4) new Vec4(u).setMuted(true), (Vec4) new Vec4(v).setMuted(true));
            if (u.dot3D(true, v, true) != 0)
                throw new RuntimeException("The u and v vec of a square have to be orthogonal! u = \n" + u + " v = \n" + v);
        }
    }

}
