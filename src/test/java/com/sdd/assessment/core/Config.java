package com.sdd.assessment.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

/** System properties override environment variables, which override checked-in defaults. */
public final class Config {
    private static final Properties DEFAULTS = load();
    private Config() { }
    private static Properties load() {
        Properties p = new Properties();
        try (InputStream in = Config.class.getResourceAsStream("/config/default.properties")) {
            if (in == null) throw new IllegalStateException("Missing config/default.properties");
            p.load(in);
            return p;
        } catch (IOException e) { throw new IllegalStateException("Cannot load defaults", e); }
    }
    public static String get(String key) {
        String value = System.getProperty(key);
        if (value == null) value = System.getenv(key.replaceAll("([a-z])([A-Z])", "$1_$2").replace('.', '_').toUpperCase(Locale.ROOT));
        if (value == null) value = DEFAULTS.getProperty(key);
        return value == null ? "" : value.trim();
    }
    public static int positiveInt(String key) {
        int value = Integer.parseInt(get(key));
        if (value < 1) throw new IllegalArgumentException(key + " must be positive");
        return value;
    }
    public static boolean bool(String key) {
        String value = get(key);
        if (!value.equals("true") && !value.equals("false")) throw new IllegalArgumentException(key + " must be true or false");
        return Boolean.parseBoolean(value);
    }
}
