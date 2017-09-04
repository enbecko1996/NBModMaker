package com.enbecko.nbmodmaker.linalg.real;

public class Matrix_Intersect extends Matrix_3x3 {
    public static class MATRIXFACTORY {
        public static Matrix_Intersect makeMatrixFromColumns(Vec3... columns) {
            return new Matrix_Intersect(3, columns);
        }

        public static Matrix_Intersect makeMatrixFromColumns(Vec4... columns) {
            return new Matrix_Intersect(3, columns);
        }

        public static Matrix_Intersect makeMatrixFromRows(Vec3... rows) {
            return new Matrix_Intersect(rows, 3);
        }

        public static Matrix_Intersect makeMatrixFromRows(Vec4... rows) {
            return new Matrix_Intersect(rows, 3);
        }

        public static Matrix_Intersect makeIdent(int off) {
            Matrix_Intersect out = new Matrix_Intersect();
            out.toIdentWithOffset(off);
            return out;
        }
    }

    public Matrix_Intersect() {
        super();
    }

    public Matrix_Intersect(float... content) {
        super(content);
    }

    protected Matrix_Intersect(int m, Real_Vec_n[] columns) {
        super(m, columns);
    }

    protected Matrix_Intersect(Real_Vec_n[] rows, int n) {
        super();
    }

    public Matrix_Intersect(Matrix_3x3 other) {
        super(other);
    }

    public void setColumn(int p, Real_Vec_n vec_n) {
        if (p != 2) {
            throw new RuntimeException("You can only change column 3 on an Matrix_Intersect " + this);
        } else
            super.setColumn(p, vec_n);
    }

    public void setRow(int p, Real_Vec_n vec_n) {
        throw new RuntimeException("You can not change rows on an Matrix_Intersect " + this);
    }

    public void setAt(int m, int n, float val) {
        if (n != 2)
            throw new RuntimeException("You can only change values in column 3 on an Matrix_Intersect " + this);
        else
            super.setAt(m, n, val);
    }

    public void doLUDecomposition() {
        if (!this.isLUDecomposed()) {
            super.doLUDecomposition();
        } else {
            super.doLUDecomposition();
            /**
             * TODO
             * this should be faster because only the last column changes.
             */
        }
    }
}
