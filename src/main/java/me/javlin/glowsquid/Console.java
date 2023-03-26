package me.javlin.glowsquid;

import me.javlin.glowsquid.gui.GUIGlowsquid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;

public class Console {
    private static final Logger logger = LoggerFactory.getLogger("Glowsquid " + Glowsquid.VERSION);
    private static final ResourceBundle bundle = ResourceBundle.getBundle("Glowsquid", new Locale("en", "US"));

    public static void info(String key, Object... args) {
        key = get(key, args);
        logger.info(key);
        logGUI(key, "INFO");
    }

    public static void warn(String key, Object... args) {
        key = get(key, args);
        logger.warn(key);
        logGUI(key, "WARN");
    }

    public static void error(String key, Object... args) {
        key = get(key, args);
        logger.error(key);
        logGUI(key, "ERROR");
    }

    public static void debug(String text) {
        logger.info(text);
    }

    public static String get(String key, Object... args) {
        return String.format(bundle.getString(key), args);
    }

    private static void logGUI(String text, String type) {
        GUIGlowsquid.getInstance().printToConsole(String.format("[%s] [%s] %s", Thread.currentThread().getName(), type, text));
    }
}
