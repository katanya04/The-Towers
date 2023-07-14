package mx.towers.pato14.utils.enums;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public enum Subcommand {
    STATS(0, 1, false, true, "<player>"),
    SPECTATOR(0, 0, true, true),
    ORGANIZER(0, 1, true, true, "<password>"),
    COUNT(1, 1, true, true, "stop|start|<number>"),
    RULE(1, 2, true, true, argsBuilder(Arrays.stream(Rule.values()).map(x -> x.name().toLowerCase()).toArray(String[]::new), '|'), "true|false"),
    SETSCORE(1, 2, true, true, argsBuilder(Arrays.stream(TeamColor.values()).map(x -> x.name().toLowerCase()).toArray(String[]::new), '|'), "<number>"),
    JOINTEAM(1, 2, true, true, argsBuilder(Arrays.stream(TeamColor.values()).map(x -> x.name().toLowerCase()).toArray(String[]::new), '|'), "<onlinePlayer>"),
    LOCATIONS(2, 0, true, true),
    TPWORLD(2, 1, true, true, "<worldName>"),
    CREATEWORLD(2, 2, true, true, "<worldName>", "emptyWorld"),
    BACKUPWORLD(2, 0, true, true),
    LOADWORLD(2, 1, true, true, "<worldName>"),
    SETREGION(2, 1, true, false, argsBuilder(Arrays.stream(Location.values()).map(x -> x.name().toLowerCase()).toArray(String[]::new), '|')),
    HELP(2, 0, true, true),
    VAULTINFO(2, 0, true, true),
    RELOADCONFIG(2, 1, true, true, argsBuilder(Arrays.stream(ConfigType.values()).map(x -> x.name().toLowerCase()).toArray(String[]::new), '|')),
    TOOL(2, 1, true, true, "wand|refillChest");

    private final int permissionLevel;
    private final int numberOfArguments;
    private final boolean playerExecutorOnly;
    private final boolean needAllArgs;
    private final String[] arguments;

    Subcommand(int permissionLevel, int numberOfArguments, boolean playerExecutorOnly, boolean needAllArgs, String... arguments) {
        this.permissionLevel = permissionLevel;
        this.numberOfArguments = numberOfArguments;
        this.playerExecutorOnly = playerExecutorOnly;
        this.needAllArgs = needAllArgs;
        this.arguments = arguments;
    }

    public String[] getArguments() {
        return arguments;
    }

    private static String argsBuilder(String[] array, char separator) {
        StringBuilder args = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            args.append(array[i]);
            if (i < array.length - 1)
                args.append(separator);
        }
        return args.toString();
    }

    public static Subcommand isValidSubcommand(String subcommand) {
        try {
            return Subcommand.valueOf(subcommand.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public boolean hasPermission(CommandSender sender) {
        int senderPermission = sender.hasPermission("towers.admin") ? 2 : sender.hasPermission("towers.organizer") ? 1 : 0;
        return this.permissionLevel <= senderPermission;
    }

    public boolean correctNumberOfArguments(String[] args) {
        return !needAllArgs || args.length == this.numberOfArguments;
    }

    public boolean checkCorrectSender(CommandSender sender) {
        return !this.playerExecutorOnly || sender instanceof Player;
    }

    private String getArgs() {
        return argsBuilder(this.arguments, ' ');
    }

    public String getCorrectUse() {
        StringBuilder toret = new StringBuilder();
        toret.append("§f/towers §a").append(this.name().toLowerCase());
        String arguments = this.getArgs();
        if (!arguments.isEmpty())
            toret.append(" §e").append(arguments);
        return toret.toString();
    }

    public static String listOfCommands() {
        StringBuilder toret = new StringBuilder();
        for (Subcommand subcommand : Subcommand.values()) {
            toret.append(subcommand.getCorrectUse());
            toret.append("\n");
        }
        return toret.toString();
    }

}
