package com.bachlinh.order.utils;

import com.bachlinh.order.annotation.Reachable;
import com.bachlinh.order.exception.system.UnsafeException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

@NoArgsConstructor(access = AccessLevel.NONE)
@Reachable(onClasses = UnsafeUtils.class, fieldNames = "theUnsafe")
public final class UnsafeUtils {
    private static final Unsafe INSTANCE;

    static {
        try {
            Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafeField.setAccessible(true);
            INSTANCE = (Unsafe) theUnsafeField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new UnsafeException("Can not obtain the unsafe", e);
        }
    }

    public static Unsafe getUnsafe() {
        return INSTANCE;
    }
}
