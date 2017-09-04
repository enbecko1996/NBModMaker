package com.enbecko.nbmodmaker.linalg.real;

/**
 * Created by enbec on 15.08.2017.
 */
public class Matrix_4x4 extends Real_Matrix_NxN {
    public static class MATRIXFACTORY {
        public static Matrix_4x4 makeMatrixFromColumns(Vec4[] columns) {
            return new Matrix_4x4(4, columns);
        }

        public static Matrix_4x4 makeMatrixFromRows(Vec4[] rows) {
            return new Matrix_4x4(rows, 4);
        }

        public static Matrix_4x4 makeIdent(int off) {
            Matrix_4x4 out = new Matrix_4x4();
            out.toIdentWithOffset(off);
            return out;
        }
    }

    public Matrix_4x4() {
        super(4);
    }

    public Matrix_4x4(float... content) {
        super(4, content);
    }

    protected Matrix_4x4(int m, Vec4[] columns) {
        super(m, columns);
    }

    protected Matrix_4x4(Vec4[] rows, int n) {
        super(rows, n);
    }

    public Matrix_4x4(Matrix_4x4 other) {
        super(other);
    }
}
