package com.example.agroventa.config;

public final class RuntimeConfig {

    private static final String BUILD_CONFIG_CLASS = "com.example.agroventa.BuildConfig";
    private static final String DEFAULT_BACKEND_BASE_URL = "https://api.agroventa.local/";
    private static final boolean DEFAULT_USE_MOCK_BACKEND = true;

    private RuntimeConfig() {
    }

    public static String getBackendBaseUrl() {
        String value = readStringField("BACKEND_BASE_URL", DEFAULT_BACKEND_BASE_URL);
        // Retrofit requiere una URL base con slash final.
        return value.endsWith("/") ? value : value + "/";
    }

    public static boolean useMockBackend() {
        return readBooleanField("USE_MOCK_BACKEND", DEFAULT_USE_MOCK_BACKEND);
    }

    private static String readStringField(String fieldName, String fallback) {
        try {
            Class<?> clazz = Class.forName(BUILD_CONFIG_CLASS);
            Object value = clazz.getField(fieldName).get(null);
            return value instanceof String ? (String) value : fallback;
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    private static boolean readBooleanField(String fieldName, boolean fallback) {
        try {
            Class<?> clazz = Class.forName(BUILD_CONFIG_CLASS);
            Object value = clazz.getField(fieldName).get(null);
            return value instanceof Boolean ? (Boolean) value : fallback;
        } catch (Throwable ignored) {
            return fallback;
        }
    }
}

