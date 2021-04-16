package pl.ksitarski.icf.core.prototype.util;

import java.awt.*;

/**
 * Class that helps use integer as a pixel color in ARGB model, provided by BufferedImage.
 */
public final class IntArgb {

    public static final int A = 0;
    public static final int R = 1;
    public static final int G = 2;
    public static final int B = 3;
    private static final int ARR_SIZE = 4;

    public static int[] asArray(int argb) {
        int[] array = new int[ARR_SIZE];
        asArray(argb, array);
        return array;
    }

    public static void asArray(int argb, int[] array) {
        array[A] = (argb >> 24) & 0xFF;
        array[R] = (argb >> 16) & 0xFF;
        array[G] = (argb >> 8) & 0xFF;
        array[B] = argb & 0xFF;
    }

    public static int toRgbaInteger(int r, int g, int b, int a) {
        int rgba = a;
        rgba = (rgba << 8) + r;
        rgba = (rgba << 8) + g;
        rgba = (rgba << 8) + b;
        return rgba;
    }

    public static int toRgbaInteger(int[] argbArray) {
        return toRgbaInteger(argbArray[R], argbArray[G], argbArray[B], argbArray[A]);
    }

    public static int[] diff(int[] argb0, int[] argb1) {
        int[] out = new int[4];
        out[0] = argb0[0] - argb1[0];
        out[1] = argb0[1] - argb1[1];
        out[2] = argb0[2] - argb1[2];
        out[3] = argb0[3] - argb1[3];
        return out;
    }

    public static int[] pack(int a, int r, int g, int b) {
        int[] out = new int[4];
        out[A] = a;
        out[R] = r;
        out[G] = g;
        out[B] = b;
        return out;
    }

    public static double compare(int[] rgba0, int[] rgba1) {
        double totalSimilarity = 255.0 * 3.0;

        totalSimilarity -= Math.abs(rgba0[R] - rgba1[R]);
        totalSimilarity -= Math.abs(rgba0[G] - rgba1[G]);
        totalSimilarity -= Math.abs(rgba0[B] - rgba1[B]);

        return totalSimilarity/(255.0*3.0);
    }

    public static float[] convertRgbToHsb(int[] argb) {
        return Color.RGBtoHSB(argb[R], argb[G], argb[B], null);
    }

    public static int convertHsbToRgb(float[] hsb) {
        return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
    }
}
