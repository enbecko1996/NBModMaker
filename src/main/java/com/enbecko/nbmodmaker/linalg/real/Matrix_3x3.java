package com.enbecko.nbmodmaker.linalg.real;

/**
 * Created by enbec on 15.08.2017.
 */
public class Matrix_3x3 extends Real_Matrix_NxN {
    public static class MATRIXFACTORY {
        public static Matrix_3x3 makeMatrixFromColumns(Vec3... columns) {
            return new Matrix_3x3(3, columns);
        }

        public static Matrix_3x3 makeMatrixFromColumns(Vec4... columns) {
            return new Matrix_3x3(3, columns);
        }

        public static Matrix_3x3 makeMatrixFromRows(Vec3... rows) {
            return new Matrix_3x3(rows, 3);
        }

        public static Matrix_3x3 makeMatrixFromRows(Vec4... rows) {
            return new Matrix_3x3(rows, 3);
        }

        public static Matrix_3x3 makeIdent(int off) {
            Matrix_3x3 out = new Matrix_3x3();
            out.toIdentWithOffset(off);
            return out;
        }
    }

    public Matrix_3x3() {
        super(3);
    }

    public Matrix_3x3(float... content) {
        super(3, content);
    }

    protected Matrix_3x3(int m, Real_Vec_n[] columns) {
        super(m, columns);
    }

    protected Matrix_3x3(Real_Vec_n[] rows, int n) {
        super(rows, n);
    }

    public Matrix_3x3(Matrix_3x3 other) {
        super(other);
    }

    public void setColumn(int n, Vec4 column) {
        if (!this.isMuted) {
            if (n < 3) {
                for (int mCC = 0; mCC < 3; mCC++) {
                    this.content[!this.isTransposed ? mCC : n][!this.isTransposed ? n : mCC] = column.content[mCC][0];
                }
            }
        } else
            throw new RuntimeException(this + "\n is muted and you can't change it");
    }

    public void setRow(int m, Vec4 row) {
        if (!this.isMuted) {
            if (m < 3) {
                for (int nCC = 0; nCC < 3; nCC++) {
                    this.content[!this.isTransposed ? m : nCC][!this.isTransposed ? nCC : m] = row.content[nCC][0];
                }
            }
        } else
            throw new RuntimeException(this + "\n is muted and you can't change it");
    }
}
