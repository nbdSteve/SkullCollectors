package gg.steve.skullwars.collectors.managers;

import com.massivecraft.factions.FactionsPlugin;
import gg.steve.skullwars.collectors.SkullCollectors;
import gg.steve.skullwars.collectors.cmd.CollectorCmd;
import gg.steve.skullwars.collectors.cmd.FCollectorCmd;
import gg.steve.skullwars.collectors.core.CollectorManager;
import gg.steve.skullwars.collectors.growth.ChunkPreCollectorManager;
import gg.steve.skullwars.collectors.gui.GuiClickListener;
import gg.steve.skullwars.collectors.listener.CollectionListener;
import gg.steve.skullwars.collectors.listener.InteractionListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * Class that handles setting up the plugin on start
 */
public class SetupManager {

    private SetupManager() throws IllegalAccessException {
        throw new IllegalAccessException("Manager class cannot be instantiated.");
    }

    /**
     * Loads the files into the file manager
     *
     * @param fileManager FileManager, the plugins file manager
     */
    public static void setupFiles(FileManager fileManager) {
        // general files
        for (Files file : Files.values()) {
            file.load(fileManager);
        }
    }

    public static void registerCommands(SkullCollectors instance) {
        instance.getCommand("collector").setExecutor(new CollectorCmd());
        FactionsPlugin.getInstance().cmdBase.addSubCommand(new FCollectorCmd());
    }

    /**
     * Register all of the events for the plugin
     *
     * @param instance Plugin, the main plugin instance
     */
    public static void registerEvents(Plugin instance) {
        PluginManager pm = instance.getServer().getPluginManager();
        pm.registerEvents(new GuiClickListener(), instance);
        pm.registerEvents(new InteractionListener(), instance);
        pm.registerEvents(new CollectionListener(), instance);
        pm.registerEvents(new CollectorManager(), instance);
        pm.registerEvents(new ChunkPreCollectorManager(), instance);
    }
}
