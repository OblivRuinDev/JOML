package org.joml;

import java.nio.Buffer;
import java.nio.DoubleBuffer;

import static org.joml.MemUtil.U1;
import static org.joml.MemUtil.U2;

class Unsafe {
    public static long bufferAddress(Buffer buffer) {
        if (U2) {
            return MemUtil$$U2.UNSAFE.getLong(buffer, MemUtil$$Field.ADDRESS);
        } else if (U1) {
            return MemUtil$$U1.UNSAFE.getLong(buffer, MemUtil$$Field.ADDRESS);
        }
        throw new IllegalStateException("Unsafe not available");
    }
    public static boolean get(Matrix2d m, int offset, DoubleBuffer src) {}
}
