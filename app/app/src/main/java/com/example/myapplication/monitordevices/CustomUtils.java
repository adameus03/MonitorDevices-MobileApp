package com.example.myapplication.monitordevices;

import java.util.Objects;

public class CustomUtils {

    // Prevents us from upgrading from Java 8
    public static <T> T requireNonNullElse(T obj, T defaultObj) {
        return (obj != null) ? obj : Objects.requireNonNull(defaultObj, "defaultObj");
    }
}
