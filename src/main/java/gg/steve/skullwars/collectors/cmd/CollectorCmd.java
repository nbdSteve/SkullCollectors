package gg.steve.skullwars.collectors.cmd;

import gg.steve.skullwars.collectors.cmd.sub.GiveCmd;
import gg.steve.skullwars.collectors.cmd.sub.HelpCmd;
import gg.steve.skullwars.collectors.cmd.sub.ReloadCmd;
import gg.steve.skullwars.collectors.message.CommandDebug;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CollectorCmd implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("collector")) {
            if (args.length == 0) {
                HelpCmd.help(sender);
                return true;
            }
            switch (args[0]) {
                case "help":
                case "h":
                    HelpCmd.help(sender);
                    break;
                case "reload":
                case "r":
                    ReloadCmd.reload(sender);
                    break;
                case "give":
                case "g":
                    GiveCmd.give(sender, args);
                    break;
                default:
                    CommandDebug.INVALID_COMMAND.message(sender);
                    break;
            }
        }
        return true;
    }
}
