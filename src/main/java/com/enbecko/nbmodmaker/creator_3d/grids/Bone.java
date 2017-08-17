package com.enbecko.nbmodmaker.creator_3d.grids;

import com.enbecko.nbmodmaker.linalg.real.Matrix_4x4;
import com.enbecko.nbmodmaker.linalg.real.Real_Vec_n;
import com.enbecko.nbmodmaker.linalg.real.Vec3;
import com.enbecko.nbmodmaker.linalg.real.Vec4;

/**
 * Created by enbec on 15.08.2017.
 */
public class Bone {
    /**
     * Rotation is in a right-handed coordinate system with the
     * roll-axis as x, yaw as z und pitch as y.
     */
    private double yaw, pitch, roll, axisAngle;
    private Vec3 rotationAxis;
    private Matrix_4x4 rotation, translation, scale, offset, combined, inverseCombined;

    private Grid[] grids = new Grid[8];

    public Bone() {
        this.init();
    }

    public static void main(String[] args) {
        Bone b = new Bone();
    }

    public void init() {
        this.rotationAxis = new Vec3();
        this.rotation = Matrix_4x4.MATRIXFACTORY.makeIdent(0);
        this.translation = Matrix_4x4.MATRIXFACTORY.makeIdent(0);
        this.scale = Matrix_4x4.MATRIXFACTORY.makeIdent(0);
        this.offset = Matrix_4x4.MATRIXFACTORY.makeIdent(0);
        this.combined = Matrix_4x4.MATRIXFACTORY.makeIdent(0);
        this.inverseCombined = Matrix_4x4.MATRIXFACTORY.makeIdent(0);
    }

    public void move(float dX, float dY, float dZ) {
        this.translation.setAt(0, 3, this.translation.getAt(0, 3) + dX);
        this.translation.setAt(1, 3, this.translation.getAt(1, 3) + dY);
        this.translation.setAt(2, 3, this.translation.getAt(2, 3) + dZ);
    }

    public void moveTo(float x, float y, float z) {
        this.translation.setAt(0, 3, x);
        this.translation.setAt(1, 3, y);
        this.translation.setAt(2, 3, z);
    }

    public void incYawPitchRoll(double dYaw, double dPitch, double dRoll) {
        this.yaw += Math.toRadians(dYaw);
        this.pitch += Math.toRadians(dPitch);
        this.roll += Math.toRadians(dRoll);
        this.setYawPitchRoll(this.yaw, this.pitch, this.roll);
    }

    /**
     * GEGEN den Uhrzeigersinn.
     *
     * @param yaw   around z-axis
     * @param pitch around y-axis
     * @param roll  around x-axis
     */
    public void setYawPitchRoll(double yaw, double pitch, double roll) {
        this.yaw = Math.toRadians(yaw);
        this.pitch = Math.toRadians(pitch);
        this.roll = Math.toRadians(roll);
        double c1 = Math.cos(this.yaw), c2 = Math.cos(this.pitch), c3 = Math.cos(this.roll);
        double s1 = Math.sin(this.yaw), s2 = Math.sin(this.pitch), s3 = Math.sin(this.roll);
        this.rotation.setAt(0, 0, (float) (c2 * c1));
        this.rotation.setAt(0, 1, (float) (s3 * s2 * c1 - c3 * s1));
        this.rotation.setAt(0, 2, (float) (s3 * s1 + c3 * s2 * c1));

        this.rotation.setAt(1, 0, (float) (c2 * s1));
        this.rotation.setAt(1, 1, (float) (c3 * c1 + s3 * s2 * s1));
        this.rotation.setAt(1, 2, (float) (c3 * s2 * s1 - s3 * c1));

        this.rotation.setAt(2, 0, (float) -s2);
        this.rotation.setAt(2, 1, (float) (s3 * c2));
        this.rotation.setAt(2, 2, (float) (c3 * c2));
    }

    public void incRollPitchYaw(double dRoll, double dPitch, double dYaw) {
        this.yaw += Math.toRadians(dYaw);
        this.pitch += Math.toRadians(dPitch);
        this.roll += Math.toRadians(dRoll);
        this.setRollPitchYaw(this.roll, this.pitch, this.yaw);
    }

