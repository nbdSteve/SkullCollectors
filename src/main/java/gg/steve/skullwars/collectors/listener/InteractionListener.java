package gg.steve.skullwars.collectors.listener;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import gg.steve.skullwars.collectors.core.Collector;
import gg.steve.skullwars.collectors.core.CollectorManager;
import gg.steve.skullwars.collectors.managers.Files;
import gg.steve.skullwars.collectors.message.MessageType;
import gg.steve.skullwars.collectors.nbt.NBTItem;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractionListener implements Listener {

    @EventHandler
    public void collectorPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        if (event.getPlayer().getItemInHand().getType().equals(Material.AIR)) return;
        NBTItem item = new NBTItem(event.getPlayer().getItemInHand());
        if (!item.getBoolean("skull.collector")) return;
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (!fPlayer.hasFaction()) {
            MessageType.NO_FACTION.message(event.getPlayer());
            event.setCancelled(true);
            return;
        }
        if (!Board.getInstance().getFactionAt(new FLocation(event.getBlock())).equals(fPlayer.getFaction())) {
            MessageType.NOT_IN_FACTION_LAND.message(event.getPlayer());
            event.setCancelled(true);
            return;
        }
        Chunk chunk = event.getPlayer().getWorld().getChunkAt(event.getBlock().getLocation());
        if (CollectorManager.isCollectorActive(chunk)) {
            MessageType.COLLECTOR_ACTIVE.message(event.getPlayer());
            event.setCancelled(true);
            return;
        }
        if (!Files.isAllowedWorld(chunk.getWorld())) {

            event.setCancelled(true);
            return;
        }
        CollectorManager.addCollectorForFaction(fPlayer.getFactionId(), event.getBlock().getLocation());
        MessageType.COLLECTOR_PLACE.message(event.getPlayer());
    }

    @EventHandler
    public void collectorBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Chunk chunk = event.getBlock().getChunk();
        if (!CollectorManager.isCollectorActive(chunk)) return;
        Collector collector = CollectorManager.getCollector(chunk);
        if (!event.getBlock().equals(collector.getCollectorLocation().getBlock())) return;
        CollectorManager.removeCollector(collector);
        event.getBlock().setType(Material.AIR);
        event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), CollectorManager.getCollectorItem());
        MessageType.COLLECTOR_BREAK.message(event.getPlayer());
    }

    @EventHandler
    public void collectorClick(PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        Chunk chunk = event.getClickedBlock().getChunk();
        if (!CollectorManager.isCollectorActive(chunk)) return;
        Collector collector = CollectorManager.getCollector(chunk);
        if (!(event.getClickedBlock().getX() == collector.getCollectorLocation().getBlockX()
                && event.getClickedBlock().getY() == collector.getCollectorLocation().getBlockY()
                && event.getClickedBlock().getZ() == collector.getCollectorLocation().getBlockZ())) return;
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (!fPlayer.hasFaction() || !fPlayer.getFaction().equals(collector.getManager().getFaction())) {
            MessageType.NOT_PLAYER_COLLECTOR.message(fPlayer.getPlayer());
            return;
        }
        event.setCancelled(true);
        collector.getManager().openFCollectorGui(event.getPlayer());
    }

    @EventHandler
    public void collectorExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            Chunk chunk = block.getChunk();
            if (!CollectorManager.isCollectorActive(chunk)) return;
            Collector collector = CollectorManager.getCollector(chunk);
            if (!block.equals(collector.getCollectorLocation().getBlock())) return;
            CollectorManager.removeCollector(collector);
            block.getDrops().clear();
            block.setType(Material.AIR);
            block.getWorld().dropItem(block.getLocation(), CollectorManager.getCollectorItem());
        }
    }

    @EventHandler
    public void collectorPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (!CollectorManager.isCollectorActive(block.getChunk())) continue;
            Collector collector = CollectorManager.getCollector(block.getChunk());
            if (block.getLocation().equals(collector.getCollectorLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void collectorPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (!CollectorManager.isCollectorActive(block.getChunk())) continue;
            Collector collector = CollectorManager.getCollector(block.getChunk());
            if (block.getLocation().equals(collector.getCollectorLocation())) {
                event.setCancelled(true);
            }
        }
    }
}
