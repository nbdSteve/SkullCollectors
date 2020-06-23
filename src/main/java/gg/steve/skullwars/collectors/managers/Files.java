package gg.steve.skullwars.collectors.managers;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;

public enum Files {
    CONFIG("skull-collectors.yml"),
    PERMISSIONS("permissions.yml"),
    DEBUG("lang" + File.separator + "debug.yml"),
    MESSAGES("lang" + File.separator + "messages.yml"),
    //data
    DATA("data" + File.separator + "collector-data.yml");

    private final String path;

    Files(String path) {
        this.path = path;
    }

    public void load(FileManager fileManager) {
        fileManager.add(name(), this.path);
    }

    public YamlConfiguration get() {
        return FileManager.get(name());
    }

    public void save() {
        FileManager.save(name());
    }

    public static void reload() {
        FileManager.reload();
    }

    public static boolean isAllowedWorld(World world) {
        return CONFIG.get().getStringList("allowed-worlds").contains(world.getName());
    }

    public static boolean doSpawn(EntityType type) {
        return CONFIG.get().getStringList("enabled-spawn-types").contains(type.toString().toLowerCase());
    }

    public static boolean doVanillaDrops() {
        return CONFIG.get().getBoolean("do-vanilla-drops");
    }
}
