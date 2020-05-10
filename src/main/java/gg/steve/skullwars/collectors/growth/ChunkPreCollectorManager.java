package gg.steve.skullwars.collectors.growth;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import gg.steve.skullwars.collectors.SkullCollectors;
import gg.steve.skullwars.collectors.core.CollectorManager;
import gg.steve.skullwars.collectors.core.DropType;
import gg.steve.skullwars.collectors.managers.Files;
import gg.steve.skullwars.collectors.utils.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChunkPreCollectorManager implements Listener {
    private static Map<Block, Integer> spawners;

    public static void initialise() {
        spawners = new HashMap<>();
        Bukkit.getScheduler().runTaskTimer(SkullCollectors.get(), () -> {
            if (spawners.isEmpty()) return;
            spawners.clear();
        }, 0L, 60 * 20L);
    }

    @EventHandler
    public void cropGrow(BlockGrowEvent event) {
        if (event.isCancelled()) return;
        if (CollectorManager.getFactionCollectorManager(Board.getInstance().getIdAt(new FLocation(event.getBlock().getLocation()))) == null) {
            CollectorManager.addFactionCollectorManager(Board.getInstance().getIdAt(new FLocation(event.getBlock().getLocation())));
        }
        if (!CollectorManager.isCollectorActive(event.getBlock().getChunk())) {
            if (event.getBlock().getRelative(BlockFace.DOWN).getType().toString().equalsIgnoreCase("sugar_cane_block")) {
                return;
            }
            event.setCancelled(true);
            return;
        }
        if (CollectorManager.getCollector(event.getBlock().getChunk()).getManager().getDropAmount(DropType.CROP) >= DropType.getCapacity(DropType.CROP)) {
            event.getBlock().setType(Material.AIR);
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void mobSpawn(SpawnerSpawnEvent event) {
        if (event.isCancelled()) return;
        if (CollectorManager.getFactionCollectorManager(Board.getInstance().getIdAt(new FLocation(event.getSpawner().getLocation()))) == null) {
            CollectorManager.addFactionCollectorManager(Board.getInstance().getIdAt(new FLocation(event.getSpawner().getLocation())));
        }
        if (!CollectorManager.isCollectorActive(event.getSpawner().getChunk())) {
            event.setCancelled(true);
            return;
        }
        if (CollectorManager.getCollector(event.getSpawner().getChunk()).getManager().getDropAmount(DropType.MOB) >= DropType.getCapacity(DropType.MOB)) {
            event.setCancelled(true);
            return;
        }
        if (spawners == null) {
            initialise();
        }
        if (spawners.containsKey(event.getSpawner().getBlock())) {
            if (spawners.get(event.getSpawner().getBlock()) >= Files.getSpawnsPerMinute()) {
                event.setCancelled(true);
                return;
            }
        } else {
            spawners.put(event.getSpawner().getBlock(), 0);
        }
        spawners.put(event.getSpawner().getBlock(), spawners.get(event.getSpawner().getBlock()) + 1);
        if (!Files.doSpawn(event.getEntityType())) {
            if (Files.doVanillaDrops()) {
                Damageable entity = (Damageable) event.getEntity();
                entity.damage(10000);
            } else {
                Bukkit.getPluginManager().callEvent(new EntityDeathEvent((LivingEntity) event.getEntity(), new ArrayList<>()));
            }
            event.getEntity().remove();
        }
    }
}
