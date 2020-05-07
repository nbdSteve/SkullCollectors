package gg.steve.skullwars.collectors.gui;

import gg.steve.skullwars.collectors.SkullCollectors;
import gg.steve.skullwars.collectors.core.DropType;
import gg.steve.skullwars.collectors.core.FactionCollectorsManager;
import gg.steve.skullwars.collectors.message.MessageType;
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
        super(section, section.getString("type"), section.getInt("size"), collectorManager.getFaction().getTag());
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
                        String[] mob = collectorManager.sellDrops(DropType.MOB);
                        MessageType.MOB_SELL.message(player, mob[1], mob[0]);
                        collectorManager.openFCollectorGui(player);
                        break;
                    case "sell-crop":
                        player.closeInventory();
                        String[] crop = collectorManager.sellDrops(DropType.CROP);
                        MessageType.CROP_SELL.message(player, crop[1], crop[0]);
                        collectorManager.openFCollectorGui(player);
                        break;
                    case "deposit-tnt":
                        player.closeInventory();
                        double tnt = collectorManager.depositTnt(player);
                        MessageType.TNT_DEPOSIT.message(player, SkullCollectors.getNumberFormat().format(tnt));
                        collectorManager.openFCollectorGui(player);
                        break;
                }
            });
        }
    }
}
