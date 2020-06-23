package gg.steve.skullwars.collectors.growth;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.integration.Econ;
import gg.steve.mc.skullwars.raids.core.FBaseManager;
import gg.steve.skullwars.collectors.SkullCollectors;
import gg.steve.skullwars.collectors.core.Collector;
import gg.steve.skullwars.collectors.core.CollectorManager;
import gg.steve.skullwars.collectors.drops.DropType;
import gg.steve.skullwars.collectors.drops.EntityDrops;
import gg.steve.skullwars.collectors.managers.Files;
import net.techcable.tacospigot.event.entity.SpawnerPreSpawnEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

import java.util.ArrayList;
import java.util.List;

public class ChunkPreCollectorManager implements Listener {
    private static List<Block> spawners;

    public static void initialise() {
        spawners = new ArrayList<>();
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
    public void mobSpawn(SpawnerPreSpawnEvent event) {
        if (event.isCancelled()) return;
        Faction faction = Board.getInstance().getFactionAt(new FLocation(event.getLocation()));
        if (faction.isWilderness() || faction.isSafeZone() || faction.isWarZone()) {
            event.setCancelled(true);
            return;
        }
        if (!FBaseManager.isSpawnerChunk(faction, event.getLocation().getChunk())) {
            event.setCancelled(true);
            return;
        }
        if (CollectorManager.getFactionCollectorManager(faction.getId()) == null) {
            CollectorManager.addFactionCollectorManager(faction.getId());
        }
        if (!CollectorManager.isCollectorActive(event.getLocation().getChunk())) {
            event.setCancelled(true);
            return;
        }
        if (CollectorManager.getCollector(event.getLocation().getChunk()).getManager().getDropAmount(DropType.MOB) >= DropType.getCapacity(DropType.MOB)) {
            event.setCancelled(true);
            return;
        }
        if (spawners == null) {
            initialise();
        }
        if (spawners.contains(event.getLocation().getBlock())) {
            event.setCancelled(true);
            return;
        } else {
            spawners.add(event.getLocation().getBlock());
        }
        if (!Files.doSpawn(event.getSpawnedType())) {
            event.setCancelled(true);
            Collector collector = CollectorManager.getCollector(event.getLocation().getChunk());
            collector.creatureDeath(EntityDrops.getDrops(event.getSpawnedType()));
            Econ.deposit(faction.getAccountId(), EntityDrops.getMoney(event.getSpawnedType()));
        }
    }
}
