package com.enbecko.nbmodmaker.linalg.real;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Created by enbec on 15.08.2017.
 */
public class Real_Matrix_NxN extends Real_Matrix_MxN {
    private final int length;
    private Real_Matrix_NxN LU_lower, LU_upper, LU_permutation, ident_Stub;
    private Real_Vec_n x, y, Pb;

    public Real_Matrix_NxN(int length) {
        super(length, length);
        this.length = length;
    }

    public Real_Matrix_NxN(int length, float... content) {
        super(length, length, content);
        this.length = length;
    }

    public Real_Matrix_NxN(int m, Real_Vec_n[] columns) {
        super(m, columns);
        this.length = m;
        if (columns.length != columns[0].getSize())
            throw new RuntimeException("Can't make NxN matrix with those columns: \n" + Arrays.toString(columns));
    }

    public Real_Matrix_NxN(Real_Vec_n[] rows, int n) {
        super(rows, n);
        this.length = n;
        if (rows.length != rows[0].getSize())
            throw new RuntimeException("Can't make NxN matrix with those rows: \n" + Arrays.toString(rows));
    }

    public Real_Matrix_NxN(Real_Matrix_NxN other) {
        super(other);
        this.length = other.length;
    }

    public static void main(String[] args) {
        Real_Matrix_NxN lhs = new Real_Matrix_NxN(3,
                1, 2, 3,
                4, 5, 6,
                7, 8, 10);
        Real_Vec_n vec = new Real_Vec_n(1, 2, 3);
        lhs.doLUDecomposition();
        System.out.println(lhs.solveLGS_fromLU(vec, null));
    }

    public void doLUDecomposition() {
        if (LU_lower == null) {
            LU_upper = NxN_FACTORY.makeMatrixFromMatrix(this);
            LU_lower = NxN_FACTORY.makeIdent(this.length);
            LU_permutation = NxN_FACTORY.makeIdent(this.length);
            ident_Stub = NxN_FACTORY.makeIdent(this.length);
        } else {
            this.LU_upper.fillWithContent(this);
            this.LU_lower.fillWithContent(ident_Stub);
            this.LU_permutation.fillWithContent(ident_Stub);
        }
        int length = this.m;
        for (int k = 0; k < length; k++) {
            //PIVOTING
            boolean foundPivot = false;
            double biggest = 0;
            int swapRow = k;
            for (int l = 0; k + l < length; l++) {
                //take biggest Pivot
                if (LU_upper.content[k][k + l] != 0 && Math.abs(LU_upper.content[k][k + l]) > biggest) {
                    biggest = Math.abs(LU_upper.content[k][k + l]);
                    swapRow = k + l;
                    foundPivot = true;
                }
            }
            if (foundPivot && swapRow != k) {
                LU_upper.swapRows(k, swapRow);
                LU_permutation.swapRows(k, swapRow);
                for (int lk = 0; lk < k; lk++) {
                    float tmp = LU_lower.content[k][lk];
                    LU_lower.content[k][lk] = LU_lower.content[swapRow][lk];
                    LU_lower.content[swapRow][lk] = tmp;
                }
            }
            System.out.println(LU_upper);
            //Eliminating
            for (int p = k + 1; p < length; p++) {
                if (LU_upper.content[p][k] != 0) {
                    double fac = LU_upper.content[k][k] != 0 ? -(LU_upper.content[p][k] / LU_upper.content[k][k]) : 0;
                    for (int l = 0; l < this.m; l++)
                        LU_upper.content[p][l] += (float) (LU_upper.content[k][l] * fac);
                    LU_lower.setAt(p, k, (float) -fac);
                }
            }
        }
    }

    public Real_Matrix_NxN invert(@Nullable Real_Matrix_NxN fill) {
        if (this.LU_permutation != null && this.LU_lower != null && this.LU_upper != null) {
            Real_Matrix_NxN out = (fill == null || (fill.length != this.length)) ? this : fill;
            Real_Vec_n tmpVec = new Real_Vec_n(this.length);
            for (int k = 0; k < this.length; k++) {
                tmpVec.toIdentWithOffset(k);
                out.setColumn(k, this.solveLGS_fromLU(tmpVec, tmpVec));
            }
            return out;
        } else
            throw new RuntimeException("Can't invert when LU decomp. wasn't done \n" + this);
    }

    public Real_Matrix_NxN newInvert(@Nullable Real_Matrix_NxN fill) {
        return this.invert(new Real_Matrix_NxN(this.length));
    }

    public Real_Vec_n solveLGS_fromLU(Real_Vec_n rhs, @Nullable Real_Vec_n fill) {
        if (this.LU_permutation != null && this.LU_lower != null && this.LU_upper != null) {
            //solve Ly = Pb
            if (this.y == null || this.Pb == null) {
                y = new Real_Vec_n(this.length);
                Pb = new Real_Vec_n(this.length);
            }
            Real_Vec_n out = (fill == null || fill.size != this.length) ? new Real_Vec_n(this.length) : fill;
            this.LU_permutation.multiply(rhs, Pb);
            for (int k = 0; k < y.size; k++) {
                double sumOfLower = 0;
                for (int i = 0; i <= k - 1; i++)
                    sumOfLower += y.content[i][0] * this.LU_lower.getAt(k, i);
                y.content[k][0] = (float) ((Pb.content[k][0] - sumOfLower) / this.LU_lower.getAt(k, k));
            }

            //solving Rx = y
            for (int k = 0; k < out.size; k++) {
                double sumOfLower = 0;
                for (int i = 0; i <= k - 1; i++)
                    sumOfLower += out.content[out.size - 1 - i][0] * this.LU_upper.getAt(out.size - 1 - k, out.size - 1 - i);
                out.content[out.size - 1 - k][0] = (float) ((y.content[out.size - 1 - k][0] - sumOfLower) / this.LU_upper.getAt(out.size - 1 - k, out.size - 1 - k));
            }
            return out;
        } else {
            throw new RuntimeException("Can't solve LGS when LU decomp. wasn't done \n" + this);
        }
    }

    public static class NxN_FACTORY {
        public static Real_Matrix_NxN makeMatrixFromMatrix(Real_Matrix_NxN matrix) {
            return new Real_Matrix_NxN(matrix);
        }

        public static Real_Matrix_NxN makeIdent(int length) {
            Real_Matrix_NxN out = new Real_Matrix_NxN(length);
            for (int k = 0; k < length; k++)
                out.content[k][k] = 1;
            return out;
        }
    }
}
