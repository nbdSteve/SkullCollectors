package gg.steve.skullwars.collectors;

import gg.steve.skullwars.collectors.core.CollectorManager;
import gg.steve.skullwars.collectors.drops.EntityDrops;
import gg.steve.skullwars.collectors.managers.FileManager;
import gg.steve.skullwars.collectors.managers.SetupManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;

public final class SkullCollectors extends JavaPlugin {
    private static SkullCollectors instance;
    private static DecimalFormat numberFormat = new DecimalFormat("#,###.##");

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        SetupManager.setupFiles(new FileManager(instance));
        SetupManager.registerCommands(instance);
        SetupManager.registerEvents(instance);
        CollectorManager.initialise();
        EntityDrops.loadDrops();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        CollectorManager.saveAllFactionCollectors();
        EntityDrops.shutdown();
    }

    public static SkullCollectors get() {
        return instance;
    }

    public static DecimalFormat getNumberFormat() {
        return numberFormat;
    }
}
