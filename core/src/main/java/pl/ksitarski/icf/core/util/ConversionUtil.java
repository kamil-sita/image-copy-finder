package pl.ksitarski.icf.core.util;

import java.nio.ByteBuffer;

public class ConversionUtil {

    public static final int INT_TO_ARRAY_LENGTH = 4;
    public static final int DOUBLE_TO_ARRAY_LENGTH = 8;
    public static final int FLOAT_TO_ARRAY_LENGTH = 4;

    public static int byteArrayToInt(byte[] array, int start) {
        return ((array[start + 0] & 0xFF) << 24) |
               ((array[start + 1] & 0xFF) << 16) |
               ((array[start + 2] & 0xFF) << 8) |
               ((array[start + 3] & 0xFF));
    }
    
    public static void intToByteArray(byte[] array, int start, int value) {
        array[start + 0] = (byte) ((value >> 24) & 0xFF);
        array[start + 1] = (byte) ((value >> 16) & 0xFF);
        array[start + 2] = (byte) ((value >> 8) & 0xFF);
        array[start + 3] = (byte) (value & 0xFF);
    }

    public static byte[] convertToByteArray(int[] array) {
        byte[] outArray = new byte[array.length * INT_TO_ARRAY_LENGTH];

        for (int i = 0 ; i < array.length; i++) {
            intToByteArray(outArray, i * INT_TO_ARRAY_LENGTH, array[i]);
        }

        return outArray;
    }

    public static int[] convertToIntArray(byte[] array) {
        if (array.length % INT_TO_ARRAY_LENGTH != 0) {
            throw new IllegalArgumentException("Byte array length must be divisible by " + INT_TO_ARRAY_LENGTH + " for conversion");
        }

        int[] outArray = new int[array.length / INT_TO_ARRAY_LENGTH];

        for (int i = 0; i < array.length; i += INT_TO_ARRAY_LENGTH) {
            int el = byteArrayToInt(array, i);
            outArray[i / INT_TO_ARRAY_LENGTH] = el;
        }
        return outArray;
    }

    public static byte[] toByteArray(double value) {
        byte[] out = new byte[DOUBLE_TO_ARRAY_LENGTH];
        ByteBuffer.wrap(out).putDouble(value);
        return out;
    }

    public static double toDouble(byte[] bytes) {
        if (bytes.length != DOUBLE_TO_ARRAY_LENGTH) {
            throw new IllegalArgumentException("Length of array must be " + DOUBLE_TO_ARRAY_LENGTH);
        }
        return ByteBuffer.wrap(bytes).getDouble();
    }

    public static byte[] toByteArray(float value) {
        byte[] out = new byte[FLOAT_TO_ARRAY_LENGTH];
        ByteBuffer.wrap(out).putFloat(value);
        return out;
    }

    public static byte[] toByteArray(float[] value) {
        byte[] out = new byte[FLOAT_TO_ARRAY_LENGTH * value.length];

        ByteBuffer bb = ByteBuffer.wrap(out);

        for (int i = 0, valueLength = value.length; i < valueLength; i++) {
            float v = value[i];
            bb.putFloat(i, v);
        }
        return out;
    }

    public static float[] toFloatArray(byte[] bytes) {
        if (bytes.length % FLOAT_TO_ARRAY_LENGTH != 0) {
            throw new IllegalArgumentException("Byte array length must be divisible by " + FLOAT_TO_ARRAY_LENGTH + " for conversion");
        }
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        float[] out = new float[bytes.length / FLOAT_TO_ARRAY_LENGTH];
        for (int i = 0; i < out.length; i++) {
            out[i] = bb.getFloat(i);
        }
        return out;
    }

    public static float toFloat(byte[] bytes) {
        if (bytes.length != FLOAT_TO_ARRAY_LENGTH) {
            throw new IllegalArgumentException("Length of array must be " + FLOAT_TO_ARRAY_LENGTH);
        }
        return ByteBuffer.wrap(bytes).getFloat();
    }

    public static byte[] toByteArray(int value) {
        byte[] out = new byte[INT_TO_ARRAY_LENGTH];
        ByteBuffer.wrap(out).putInt(value);
        return out;
    }

    public static int toInt(byte[] bytes) {
        if (bytes.length != INT_TO_ARRAY_LENGTH) {
            throw new IllegalArgumentException("Length of array must be " + INT_TO_ARRAY_LENGTH);
        }
        return ByteBuffer.wrap(bytes).getInt();
    }
}
