package gg.steve.skullwars.collectors.drops;

import gg.steve.skullwars.collectors.managers.Files;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public enum DropType {
    MOB("mob-drops"),
    CROP("crop-drops");

    private List<Material> drops;
    private int capacity;
    private String path;

    DropType(String path) {
        this.path = path;
        this.drops = new ArrayList<>();
        this.capacity = Files.CONFIG.get().getInt(path + ".capacity");
        for (String material : Files.CONFIG.get().getStringList(path + ".items")) {
            this.drops.add(Material.valueOf(material.toUpperCase()));
        }
    }

    public static DropType getDropType(Material material) {
        if (CROP.drops.contains(material)) return CROP;
        if (MOB.drops.contains(material)) return MOB;
        return null;
    }

    public static boolean isCropDrop(Material material) {
        return CROP.drops.contains(material);
    }

    public static boolean isMobDrop(Material material) {
        return MOB.drops.contains(material);
    }

    public static boolean isDropType(DropType type, Material material) {
        return type.drops.contains(material);
    }

    public static int getCapacity(DropType type) {
        return type.capacity;
    }

    public static boolean isCollectable(Material material) {
        return (CROP.drops.contains(material) || MOB.drops.contains(material));
    }

    public static void reload() {
        for (DropType type : DropType.values()) {
            type.drops = new ArrayList<>();
            type.capacity = Files.CONFIG.get().getInt(type.path + ".capacity");
            for (String material : Files.CONFIG.get().getStringList(type.path + ".items")) {
                type.drops.add(Material.valueOf(material.toUpperCase()));
            }
        }
    }

    public static int getMaxTNT() {
        return Files.CONFIG.get().getInt("tnt.capacity");
    }
}
