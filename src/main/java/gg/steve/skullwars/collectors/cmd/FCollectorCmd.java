package gg.steve.skullwars.collectors.cmd;

import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import gg.steve.skullwars.collectors.core.CollectorManager;
import gg.steve.skullwars.collectors.message.MessageType;

public class FCollectorCmd extends FCommand {

    public FCollectorCmd() {
        aliases.add("collector");
        aliases.add("collectors");
        this.requirements = new CommandRequirements.Builder(Permission.MONEY_WITHDRAW).playerOnly().memberOnly().build();
    }

    @Override
    public void perform(CommandContext context) {
        if (!context.fPlayer.hasFaction()) {
            MessageType.NO_FACTION.message(context.player);
            return;
        }
        if (!CollectorManager.isCollectorManagerLoaded(context.faction)) {
            CollectorManager.addFactionCollectorManager(context.fPlayer.getFactionId());
        }
        CollectorManager.openCollectorGui(context.fPlayer);
    }

    @Override
    public TL getUsageTranslation() {
        return null;
    }
}
