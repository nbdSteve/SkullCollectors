package gg.steve.skullwars.collectors;

import gg.steve.skullwars.collectors.core.CollectorManager;
import gg.steve.skullwars.collectors.managers.FileManager;
import gg.steve.skullwars.collectors.managers.SetupManager;
import gg.steve.skullwars.collectors.utils.LogUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;

public final class SkullCollectors extends JavaPlugin {
    private static SkullCollectors instance;
    private static Economy economy;
    private static DecimalFormat numberFormat = new DecimalFormat("#,###.##");

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        SetupManager.setupFiles(new FileManager(instance));
        SetupManager.registerCommands(instance);
        SetupManager.registerEvents(instance);
        CollectorManager.initialise();
        // verify that the server is running vault so that eco features can be used
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            economy = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        } else {
            LogUtil.info("Unable to find economy instance, disabling economy features.");
            economy = null;
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        CollectorManager.saveAllFactionCollectors();
    }

    public static SkullCollectors get() {
        return instance;
    }

    public static DecimalFormat getNumberFormat() {
        return numberFormat;
    }

    public static Economy eco() {
        return economy;
    }
}
