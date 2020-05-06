package gg.steve.skullwars.collectors.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import gg.steve.skullwars.collectors.core.CollectorManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CollectorCmd implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("collector")) {
            if (args.length == 0) {
                Player player = (Player) sender;
                player.getInventory().addItem(CollectorManager.getCollectorItem());
            } else {
                FPlayer fPlayer = FPlayers.getInstance().getByPlayer((Player) sender);
                CollectorManager.getFactionCollectorManager(fPlayer.getFactionId()).openFCollectorGui((Player) sender);
            }
        }
        return true;
    }
}
