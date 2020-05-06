package gg.steve.skullwars.collectors.managers;

import org.bukkit.configuration.file.YamlConfiguration;

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
}
