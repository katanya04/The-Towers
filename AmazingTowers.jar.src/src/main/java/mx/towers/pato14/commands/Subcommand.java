package mx.towers.pato14.commands;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;

public enum Subcommand { //* = optional argument, always at the end if it exists. $ = argument needed if run from the console
    STATS(PermissionLevel.NONE, 1, false, false, "<player>"),
    SPECTATOR(PermissionLevel.NONE, 0, true, true),
    ORGANIZER(PermissionLevel.NONE, 1, true, true, "<password>", "$<instanceName>"),
    COUNT(PermissionLevel.ORGANIZER, 1, false, true, "stop|start|<number>", "$<instanceName>"),
    RULE(PermissionLevel.ORGANIZER, 2, false, true, argsBuilder(Arrays.stream(Rule.values()).map(x -> x.name().toLowerCase()).toArray(String[]::new), '|'), "true|false", "$<instanceName>"),
    SETSCORE(PermissionLevel.ORGANIZER, 2, false, true, "%team_colors%", "<number>", "$<instanceName>"),
    JOINTEAM(PermissionLevel.ORGANIZER, 2, false, true, "%team_colors%", "<onlinePlayer>", "$<instanceName>"),
    TPWORLD(PermissionLevel.ADMIN, 2, false, false, "<worldName>", "<player>"),
    CREATEWORLD(PermissionLevel.ADMIN, 1, false, false, "<instanceName>|all"),
    BACKUPWORLD(PermissionLevel.ADMIN, 0, false, false, "$<instanceName>"),
    LOADWORLD(PermissionLevel.ADMIN, 1, false, false, "<worldName>"),
    SETREGION(PermissionLevel.ADMIN, 1, true, true, argsBuilder(Arrays.stream(Location.values()).map(x -> x.name().toLowerCase()).toArray(String[]::new), '|'), "*%team_colors%"),
    HELP(PermissionLevel.NONE, 0, false, false),
    VAULTINFO(PermissionLevel.ADMIN, 0, false, false),
    RELOADCONFIG(PermissionLevel.ADMIN, 1, false, false, argsBuilder(Arrays.stream(ConfigType.values()).map(x -> x.name().toLowerCase()).toArray(String[]::new), '|'), "$<instanceName>"),
    TOOL(PermissionLevel.ADMIN, 1, true, false, "wand|refillChest"),
    TIMER(PermissionLevel.ADMIN, 1, false, true, "true|false|<number>", "$<instanceName>"),
    WHITELIST(PermissionLevel.ADMIN, 1, false, true, "add|remove|list|true|false", "*<player>"),
    BLACKLIST(PermissionLevel.ADMIN, 1, false, true, "add|remove|list|true|false", "*<player>"),
    MODIFYSETTING(PermissionLevel.ORGANIZER, 1, false, true, "<path>", "*add|remove"),
    SAVESETTINGS(PermissionLevel.ORGANIZER, 0, false, true, "$<instanceName>"),
    BOOK(PermissionLevel.ORGANIZER, 0, true, true),
    PARKOURPRIZE(PermissionLevel.ADMIN, 1, false, false, "<player>"),
    KITS(PermissionLevel.NONE, 0, true, true, "*modify");

    private final PermissionLevel permissionLevel;
    private final int numberOfNeededArguments;
    private final boolean playerExecutorOnly;
    private final boolean needsAGameInstance;
    private final String[] arguments;

