package gg.steve.skullwars.collectors.core;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.integration.Econ;
import gg.steve.skullwars.collectors.SkullCollectors;
import gg.steve.skullwars.collectors.drops.DropType;
import gg.steve.skullwars.collectors.gui.FCollectorGui;
import gg.steve.skullwars.collectors.integration.ShopGUIPlusIntegration;
import gg.steve.skullwars.collectors.managers.Files;
import gg.steve.skullwars.collectors.utils.LogUtil;
import kr.xguys.outpost.OutpostPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class FactionCollectorsManager {
    private String factionId;
    private int lifetime, tnt;
    private Map<UUID, Collector> collectors;
    private Map<Material, Integer> collectorContents;
    private FCollectorGui fCollectorGui;

    public FactionCollectorsManager(String factionId) {
        this.factionId = factionId;
        if (Files.DATA.get().getConfigurationSection(this.factionId) == null) {
            Files.DATA.get().createSection(this.factionId);
            Files.DATA.get().getConfigurationSection(this.factionId).set("lifetime", 0);
            Files.DATA.get().getConfigurationSection(this.factionId).set("tnt", 0);
            Files.DATA.get().createSection(this.factionId + ".contents");
            Files.DATA.save();
        }
        this.lifetime = Files.DATA.get().getConfigurationSection(this.factionId).getInt("lifetime");
        this.tnt = Files.DATA.get().getConfigurationSection(this.factionId).getInt("tnt");
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
            if (entry.equalsIgnoreCase("contents") || entry.equalsIgnoreCase("lifetime") || entry.equalsIgnoreCase("tnt"))
                continue;
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
        if (section == null) return;
        section.set("contents", null);
        section.createSection("contents");
        section.set("lifetime", this.lifetime);
        section.set("tnt", this.tnt);
        for (Material material : this.collectorContents.keySet()) {
            section.set("contents." + material.name(), this.collectorContents.get(material));
        }
        for (UUID collectorId : this.collectors.keySet()) {
            Collector collector = collectors.get(collectorId);
            saveCollector(collector);
        }
        Files.DATA.save();
        LogUtil.info("Successfully written collector data for faction: " + this.factionId + " to file.");
    }

    public void saveCollector(Collector collector) {
        if (Files.DATA.get().getConfigurationSection(this.factionId) == null) {
            Files.DATA.get().createSection(this.factionId);
            Files.DATA.get().getConfigurationSection(this.factionId).set("lifetime", 0);
            Files.DATA.get().getConfigurationSection(this.factionId).set("tnt", 0);
            Files.DATA.get().createSection(this.factionId + ".contents");
            Files.DATA.save();
        }
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
        lifetime += amount;
        if (collectorContents.containsKey(material)) {
            amount += collectorContents.get(material);
        }
        collectorContents.put(material, amount);
    }

    public void addTNT(int amount) {
        this.lifetime += amount;
        this.tnt += amount;
    }

    public void takeTNT(int amount) {
        this.tnt -= amount;
    }

    public String[] sellDrops(DropType type) {
        double deposit = 0, multiplier = 1;
        int itemsSold = 0;
        if (OutpostPlugin.getInstance().getCapturedFaction().getId().equalsIgnoreCase(this.factionId)) {
            multiplier = Files.CONFIG.get().getDouble("outpost-multiplier");
        }
        List<Material> soldItems = new ArrayList<>();
        for (Material material : collectorContents.keySet()) {
            if (DropType.isDropType(type, material)) {
                double price = ShopGUIPlusIntegration.getItemPrice(material) * this.collectorContents.get(material) * multiplier;
                deposit += price;
                itemsSold += this.collectorContents.get(material);
                Econ.deposit(this.getFaction().getAccountId(), deposit);
                soldItems.add(material);
            }
        }
        for (Material sold : soldItems) {
            this.collectorContents.remove(sold);
        }
        return (SkullCollectors.getNumberFormat().format(deposit) + ":" + SkullCollectors.getNumberFormat().format(itemsSold)).split(":");
    }

    public double depositTnt(Player player) {
//        int deposit = 0;
//        List<ItemStack> depositedStacks = new ArrayList<>();
//        for (ItemStack item : player.getInventory()) {
//            if (item != null && item.getType().equals(Material.TNT)) {
//                deposit += item.getAmount();
//                depositedStacks.add(item);
//            }
//        }
//        for (ItemStack item : depositedStacks) {
//            player.getInventory().remove(item);
//        }
//        FPlayers.getInstance().getByPlayer(player).getFaction().addTnt(deposit);
//        return deposit;
        int current = this.tnt;
        FPlayers.getInstance().getByPlayer(player).getFaction().addTnt(this.tnt);
        this.tnt = 0;
        return current;
    }

    public int getTNT() {
        return this.tnt;
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

    public Faction getFaction() {
        return Factions.getInstance().getFactionById(this.factionId);
    }

    public int getCollectorCount() {
        return this.collectors.size();
    }

    public int getCollectedItems() {
        int amount = 0;
        for (Material material : this.collectorContents.keySet()) {
            amount += this.collectorContents.get(material);
        }
        return amount;
    }

    public int getLifetime() {
        return this.lifetime;
    }
}
