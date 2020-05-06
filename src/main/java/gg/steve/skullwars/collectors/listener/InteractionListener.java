package gg.steve.skullwars.collectors.listener;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import gg.steve.skullwars.collectors.core.Collector;
import gg.steve.skullwars.collectors.core.CollectorManager;
import gg.steve.skullwars.collectors.nbt.NBTItem;
import gg.steve.skullwars.collectors.utils.LogUtil;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractionListener implements Listener {

    @EventHandler
    public void collectorPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        if (event.getPlayer().getItemInHand() == null || event.getPlayer().getItemInHand().getType().equals(Material.AIR)) return;
        NBTItem item = new NBTItem(event.getPlayer().getItemInHand());
        if (!item.getBoolean("skull.collector")) return;
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (!fPlayer.hasFaction()) {
            event.setCancelled(true);
            return;
        }
        if (!Board.getInstance().getFactionAt(new FLocation(event.getBlock())).equals(fPlayer.getFaction())) {
            event.setCancelled(true);
            return;
        }
        Chunk chunk = event.getPlayer().getWorld().getChunkAt(event.getBlock().getLocation());
        if (CollectorManager.isCollectorActive(chunk)) {
            event.setCancelled(true);
            return;
        }
        CollectorManager.addCollectorForFaction(fPlayer.getFactionId(), event.getBlock().getLocation());
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
    }
}
