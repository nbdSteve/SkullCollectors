package gg.steve.skullwars.collectors.gui;

import gg.steve.skullwars.collectors.core.FactionCollectorsManager;
import gg.steve.skullwars.collectors.utils.GuiItemUtil;
import org.bukkit.configuration.ConfigurationSection;

public class FCollectorGui extends AbstractGui {
    private ConfigurationSection section;
    private FactionCollectorsManager collectorManager;

    /**
     * Constructor the create a new Gui
     *
     * @param section
     */
    public FCollectorGui(ConfigurationSection section, FactionCollectorsManager collectorManager) {
        super(section, section.getString("type"), section.getInt("size"));
        this.section = section;
        this.collectorManager = collectorManager;
        refresh();
    }

    public void refresh() {
        for (String entry : section.getKeys(false)) {
            try {
                Integer.parseInt(entry);
            } catch (Exception e) {
                continue;
            }
            setItemInSlot(section.getInt(entry + ".slot"), GuiItemUtil.createItem(section, entry, collectorManager), player -> {
                switch (section.getString(entry + ".action")) {
                    case "none":
                        break;
                    case "close":
                        player.closeInventory();
                        break;
                    case "sell-mob":
                        player.closeInventory();
                        break;
                    case "sell-crop":
                        player.closeInventory();
                        break;
                    case "deposit-tnt":
                        player.closeInventory();
                        break;
                }
            });
        }
    }
}
