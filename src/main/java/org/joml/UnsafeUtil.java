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

import java.lang.reflect.Field;

class UnsafeUtil {
    static final Wrapper IMPL;
    static {
        Wrapper wrapper
//#ifdef __HAS_UNSAFE__
                = null;
        try {
            if (Options.NO_UNSAFE && Options.FORCE_UNSAFE) {
                throw new ConfigurationException("Cannot enable both -Djoml.nounsafe and -Djoml.forceUnsafe", null);
            }
            if (Options.NO_UNSAFE) {
                wrapper = new Safe();
            } else {
                if (Options.INTERNAL_UNSAFE) {
                    try {
                        // To a certain extent, reduce the possibility of UnsafeInternal class initializing fail
                        // but it is not necessary?
                        Class.forName("jdk.internal.misc.Unsafe");
                        wrapper = new UnsafeInternal();
                    } catch (Throwable ignored) {
                        wrapper = new Unsafe1();
                    }
                } else {
                    wrapper = new Unsafe1();
                }
            }
        } catch (Throwable e) {
            if (Options.FORCE_UNSAFE) {
                throw new ConfigurationException("Unsafe is not supported but its use was forced via -Djoml.forceUnsafe", e);
            }
        }
        if (wrapper == null)
            wrapper
//#endif
                    = new Safe();

        IMPL = wrapper;
    }
    static Field getDeclaredField(Class clazz, String fieldName) throws NoSuchFieldException {
        Class type = clazz;
        do {
            try {
                return type.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                type = type.getSuperclass();
            } catch (SecurityException e) {
                type = type.getSuperclass();
            }
        } while (type != null);
        throw new NoSuchFieldException(fieldName + " does not exist in " + clazz.getName() + " or any of its superclasses."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    // In order to achieve better performance optimization,
    // this interface should have only one instance of implementation at runtime.
    interface Wrapper {
        /**
         * Attempt to obtain the offset of the target field, or return {@code -1L} if cannot get offset.
         *
         * @return {@code -1L} if not
         */
        long objectFieldOffset(Class clazz, String fieldName);
    }
    // Just like its name suggests, it is extremely safe.
    static final class Safe implements Wrapper {
        @Override
        public long objectFieldOffset(Class clazz, String fieldName) {
            return -1L;
        }
    }

    static final class Unsafe1 implements Wrapper {
        private static final sun.misc.Unsafe U = MemUtil.MemUtilUnsafe.getUnsafeInstance();
        Unsafe1() {}

        @Override
        public long objectFieldOffset(Class clazz, String fieldName) {
            try {
                return U.objectFieldOffset(clazz.getDeclaredField(fieldName));
            } catch (NoSuchFieldException | SecurityException e) {
                return -1L;
            }
        }
    }

    static final class UnsafeInternal implements Wrapper {
        private static final jdk.internal.misc.Unsafe U = jdk.internal.misc.Unsafe.getUnsafe();
        UnsafeInternal() {}

        @Override
        public long objectFieldOffset(Class clazz, String fieldName) {
            try {
                return U.objectFieldOffset(clazz, fieldName);
            } catch (InternalError e) {
                // TODO: should we catch InternalError? it throw when firld not declared in clazz
                // Otherwise, we got Field object and then try to get offset?
                return -1L;
            }
        }
    }
}