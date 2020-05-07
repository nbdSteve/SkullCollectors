package gg.steve.skullwars.collectors.growth;

import gg.steve.skullwars.collectors.core.CollectorManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;

public class ChunkPreCollectorManager implements Listener {

    @EventHandler
    public void cropGrow(BlockGrowEvent event) {
        if (event.isCancelled()) return;
        if (!CollectorManager.isCollectorActive(event.getBlock().getChunk())) {
            if (event.getBlock().getType().toString().equalsIgnoreCase("sugar_cane_block")) {
                return;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void mobSpawn(SpawnerSpawnEvent event) {
        if (event.isCancelled()) return;
        if (!CollectorManager.isCollectorActive(event.getSpawner().getChunk())) {
            event.setCancelled(true);
        }
    }
}
