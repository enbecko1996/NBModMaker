package com.enbecko.nbmodmaker.linalg.real;

import javax.annotation.Nullable;

/**
 * Created by enbec on 15.08.2017.
 */
public class Vec3 extends Real_Vec_n {
    public Vec3() {
        super(0, 0, 0);
    }

    public Vec3(float x, float y, float z) {
        super(x, y, z);
    }

    public Vec3 update(float x, float y, float z) {
        this.content[0][0] = x;
        this.content[1][0] = y;
        this.content[2][0] = z;
        return this;
    }

    public Vec3 update(Vec3 other) {
        for (int k = 0; k < 3; k++) {
            this.content[k][0] = other.content[k][0];
        }
        return this;
    }

    public Vec3 cross(Vec3 rhs, @Nullable Vec3 fill) {
        if (this.size == rhs.size) {
            Vec3 out = fill == null ? new Vec3() : fill;
            out.content[0][0] = this.content[1][0] * rhs.content[2][0] - this.content[2][0] * rhs.content[1][0];
            out.content[1][0] = this.content[2][0] * rhs.content[0][0] - this.content[0][0] * rhs.content[2][0];
            out.content[2][0] = this.content[0][0] * rhs.content[1][0] - this.content[1][0] * rhs.content[0][0];
            return out;
        } else
            throw new RuntimeException("You can't dot \n" + this + " and \n" + rhs);
    }

    public float dot(Vec3 rhs) {
        if (this.size == rhs.size) {
            float val = 0;
            for (int k = 0; k < 3; k++)
                val += this.content[k][0] * rhs.content[k][0];
            return val;
        } else
            throw new RuntimeException("You can't dot \n" + this + " and \n" + rhs);
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
}
