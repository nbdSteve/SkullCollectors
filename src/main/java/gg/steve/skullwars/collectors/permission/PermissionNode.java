package gg.steve.skullwars.collectors.permission;

import gg.steve.skullwars.collectors.managers.Files;
import org.bukkit.command.CommandSender;

public enum PermissionNode {
    GUI("command.gui"),
    RELOAD("command.reload"),
    HELP("command.help");

    private String path;

    PermissionNode(String path) {
        this.path = path;
    }

    public String get() {
        return Files.PERMISSIONS.get().getString(this.path);
    }

    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(get());
    }
}
