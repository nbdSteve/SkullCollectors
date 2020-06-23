package gg.steve.skullwars.collectors.drops;

import gg.steve.skullwars.collectors.managers.Files;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityDrops {
    private static Map<EntityType, MobLootManager> drops;

    public static void loadDrops() {
        drops = new HashMap<>();
        for (String entry : Files.CONFIG.get().getConfigurationSection("loot-per-minute").getKeys(false)) {
            EntityType entityType = EntityType.valueOf(entry.toUpperCase());
            drops.put(entityType, new MobLootManager(entityType));
        }
    }

    public static void shutdown() {
        if (drops != null && !drops.isEmpty()) drops.clear();
    }

    public static double getMoney(EntityType entityType) {
        if (!drops.containsKey(entityType)) return 0d;
        return drops.get(entityType).getMoney();
    }

    public static List<ItemStack> getDrops(EntityType entityType) {
        if (!drops.containsKey(entityType)) return new ArrayList<>();
        return drops.get(entityType).getDrops();
    }
}
