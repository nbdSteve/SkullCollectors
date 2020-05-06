package gg.steve.skullwars.collectors.utils;

import gg.steve.skullwars.collectors.SkullCollectors;

public class LogUtil {

    public static void info(String message) {
        SkullCollectors.get().getLogger().info(message);
    }

    public static void warning(String message) {
        SkullCollectors.get().getLogger().warning(message);
    }
}
