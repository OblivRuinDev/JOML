/*
 * The MIT License
 *
 * Copyright (c) 2026 OblivRuinDev, Kai Burjack
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.joml;

import java.nio.Buffer;

//#ifdef __HAS_UNSAFE__
/**
 * Under normal conditions, this class will initialization successfully.
 * <p>
 * The {@code public static final} fields in this class are object field offset,
 * they may be -1 or an effective object field offset.
 *
 * @author OblivRuinDev
 */
public class MemUtil$$Field {
//#ifdef __HAS_NIO__
    public static final long ADDRESS;
//#endif
    public static final long Matrix2f_m00;
    public static final long Matrix3f_m00;
    public static final long Matrix3d_m00;
    public static final long Matrix4f_m00;
    public static final long Matrix4d_m00;
    public static final long Matrix4x3f_m00;
    public static final long Matrix3x2f_m00;
    public static final long Vector4f_x;
    public static final long Vector4i_x;
    public static final long Vector3f_x;
    public static final long Vector3i_x;
    public static final long Vector2f_x;
    public static final long Vector2i_x;
    static {
//#ifdef __HAS_NIO__
        ADDRESS = offset(Buffer.class, "address");
//#endif
        Matrix4f_m00 = checkMatrix(Matrix4f.class, Float.BYTES, 3, 3);
        Matrix4d_m00 = checkMatrix(Matrix4d.class, Double.BYTES, 3, 3);
        Matrix4x3f_m00 = checkMatrix(Matrix4x3f.class, Float.BYTES, 3, 2);
        Matrix3f_m00 = checkMatrix(Matrix3f.class, Float.BYTES, 2, 2);
        Matrix3d_m00 = checkMatrix(Matrix3d.class, Double.BYTES, 2, 2);
        Matrix3x2f_m00 = checkMatrix(Matrix3x2f.class, Float.BYTES, 1, 2);
        Matrix2f_m00 = checkMatrix(Matrix2f.class, Float.BYTES, 1, 1);
        Vector4f_x = checkVector(Vector4f.class, Float.BYTES, 4);
        Vector4i_x = checkVector(Vector4i.class, Integer.BYTES, 4);
        Vector3f_x = checkVector(Vector3f.class, Float.BYTES, 3);
        Vector3i_x = checkVector(Vector3i.class, Integer.BYTES, 3);
        Vector2f_x = checkVector(Vector2f.class, Float.BYTES, 2);
        Vector2i_x = checkVector(Vector2i.class, Integer.BYTES, 2);
    }

    /**
     * Doesn't throw any exception
     * @return {@code -1L} means field not exist
     */
    static long offset(Class<?> clazz, String fieldName) {
        try {
            if (MemUtil.U1) {
                return MemUtil$$U1.UNSAFE.objectFieldOffset(getDeclaredField(clazz, fieldName));
            } else if (MemUtil.U2) {
                return MemUtil$$U2.UNSAFE.objectFieldOffset(clazz, fieldName);
            }
        } catch (Exception | InternalError ignored) {
        }
        return -1L;
    }
    static java.lang.reflect.Field getDeclaredField(Class<?> root, String fieldName) throws NoSuchFieldException {
        Class<?> type = root;
        do {
            try {
                return type.getDeclaredField(fieldName);
            } catch (NoSuchFieldException | SecurityException e) {
                type = type.getSuperclass();
            }
        } while (type != null);
        throw new NoSuchFieldException(fieldName + " does not exist in " + root.getName() + " or any of its superclasses."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * <pre>
     * 00  01  ..  0h
     * 10  11  ..  1h
     * ..  ..  ..  ..
     * w0  w1  ..  wh
     * </pre>
     */
    private static long checkMatrix(Class<?> clazz, int fieldBytes, int c, int r) {
        long base = offset(clazz, "m00");
        for (int c0 = 0; c0 <= c; ++c0) {
            for (int r0 = 0; r0 <= r; ++r0) {
                if (offset(clazz, "m" + c0 + r0) != base + (c0 * r + r0) * fieldBytes)
                    return -1L;
            }
        }
        return base;
    }

    private static long checkVector(Class<?> clazz, int fieldBytes, int dim) {
        long base = offset(clazz, "x");
        // Validate expected field offsets
        switch (dim) {
            case 4:
                if (offset(clazz, "w") != base + 3 * fieldBytes)
                    break;
            case 3:
                if (offset(clazz, "z") != base + 2 * fieldBytes)
                    break;
            case 2:
                if (offset(clazz, "y") != base + fieldBytes)
                    break;
                else
                    return base;
            default:
                throw new IllegalArgumentException("Invalid dim: " + dim);
        }
        return -1L;
    }
}
//#endif