    public void setRollPitchYaw(double roll, double pitch, double yaw) {
        this.yaw = Math.toRadians(yaw);
        this.pitch = Math.toRadians(pitch);
        this.roll = Math.toRadians(roll);
        double c1 = Math.cos(this.yaw), c2 = Math.cos(this.pitch), c3 = Math.cos(this.roll);
        double s1 = Math.sin(this.yaw), s2 = Math.sin(this.pitch), s3 = Math.sin(this.roll);
        this.rotation.setAt(0, 0, (float) (c1 * c2));
        this.rotation.setAt(0, 1, (float) (-s1 * c2));
        this.rotation.setAt(0, 2, (float) (s2));

        this.rotation.setAt(1, 0, (float) (s1 * c3 + c1 * s2 * s3));
        this.rotation.setAt(1, 1, (float) (c1 * c3 - s1 * s2 * s3));
        this.rotation.setAt(1, 2, (float) (-s3 * c2));

        this.rotation.setAt(2, 0, (float) (s1 * s3 - c1 * s2 * c3));
        this.rotation.setAt(2, 1, (float) (c1 * s3 + s1 * s2 * c3));
        this.rotation.setAt(2, 2, (float) (c2 * c3));
    }

    public void incRotationAroundCurrentAxis(double dAngle) {
        this.axisAngle += dAngle;
        this.setRotationAroundAxis(this.rotationAxis, this.axisAngle);
    }

    public void setRotationAroundAxis(Real_Vec_n axis, double angle) {
        ((Real_Vec_n)this.rotationAxis.fillWithContent(axis)).normalize();
        this.axisAngle = angle;
        double theta = Math.toRadians(angle);
        double c = Math.cos(theta), s = Math.sin(theta), x = this.rotationAxis.getAt(0), y = this.rotationAxis.getAt(1), z = this.rotationAxis.getAt(2);
        this.rotation.setAt(0, 0, (float) (c + Math.pow(x, 2) * (1 - c)));
        this.rotation.setAt(0, 1, (float) (x * y * (1 - c) - z * s));
        this.rotation.setAt(0, 2, (float) (x * z * (1 - c) + y * s));

        this.rotation.setAt(1, 0, (float) (y * x * (1 - c) + z * s));
        this.rotation.setAt(1, 1, (float) (c + Math.pow(y, 2) * (1 - c)));
        this.rotation.setAt(1, 2, (float) (y * z * (1 - c) - x * s));

        this.rotation.setAt(2, 0, (float) (z * x * (1 - c) - y * s));
        this.rotation.setAt(2, 1, (float) (z * y * (1 - c) + x * s));
        this.rotation.setAt(2, 2, (float) (c + Math.pow(z, 2) * (1 - c)));
    }

    public void setRotationAroundAxis(float x, float y, float z, float angle) {
        this.rotationAxis.fillWithContent(x, y, z);
        this.axisAngle = angle;
        this.setRotationAroundAxis(this.rotationAxis, angle);
    }

    public void addRotation(byte axis, double angle) {
        double s = Math.sin(Math.toRadians(angle));
        double c = Math.cos(Math.toRadians(angle));
        switch (axis) {
            case 0:
                float r10 = this.rotation.getAt(1, 0), r11 = this.rotation.getAt(1, 1), r12 = this.rotation.getAt(1, 2);
                float r20 = this.rotation.getAt(2, 0), r21 = this.rotation.getAt(2, 1), r22 = this.rotation.getAt(2, 1);
                this.rotation.setAt(1, 0, (float) (r10 * c - r20 * s));
                this.rotation.setAt(1, 1, (float) (r11 * c - r21 * s));
                this.rotation.setAt(1, 2, (float) (r12 * c - r22 * s));

                this.rotation.setAt(2, 0, (float) (r10 * s + r20 * c));
                this.rotation.setAt(2, 1, (float) (r11 * s + r21 * c));
                this.rotation.setAt(2, 2, (float) (r12 * s + r22 * c));
                return;
            /**
             * TODO
             */
        }
    }

    public void scale(float dX, float dY, float dZ) {
        this.scale.setAt(0, 0, this.scale.getAt(0, 0) + dX);
        this.scale.setAt(1, 1, this.scale.getAt(1, 1) + dY);
        this.scale.setAt(2, 2, this.scale.getAt(2, 2) + dZ);
    }

    public void setScale(float x, float y, float z) {
        this.scale.setAt(0, 0, x);
        this.scale.setAt(1, 1, y);
        this.scale.setAt(2, 2, z);
    }

    public void offset(float dX, float dY, float dZ) {
        this.offset.setAt(0, 3, this.offset.getAt(0, 3) + dX);
        this.offset.setAt(1, 3, this.offset.getAt(1, 3) + dY);
        this.offset.setAt(2, 3, this.offset.getAt(2, 3) + dZ);

    }

    public void setOffset(float x, float y, float z) {
        this.offset.setAt(0, 3, x);
        this.offset.setAt(1, 3, y);
        this.offset.setAt(2, 3, z);
    }
}
