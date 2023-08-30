package mx.towers.pato14.utils.enums;

import org.bukkit.command.CommandSender;

public enum PermissionLevel {
    ADMIN("towers.admin"),
    ORGANIZER("towers.organizer"),
    NONE("");
    private final String permissionName;
    PermissionLevel(String permissionName) {
        this.permissionName = permissionName;
    }

    public static PermissionLevel getPermissionLevel(CommandSender commandSender) {
        for (PermissionLevel permissionLevel : PermissionLevel.values()) {
            if (commandSender.hasPermission(permissionLevel.permissionName))
                return permissionLevel;
        }
        return NONE;
    }

    public static boolean hasPermission(PermissionLevel needed, PermissionLevel have) {
        return have.ordinal() <= needed.ordinal();
    }

    public String getPermissionName() {
        return permissionName;
    }
}