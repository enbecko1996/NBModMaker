package com.enbecko.nbmodmaker.linalg;

/**
 * Created by enbec on 15.08.2017.
 */
public class Scalar {
    private float realValue;
    
    public Scalar() {
    }

    public Scalar(float realValue) {
        this.realValue = realValue;
    }

    public float getRealValue() {
        return this.realValue;
    }

    public Scalar setRealValue(float val) {
        this.realValue = val;
        return this;
    }

    public Scalar add(Scalar rhs) {
        this.realValue += rhs.realValue;
        return this;
    }

    public Scalar sub(Scalar rhs) {
        this.realValue -= rhs.realValue;
        return this;
    }

    public Scalar mul(Scalar rhs) {
        this.realValue *= rhs.realValue;
        return this;
    }

    public Scalar div(Scalar rhs) {
        this.realValue /= rhs.realValue;
        return this;
    }

    public Scalar newAdd(Scalar rhs) {
        return new Scalar(this.realValue += rhs.realValue);
    }

    public Scalar newSub(Scalar rhs) {
        return new Scalar(this.realValue -= rhs.realValue);
    }

    public Scalar newMul(Scalar rhs) {
        return new Scalar(this.realValue *= rhs.realValue);
    }

    public Scalar newDiv(Scalar rhs) {
        return new Scalar(this.realValue /= rhs.realValue);
    }
}
