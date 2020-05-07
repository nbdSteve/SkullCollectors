package gg.steve.skullwars.collectors.cmd.sub;

import gg.steve.skullwars.collectors.message.CommandDebug;
import gg.steve.skullwars.collectors.message.MessageType;
import gg.steve.skullwars.collectors.permission.PermissionNode;
import org.bukkit.command.CommandSender;

public class HelpCmd {

    public static void help(CommandSender sender) {
        if (!PermissionNode.HELP.hasPermission(sender)) {
            CommandDebug.INSUFFICIENT_PERMISSION.message(sender, PermissionNode.HELP.get());
            return;
        }
        MessageType.HELP.message(sender);
    }
}
