package gg.steve.skullwars.collectors.utils;

import gg.steve.skullwars.collectors.SkullCollectors;
import gg.steve.skullwars.collectors.core.DropType;
import gg.steve.skullwars.collectors.core.FactionCollectorsManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class GuiItemUtil {

    public static ItemStack createItem(ConfigurationSection section, String entry, FactionCollectorsManager collectorManager) {
        ItemBuilderUtil builder = new ItemBuilderUtil(section.getString(entry + ".material"), section.getString(entry + ".data"));
        builder.addName(section.getString(entry + ".name"));
        builder.setLorePlaceholders("{mob-drops}", "{crop-drops}");
        builder.addLore(section.getStringList(entry + ".lore"),
                SkullCollectors.getNumberFormat().format(collectorManager.getDropAmount(DropType.MOB)),
                SkullCollectors.getNumberFormat().format(collectorManager.getDropAmount(DropType.CROP)));
        builder.addEnchantments(section.getStringList(entry + ".enchantments"));
        builder.addItemFlags(section.getStringList(entry + ".item-flags"));
        return builder.getItem();
    }
}
