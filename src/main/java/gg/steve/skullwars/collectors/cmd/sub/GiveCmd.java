package gg.steve.skullwars.collectors.cmd.sub;

import gg.steve.skullwars.collectors.SkullCollectors;
import gg.steve.skullwars.collectors.core.CollectorManager;
import gg.steve.skullwars.collectors.message.CommandDebug;
import gg.steve.skullwars.collectors.message.MessageType;
import gg.steve.skullwars.collectors.permission.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveCmd {

    public static void give(CommandSender sender, String[] args) {
        // /collector give player amount
        if (args.length < 2 || args.length > 3) {
            CommandDebug.INVALID_NUMBER_OF_ARGUMENTS.message(sender);
            return;
        }
        if (!PermissionNode.GIVE.hasPermission(sender)) {
            CommandDebug.INSUFFICIENT_PERMISSION.message(sender, PermissionNode.GIVE.get());
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            CommandDebug.TARGET_NOT_ONLINE.message(sender);
            return;
        }
        int amount = 1;
        if (args.length == 3) {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (Exception e) {
                CommandDebug.INVALID_AMOUNT.message(sender);
                return;
            }
        }
        for (int i = 0; i < amount; i++) {
            target.getInventory().addItem(CollectorManager.getCollectorItem());
        }
        MessageType.GIVE_RECEIVER.message(target, SkullCollectors.getNumberFormat().format(amount));
        Player player = null;
        if (sender instanceof Player) {
            if (!target.getUniqueId().equals(((Player) sender).getUniqueId())) {
                player = (Player) sender;
            }
        }
        if (!(sender instanceof Player) || player != null) {
            MessageType.GIVE_GIVER.message(sender, target.getName(), SkullCollectors.getNumberFormat().format(amount));
        }
    }
}
