package gg.steve.skullwars.collectors.cmd.sub;

import gg.steve.skullwars.collectors.SkullCollectors;
import gg.steve.skullwars.collectors.managers.Files;
import gg.steve.skullwars.collectors.message.CommandDebug;
import gg.steve.skullwars.collectors.message.MessageType;
import gg.steve.skullwars.collectors.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ReloadCmd {

    public static void reload(CommandSender sender) {
        if (!PermissionNode.RELOAD.hasPermission(sender)) {
            CommandDebug.INSUFFICIENT_PERMISSION.message(sender, PermissionNode.RELOAD.get());
            return;
        }
        Files.reload();
        Bukkit.getPluginManager().disablePlugin(SkullCollectors.get());
        Bukkit.getPluginManager().enablePlugin(SkullCollectors.get());
        MessageType.RELOAD.message(sender);
    }
}
