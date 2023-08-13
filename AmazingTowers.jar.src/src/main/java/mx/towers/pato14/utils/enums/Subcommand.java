package mx.towers.pato14.utils.enums;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;

public enum Subcommand { //* = optional argument, always at the end if it exists. $ = argument needed if run from the console
    STATS(0, 1, false, false, "<player>"),
    SPECTATOR(0, 0, true, true),
    ORGANIZER(0, 1, true, true, "<password>"),
    COUNT(1, 1, false, true, "stop|start|<number>", "$<instanceName>"),
    RULE(1, 2, false, true, argsBuilder(Arrays.stream(Rule.values()).map(x -> x.name().toLowerCase()).toArray(String[]::new), '|'), "true|false", "$<instanceName>"),
    SETSCORE(1, 2, false, true, "%team_colors%", "<number>", "$<instanceName>"),
    JOINTEAM(1, 2, false, true, "%team_colors%", "<onlinePlayer>", "$<instanceName>"),
    TPWORLD(2, 1, true, false, "<worldName>"),
    CREATEWORLD(2, 1, false, false, "<instanceName>|all"),
    BACKUPWORLD(2, 0, false, false, "$<instanceName>"),
    LOADWORLD(2, 1, false, false, "<worldName>"),
    SETREGION(2, 1, true, false, argsBuilder(Arrays.stream(Location.values()).map(x -> x.name().toLowerCase()).toArray(String[]::new), '|'), "*%team_colors%"),
    HELP(2, 0, false, false),
    VAULTINFO(2, 0, false, false),
    RELOADCONFIG(2, 1, false, false, argsBuilder(Arrays.stream(ConfigType.values()).map(x -> x.name().toLowerCase()).toArray(String[]::new), '|'), "$<instanceName>"),
    TOOL(2, 1, true, false, "wand|refillChest");

    private final int permissionLevel;
    private final int numberOfNeededArguments;
    private final boolean playerExecutorOnly;
    private final boolean needsAGameInstance;
    private final String[] arguments;

    Subcommand(int permissionLevel, int numberOfNeededArguments, boolean playerExecutorOnly, boolean needsAGameInstance, String... arguments) {
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

    public boolean hasPermission(CommandSender sender) {
        int senderPermission = sender.hasPermission("towers.admin") ? 2 : sender.hasPermission("towers.organizer") ? 1 : 0;
        return this.permissionLevel <= senderPermission;
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

    public static String listOfCommands(int numberOfTeams) {
        StringBuilder toret = new StringBuilder();
        for (Subcommand subcommand : Subcommand.values()) {
            toret.append(subcommand.getCorrectUse(numberOfTeams));
            toret.append("\n");
        }
        return toret.toString();
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
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
                        if (isInteger(args[i]))
                            matches = true;
                    } else if (currentSubArg.equals("<instanceName>")) {
                        if ((gameInstance = AmazingTowers.getPlugin().getGameInstance(Bukkit.getWorld(args[i]))) != null)
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
