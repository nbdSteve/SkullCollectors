package gg.steve.skullwars.collectors.core;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.event.FactionDisbandEvent;
import gg.steve.skullwars.collectors.managers.Files;
import gg.steve.skullwars.collectors.message.MessageType;
import gg.steve.skullwars.collectors.utils.ItemBuilderUtil;
import gg.steve.skullwars.collectors.utils.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    public static void removeFactionCollectorManager(String factionId) {
        if (!factionCollectors.containsKey(factionId)) return;
        factionCollectors.get(factionId).saveCollectorData();
        factionCollectors.remove(factionId);
    }

    public static void saveAllFactionCollectors() {
        if (factionCollectors == null || factionCollectors.isEmpty()) return;
        for (String factionId : factionCollectors.keySet()) {
            removeFactionCollectorManager(factionId);
        }
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

    public static void purgeFactionCollectorData(String factionId) {
        removeFactionCollectorManager(factionId);
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
        if (fPlayer.getFaction().getOnlinePlayers().size() <= 1 && factionCollectors.containsKey(fPlayer.getFactionId())) {
            removeFactionCollectorManager(fPlayer.getFactionId());
        }
    }

    @EventHandler
    public void collectorCommand(PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().equalsIgnoreCase("/f collector")) return;
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
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (!factionCollectors.containsKey(fPlayer.getFactionId())) {
            addFactionCollectorManager(fPlayer.getFactionId());
        }
        // check permission
        factionCollectors.get(fPlayer.getFactionId()).openFCollectorGui(event.getPlayer());
    }
}