    Subcommand(PermissionLevel permissionLevel, int numberOfNeededArguments, boolean playerExecutorOnly, boolean needsAGameInstance, String... arguments) {
        this.permissionLevel = permissionLevel;
        this.numberOfNeededArguments = numberOfNeededArguments;
        this.playerExecutorOnly = playerExecutorOnly;
        this.needsAGameInstance = needsAGameInstance;
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

    public boolean hasPermission(CommandSender commandSender) {
        return PermissionLevel.hasPermission(this.permissionLevel, PermissionLevel.getPermissionLevel(commandSender));
    }

    private int getNumberOfConsoleNeededArguments() {
        int toret = 0;
        for (String arg : arguments) {
            if (arg.charAt(0) == '$')
                toret++;
        }
        return toret;
    }

    public boolean correctNumberOfArguments(String[] args, CommandSender sender) {
        if (sender instanceof Entity)
            return numberOfNeededArguments <= args.length - 1 && arguments.length >= args.length - 1;
        else
            return numberOfNeededArguments + getNumberOfConsoleNeededArguments() <= args.length - 1 && arguments.length >= args.length - 1;
    }

    public boolean checkCorrectSender(CommandSender sender) {
        return !this.playerExecutorOnly || sender instanceof Player;
    }

    private String getArgs() {
        return argsBuilder(this.arguments, ' ');
    }

    public String getCorrectUse(int numberOfTeams) {
        StringBuilder toret = new StringBuilder();
        toret.append("§f/towers §a").append(this.name().toLowerCase());
        String arguments = this.getArgs().replace("%team_colors%",
                argsBuilder(Arrays.stream(TeamColor.values()).map(x -> x.name().toLowerCase()).limit(numberOfTeams).toArray(String[]::new), '|'));
        if (!arguments.isEmpty())
            toret.append(" §e").append(arguments);
        return toret.toString();
    }

    public static String listOfCommands(int numberOfTeams, CommandSender sender) {
        PermissionLevel permission =  PermissionLevel.getPermissionLevel(sender);
        StringBuilder toret = new StringBuilder();
        for (Subcommand subcommand : Subcommand.values()) {
            if (!PermissionLevel.hasPermission(subcommand.permissionLevel, permission))
                continue;
            toret.append(subcommand.getCorrectUse(numberOfTeams));
            toret.append("\n");
        }
        return toret.toString();
    }

    public static Map.Entry<Integer, GameInstance> checkArgs(Subcommand subcommand, String[] args, int numberOfTeams, CommandSender sender) {
        GameInstance gameInstance = null;
        int i = 1;
        for (String currentArg : subcommand.getArguments()) {
            currentArg = currentArg.replace("%team_colors%",
                argsBuilder(Arrays.stream(TeamColor.values()).map(x -> x.name().toLowerCase()).limit(numberOfTeams).toArray(String[]::new), '|'));
            if (currentArg.charAt(0) == '*') //Optional argument is always at the end
                return new AbstractMap.SimpleEntry<>(0, gameInstance);
            if (currentArg.charAt(0) == '$') //Optional argument is always at the end
                if (sender instanceof Entity)
                    return new AbstractMap.SimpleEntry<>(0, gameInstance);
            String[] currentArgSeparated = currentArg.split("\\|");
            boolean matches = false;
            for (String currentSubArg : currentArgSeparated) {
                if (currentArg.charAt(0) == '$')
                    currentSubArg = currentSubArg.replace("$", "");
                if (!(currentSubArg.charAt(0) == '<')) {
                    if (currentSubArg.equalsIgnoreCase(args[i])) {
                        matches = true;
                        break;
                    }
                } else {
                    if (currentSubArg.equals("<number>")) {
                        if (Utils.isInteger(args[i]))
                            matches = true;
                    } else if (currentSubArg.equals("<instanceName>")) {
                        if ((gameInstance = AmazingTowers.getGameInstance(Bukkit.getWorld(args[i]))) != null)
                            matches = true;
                    } else
                        matches = true;
                    if (matches)
                        break;
                }
            }
            if (matches)
                i++;
            else
                return new AbstractMap.SimpleEntry<>(i, gameInstance); //Position of incorrect argument
        }
        return new AbstractMap.SimpleEntry<>(0, gameInstance); //All arguments are correct
    }

    public boolean needsAGameInstance() {
        return needsAGameInstance;
    }
}
