package gg.steve.skullwars.collectors.integration;

import net.brcdev.shopgui.ShopGuiPlusApi;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ShopGUIPlusIntegration {

    public static double getItemPrice(Material material) {
        return ShopGuiPlusApi.getItemStackPriceSell(new ItemStack(material));
    }
}
