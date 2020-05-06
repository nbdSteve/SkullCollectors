package gg.steve.skullwars.collectors.core;

import gg.steve.skullwars.collectors.gui.FCollectorGui;
import gg.steve.skullwars.collectors.managers.Files;
import gg.steve.skullwars.collectors.utils.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FactionCollectorsManager {
    private String factionId;
    private Map<UUID, Collector> collectors;
    private Map<Material, Integer> collectorContents;
    private FCollectorGui fCollectorGui;

    public FactionCollectorsManager(String factionId) {
        this.factionId = factionId;
        if (Files.DATA.get().getConfigurationSection(this.factionId) == null) {
            Files.DATA.get().createSection(this.factionId);
            Files.DATA.get().createSection(this.factionId + ".contents");
            Files.DATA.save();
        }
        loadCollectorContents(Files.DATA.get().getConfigurationSection(this.factionId + ".contents"));
        loadCollectors(Files.DATA.get().getConfigurationSection(this.factionId));
    }

    public void loadCollectorContents(ConfigurationSection section) {
        this.collectorContents = new HashMap<>();
        for (String material : section.getKeys(false)) {
            collectorContents.put(Material.getMaterial(material.toUpperCase()), section.getInt(material));
        }
    }

    public void loadCollectors(ConfigurationSection section) {
        this.collectors = new HashMap<>();
        for (String entry : section.getKeys(false)) {
            if (entry.equalsIgnoreCase("contents")) continue;
            int x = section.getInt(entry + ".x");
            int y = section.getInt(entry + ".y");
            int z = section.getInt(entry + ".z");
            World world = Bukkit.getWorld(section.getString(entry + ".world"));
            this.collectors.put(UUID.fromString(entry),
                    new Collector(UUID.fromString(entry), world, new Location(world, x, y, z), this));
        }
    }

    public void saveCollectorData() {
        ConfigurationSection section = Files.DATA.get().getConfigurationSection(this.factionId);
        for (Material material : collectorContents.keySet()) {
            section.set("contents." + material.name(), collectorContents.get(material));
        }
        for (UUID collectorId : collectors.keySet()) {
            Collector collector = collectors.get(collectorId);
            section.set(collectorId + ".world", collector.getWorld().getName());
            section.set(collectorId + ".x", collector.getCollectorLocation().getBlockX());
            section.set(collectorId + ".y", collector.getCollectorLocation().getBlockY());
            section.set(collectorId + ".z", collector.getCollectorLocation().getBlockZ());
        }
        Files.DATA.save();
        LogUtil.info("Successfully written collector data for faction: " + this.factionId + " to file.");
    }

    public void saveCollector(Collector collector) {
        ConfigurationSection section = Files.DATA.get().getConfigurationSection(this.factionId);
        section.set(collector.getCollectorId() + ".world", collector.getWorld().getName());
        section.set(collector.getCollectorId() + ".x", collector.getCollectorLocation().getBlockX());
        section.set(collector.getCollectorId() + ".y", collector.getCollectorLocation().getBlockY());
        section.set(collector.getCollectorId() + ".z", collector.getCollectorLocation().getBlockZ());
        Files.DATA.save();
    }

    public void addCollector(Collector collector) {
        saveCollector(collector);
        this.collectors.put(collector.getCollectorId(), collector);
    }

    public void addDrop(Material material, int amount) {
        if (collectorContents.containsKey(material)) {
            amount += collectorContents.get(material);
        }
        collectorContents.put(material, amount);
    }

    public void sellDrops(DropType type) {
        for (Material material : collectorContents.keySet()) {
            if (DropType.isDropType(type, material)) {
                collectorContents.remove(material);
            }
        }
    }

    public int getDropAmount(DropType type) {
        int amount = 0;
        for (Material material : collectorContents.keySet()) {
            if (DropType.isDropType(type, material)) {
                amount += collectorContents.get(material);
            }
        }
        return amount;
    }

    public void openFCollectorGui(Player player) {
        if (this.fCollectorGui == null) {
            this.fCollectorGui = new FCollectorGui(Files.CONFIG.get().getConfigurationSection("gui"), this);
        } else {
            this.fCollectorGui.refresh();
        }
        this.fCollectorGui.open(player);
    }

    public void removeCollector(Collector collector) {
        Files.DATA.get().set(factionId + "." + collector.getCollectorId(), null);
        Files.DATA.save();
        this.collectors.remove(collector.getCollectorId());
    }

    public String getFactionId() {
        return factionId;
    }

    public Map<UUID, Collector> getCollectors() {
        return collectors;
    }

    public Map<Material, Integer> getCollectorContents() {
        return collectorContents;
    }
}
