package gg.steve.skullwars.collectors.core;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.event.LandUnclaimEvent;
import gg.steve.skullwars.collectors.managers.Files;
import gg.steve.skullwars.collectors.message.MessageType;
import gg.steve.skullwars.collectors.utils.ItemBuilderUtil;
import gg.steve.skullwars.collectors.utils.LogUtil;
import okhttp3.internal.annotations.EverythingIsNonNull;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CollectorManager implements Listener {
    private static Map<Chunk, Collector> activeCollectors;
    private static Map<String, FactionCollectorsManager> factionCollectors;
    private static ItemStack collector;

    public static void initialise() {
        activeCollectors = new HashMap<>();
        factionCollectors = new HashMap<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
            if (factionCollectors.containsKey(fPlayer.getFactionId())) continue;
            addFactionCollectorManager(fPlayer.getFactionId());
        }
        LogUtil.info("Loaded collector data for online factions");
        ConfigurationSection item = Files.CONFIG.get().getConfigurationSection("collector");
        ItemBuilderUtil builder = new ItemBuilderUtil(item.getString("material"), item.getString("data"));
        builder.addName(item.getString("name"));
        builder.addLore(item.getStringList("lore"));
        builder.addEnchantments(item.getStringList("enchantments"));
        builder.addItemFlags(item.getStringList("item-flags"));
        builder.addNBT();
        collector = builder.getItem();
        LogUtil.info("Loaded the collector item from configuration");
    }

    public static boolean isCollectorActive(Chunk chunk) {
        return activeCollectors.containsKey(chunk);
    }

    public static Collector getCollector(Chunk chunk) {
        return activeCollectors.get(chunk);
    }

    public static void addCollector(Collector collector) {
        if (activeCollectors.containsKey(collector.getChunk())) return;
        activeCollectors.put(collector.getChunk(), collector);
    }

    public static void removeCollector(Collector collector) {
        collector.getManager().removeCollector(collector);
        activeCollectors.remove(collector.getChunk());
    }

    public static void addFactionCollectorManager(String factionId) {
        if (factionCollectors.containsKey(factionId)) return;
        factionCollectors.put(factionId, new FactionCollectorsManager(factionId));
    }

    public static void removeFactionCollectorManager(String factionId, boolean disband) {
        if (!factionCollectors.containsKey(factionId)) return;
        factionCollectors.get(factionId).saveCollectorData();
        List<Collector> factionCollector = new ArrayList<>();
        for (Collector collector : activeCollectors.values()) {
            if (collector.getManager().getFactionId().equalsIgnoreCase(factionId)) {
                factionCollector.add(collector);
                collector.getCollectorLocation().getBlock().setType(Material.AIR);
//                collector.getWorld().dropItem(collector.getCollectorLocation(), getCollectorItem());
            }
        }
        for (Collector collector : factionCollector) {
            activeCollectors.remove(collector.getChunk());
        }
        factionCollectors.get(factionId).getCollectors().clear();
        if (disband) factionCollectors.remove(factionId);
    }

    public static void saveAllFactionCollectors() {
        if (factionCollectors == null || factionCollectors.isEmpty()) return;
        for (String factionId : factionCollectors.keySet()) {
            factionCollectors.get(factionId).saveCollectorData();
        }
        activeCollectors.clear();
        factionCollectors.clear();
    }

    public static void addCollectorForFaction(String factionId, Location location) {
        if (!factionCollectors.containsKey(factionId)) {
            addFactionCollectorManager(factionId);
        }
        factionCollectors.get(factionId).addCollector(new Collector(UUID.randomUUID(), location.getWorld(), location, factionCollectors.get(factionId)));
    }

    public static FactionCollectorsManager getFactionCollectorManager(String factionId) {
        return factionCollectors.get(factionId);
    }

    public static void purgeFactionCollectorData(String factionId, boolean disband) {
        removeFactionCollectorManager(factionId, disband);
        Files.DATA.get().set(factionId, null);
        Files.DATA.save();
    }

    public static ItemStack getCollectorItem() {
        return collector;
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (!factionCollectors.containsKey(fPlayer.getFactionId()))
            addFactionCollectorManager(fPlayer.getFactionId());
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (!factionCollectors.containsKey(fPlayer.getFactionId())) return;
        factionCollectors.get(fPlayer.getFactionId()).saveCollectorData();
    }

    @EventHandler
    public void collectorCommand(PlayerCommandPreprocessEvent event) {
        if (!(event.getMessage().equalsIgnoreCase("/f collector") || event.getMessage().equalsIgnoreCase("/f collectors"))) return;
        event.setCancelled(true);
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (!fPlayer.hasFaction()) {
            MessageType.NO_FACTION.message(event.getPlayer());
            event.setCancelled(true);
            return;
        }
        if (!factionCollectors.containsKey(fPlayer.getFactionId())) {
            addFactionCollectorManager(fPlayer.getFactionId());
        }
        // check permission
        factionCollectors.get(fPlayer.getFactionId()).openFCollectorGui(event.getPlayer());
    }

    @EventHandler
    public void collectorDisbandHandle(FactionDisbandEvent event) {
        purgeFactionCollectorData(event.getFPlayer().getFactionId(), true);
    }

    @EventHandler
    public void unclaimAll(LandUnclaimAllEvent event) {
        purgeFactionCollectorData(event.getfPlayer().getFactionId(), false);
    }

    @EventHandler
    public void unclaim(LandUnclaimEvent event) {
        if (!activeCollectors.containsKey(event.getLocation().getChunk())) return;
        Collector collector = activeCollectors.get(event.getLocation().getChunk());
        collector.getCollectorLocation().getBlock().setType(Material.AIR);
        Files.DATA.get().set(event.getFactionId() + "." + collector.getCollectorId(), null);
        Files.DATA.save();
        factionCollectors.get(event.getFactionId()).removeCollector(collector);
        activeCollectors.remove(event.getLocation().getChunk());
    }
}
