package gg.steve.skullwars.collectors.listener;

import gg.steve.skullwars.collectors.core.Collector;
import gg.steve.skullwars.collectors.core.CollectorManager;
import gg.steve.skullwars.collectors.drops.DropType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class CollectionListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void entityDeath(EntityDeathEvent event) {
        if (!CollectorManager.isCollectorActive(event.getEntity().getWorld().getChunkAt(event.getEntity().getLocation())))
            return;
        Collector collector = CollectorManager.getCollector(event.getEntity().getWorld().getChunkAt(event.getEntity().getLocation()));
        collector.creatureDeath(event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void playerBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        if (!CollectorManager.isCollectorActive(event.getBlock().getChunk())) return;
        Collector collector = CollectorManager.getCollector(event.getBlock().getChunk());
        for (ItemStack item : event.getBlock().getDrops()) {
            if (!DropType.isCollectable(item.getType())) continue;
            isConnectedBlockAbove(event.getBlock(), event.getBlock().getType(), collector);
            collector.cropBreak(event.getBlock());
        }
    }

    public boolean isConnectedBlockAbove(Block block, Material material, Collector collector) {
        Block above = block.getRelative(BlockFace.UP);
        if (above != null && above.getType().equals(material)) {
            isConnectedBlockAbove(above, material, collector);
            collector.cropBreak(above);
        } else {
            return false;
        }
        return true;
    }
}