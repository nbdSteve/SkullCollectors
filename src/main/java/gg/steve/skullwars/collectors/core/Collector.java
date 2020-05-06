package gg.steve.skullwars.collectors.core;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Collector implements CollectorEvents {
    private Chunk chunk;
    private World world;
    private FactionCollectorsManager manager;
    private UUID collectorId;
    private Location collectorLocation;

    public Collector(UUID collectorId, World world, Location collectorLocation, FactionCollectorsManager manager) {
        this.world = world;
        this.chunk = collectorLocation.getChunk();
        this.manager = manager;
        this.collectorId = collectorId;
        this.collectorLocation = collectorLocation;
        CollectorManager.addCollector(this);
    }

    @Override
    public void creatureDeath(EntityDeathEvent event) {
        List<ItemStack> collectedItems = new ArrayList<>();
        for (ItemStack item : event.getDrops()) {
            if (DropType.isMobDrop(item.getType()) && this.manager.getDropAmount(DropType.MOB) < DropType.getCapacity(DropType.MOB)) {
                this.manager.addDrop(item.getType(), item.getAmount());
                collectedItems.add(item);
            }
        }
        for (ItemStack item : collectedItems) {
            event.getDrops().remove(item);
        }
    }

    @Override
    public void cropBreak(Block block) {
        List<ItemStack> collectedItems = new ArrayList<>();
        for (ItemStack item : block.getDrops()) {
            if (DropType.isCropDrop(item.getType()) && this.manager.getDropAmount(DropType.CROP) < DropType.getCapacity(DropType.CROP)) {
                this.manager.addDrop(item.getType(), item.getAmount());
                collectedItems.add(item);
            }
        }
        for (ItemStack item : collectedItems) {
            block.getDrops().remove(item);
        }
        block.setType(Material.AIR);
    }

    public Chunk getChunk() {
        return chunk;
    }

    public World getWorld() {
        return world;
    }

    public FactionCollectorsManager getManager() {
        return manager;
    }

    public UUID getCollectorId() {
        return collectorId;
    }

    public Location getCollectorLocation() {
        return collectorLocation;
    }
}
