package gg.steve.skullwars.collectors.utils;

import gg.steve.skullwars.collectors.SkullCollectors;
import gg.steve.skullwars.collectors.core.DropType;
import gg.steve.skullwars.collectors.core.FactionCollectorsManager;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class GuiItemUtil {

    public static ItemStack createItem(ConfigurationSection section, String entry, FactionCollectorsManager collectorManager) {
        ItemBuilderUtil builder;
        if (section.getString(entry + ".material").startsWith("hdb")) {
            String[] id = section.getString(entry + ".material").split("-");
            builder = new ItemBuilderUtil(new HeadDatabaseAPI().getItemHead(id[1]));
        } else {
            builder = new ItemBuilderUtil(section.getString(entry + ".material"), section.getString(entry + ".data"));
        }
        builder.addName(section.getString(entry + ".name"));
        builder.setLorePlaceholders("{mob-drops}", "{crop-drops}", "{tnt-banked}", "{max-tnt}", "{collector-count}", "{collected-count}", "{lifetime-count}");
        builder.addLore(section.getStringList(entry + ".lore"),
                SkullCollectors.getNumberFormat().format(collectorManager.getDropAmount(DropType.MOB)),
                SkullCollectors.getNumberFormat().format(collectorManager.getDropAmount(DropType.CROP)),
                SkullCollectors.getNumberFormat().format(collectorManager.getFaction().getTnt()),
                SkullCollectors.getNumberFormat().format(collectorManager.getFaction().getTntBankLimit()),
                SkullCollectors.getNumberFormat().format(collectorManager.getCollectorCount()),
                SkullCollectors.getNumberFormat().format(collectorManager.getCollectedItems()),
                SkullCollectors.getNumberFormat().format(collectorManager.getLifetime()));
        builder.addEnchantments(section.getStringList(entry + ".enchantments"));
        builder.addItemFlags(section.getStringList(entry + ".item-flags"));
        return builder.getItem();
    }
}
