package com.enbecko.nbmodmaker.linalg.real;

import javax.annotation.Nullable;

/**
 * Created by enbec on 15.08.2017.
 */
public class Vec4 extends Real_Vec_n {
    public Vec4() {
        super(0, 0, 0, 1);
    }

    public Vec4(float x, float y, float z, float w) {
        super(x, y, z, w);
    }

    public Vec4(float x, float y, float z) {
        this(x, y, z, 1);
    }

    public Vec4 cross(boolean normLhs, Vec4 rhs, boolean normRhs, @Nullable Vec4 fill) {
        if (this.size == rhs.size) {
            Vec4 out = fill == null ? new Vec4() : fill;
            float normOfLhs = normLhs ? this.normalizeToW() : 1;
            float normOfRhs = normRhs ? rhs.normalizeToW() : 1;
            out.content[0][0] = this.content[1][0] * rhs.content[2][0] - this.content[2][0] * rhs.content[1][0];
            out.content[1][0] = this.content[2][0] * rhs.content[0][0] - this.content[0][0] * rhs.content[2][0];
            out.content[2][0] = this.content[0][0] * rhs.content[1][0] - this.content[1][0] * rhs.content[0][0];
            out.content[3][0] = 1;
            if (normOfLhs != 1)
                this.multiply(normOfLhs, null);
            if (normOfRhs != 1)
                rhs.multiply(normOfRhs, null);
            return out;
        } else
            throw new RuntimeException("You can't dot \n" + this + " and \n" + rhs);
    }

    public float dot(boolean normLhs, Vec4 rhs, boolean normRhs) {
        if (this.size == rhs.size) {
            float normOfLhs = normLhs ? this.normalizeToW() : 1;
            float normOfRhs = normRhs ? rhs.normalizeToW() : 1;
            float val = 0;
            for (int k = 0; k < 3; k++)
                val += this.content[k][0] * rhs.content[k][0];
            if (normOfLhs != 1)
                this.multiply(normOfLhs, null);
            if (normOfRhs != 1)
                rhs.multiply(normOfRhs, null);
            return val;
        } else
            throw new RuntimeException("You can't dot \n" + this + " and \n" + rhs);
    }

    public float normalizeToW() {
        for (int k = 0; k < 3; k++) {
            this.content[k][0] /= this.content[3][0];
        }
        float out = this.content[3][0];
        this.content[3][0] = 1;
        return out;
    }

    public float lengthOfVec3(boolean normToW) {
        float normOfLhs = normToW ? this.normalizeToW() : 1;
        this.content[3][0] = 0;
        float out = this.length();
        if (normOfLhs != 1)
            this.multiply(normOfLhs, null);
        return out;
    }

    public float getX() {
        return this.content[0][0];
    }

    public void setX(float val) {
        this.content[0][0] = val;
    }

    public float getY() {
        return this.content[1][0];
    }

    public void setY(float val) {
        this.content[1][0] = val;
    }

    public float getZ() {
        return this.content[2][0];
    }

    public void setZ(float val) {
        this.content[2][0] = val;
    }

    public float getW() {
        return this.content[3][0];
    }

    public void setW(float val) {
        this.content[3][0] = val;
    }

    public float getR() {
        return this.content[0][0];
    }

    public void setR(float val) {
        this.content[0][0] = val;
    }

    public float getG() {
        return this.content[1][0];
    }

    public void setG(float val) {
        this.content[1][0] = val;
    }

    public float getB() {
        return this.content[2][0];
    }

    public void setB(float val) {
        this.content[2][0] = val;
    }

    public float getA() {
        return this.content[3][0];
    }

    public void setA(float val) {
        this.content[3][0] = val;
    }
}
