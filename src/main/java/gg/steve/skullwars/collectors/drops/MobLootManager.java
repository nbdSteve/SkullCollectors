package gg.steve.skullwars.collectors.drops;


import gg.steve.skullwars.collectors.managers.Files;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MobLootManager {
    private EntityType entityType;
    private List<ItemStack> drops;
    private double money;

    public MobLootManager(EntityType entityType) {
        this.entityType = entityType;
        this.drops = new ArrayList<>();
        for (String drop : Files.CONFIG.get().getStringList("loot-per-minute." + entityType.name().toLowerCase() + ".items")) {
            String[] item = drop.split(":");
            int amount = Integer.parseInt(drop.split("-")[1]);
            drops.add(new ItemStack(Material.valueOf(item[0].toUpperCase()), amount, Short.parseShort(item[1].split("-")[0])));
        }
        this.money = Files.CONFIG.get().getDouble("loot-per-minute." + entityType.name().toLowerCase() + ".money");
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public List<ItemStack> getDrops() {
        return drops;
    }

    public double getMoney() {
        return money;
    }
}