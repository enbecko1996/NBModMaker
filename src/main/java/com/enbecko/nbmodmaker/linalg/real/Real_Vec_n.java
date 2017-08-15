package com.enbecko.nbmodmaker.linalg.real;

import com.enbecko.nbmodmaker.linalg.Scalar;

import javax.annotation.Nullable;

/**
 * Created by enbec on 12.07.2017.
 */
public class Real_Vec_n extends Real_Matrix_MxN {
    public Real_Vec_n(int m) {
        super(m, 1);
    }

    public Real_Vec_n(float... content) {
        super(content.length, 1, content);
    }

    public float getAt(int mC) {
        return this.getAt(mC, 0);
    }

    public void setAt(int mC, float content) {
        super.setAt(mC, 0, content);
    }

    public float dot(Real_Vec_n rhs) {
        if (this.size == rhs.size) {
            float val = 0;
            for (int k = 0; k < this.size; k++)
                val += this.content[k][0] * rhs.content[k][0];
            return val;
        } else
            throw new RuntimeException("You can't dot \n" + this + " and \n" + rhs);
    }

    public float length() {
        float val = 0;
        for (int k = 0; k < this.size; k++)
            val += Math.pow(this.content[k][0], 2);
        return (float) Math.sqrt(val);
    }

    public Real_Vec_n normalize() {
        float norm = this.length();
        for (int k = 0; k < this.size; k++) {
            this.content[k][0] /= norm;
        }
        return this;
    }
}
