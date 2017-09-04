package com.enbecko.nbmodmaker;

import com.enbecko.nbmodmaker.linalg.real.Vec4;

import javax.annotation.Nonnull;

public class GlobalRenderSetting {
    private static RenderMode renderMode = RenderMode.LIVE;

    public static void putRenderMode(@Nonnull RenderMode mode) {
        renderMode = mode;
    }

    public static RenderMode getRenderMode() {
        return renderMode;
    }

    public enum RenderMode {
        DEBUG, LIVE;
    }

    public enum RenderOption {
        OUTLINE {
            private final Vec4 color = (Vec4) ((Vec4) new Vec4(1, 1, 1, 1).normalize().addToThis(1)).normalize();
            public Vec4 getColor() {
                return color;
            }

            public void setColor(float red, float green, float blue) {
                ((Vec4) color.update(red, green, blue, 1).normalize().addToThis(1)).normalize();
            }
        }, OVERLAY_RED {
            private final Vec4 red = (Vec4) new Vec4(1, 0, 0, 0.44F);
            public Vec4 getColor() {
                return red;
            }
        }, OVERLAY_GREEN {
            private final Vec4 green = (Vec4) new Vec4(0.3F, 1, 0.3F, 0.44F);
            public Vec4 getColor() {
                return green;
            }
        }, OVERLAY_CUSTOM {
            private final Vec4 color = (Vec4) new Vec4(1, 1, 1, 0.44F);
            public Vec4 getColor() {
                return color;
            }

            public void setColor(float red, float green, float blue) {
                ((Vec4) color.update(red, green, blue, 1).normalize().addToThis(1)).normalize();
            }
        };

        public abstract Vec4 getColor();
    }

}
