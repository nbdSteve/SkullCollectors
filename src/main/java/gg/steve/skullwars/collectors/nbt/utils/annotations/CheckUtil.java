package gg.steve.skullwars.collectors.nbt.utils.annotations;

import gg.steve.skullwars.collectors.nbt.NbtApiException;
import gg.steve.skullwars.collectors.nbt.utils.MinecraftVersion;

import java.lang.reflect.Method;

public class CheckUtil {

    public static boolean isAvaliable(Method method) {
        if (MinecraftVersion.getVersion().getVersionId() < method.getAnnotation(AvaliableSince.class).version().getVersionId())
            throw new NbtApiException("The Method '" + method.getName() + "' is only avaliable for the Versions " + method.getAnnotation(AvaliableSince.class).version() + "+, but still got called!");
        return true;
    }
}