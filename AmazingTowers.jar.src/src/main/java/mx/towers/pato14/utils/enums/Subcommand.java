package mx.towers.pato14.utils.enums;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public enum Subcommand { //* = optional argument, always at the end if it exists
    STATS(0, 0, false, "<player>"),
    SPECTATOR(0, 0, true),
    ORGANIZER(0, 1, true, "<password>"),
    COUNT(1, 1, true, "stop|start|<number>"),
    RULE(1, 2, true, argsBuilder(Arrays.stream(Rule.values()).map(x -> x.name().toLowerCase()).toArray(String[]::new), '|'), "true|false"),
    SETSCORE(1, 2, true, argsBuilder(Arrays.stream(TeamColor.values()).map(x -> x.name().toLowerCase()).toArray(String[]::new), '|'), "<number>"),
    JOINTEAM(1, 2, true, argsBuilder(Arrays.stream(TeamColor.values()).map(x -> x.name().toLowerCase()).toArray(String[]::new), '|'), "<onlinePlayer>"),
    LOCATIONS(2, 0, true),
    TPWORLD(2, 1, true, "<worldName>"),
    CREATEWORLD(2, 1, false, "<instanceName>|all"),
    BACKUPWORLD(2, 0, true),
    LOADWORLD(2, 1, true, "<worldName>"),
    SETREGION(2, 1, true, argsBuilder(Arrays.stream(Location.values()).map(x -> x.name().toLowerCase()).toArray(String[]::new), '|'), "*" + argsBuilder(Arrays.stream(TeamColor.values()).map(x -> x.name().toLowerCase()).toArray(String[]::new), '|')),
    HELP(2, 0, true),
    VAULTINFO(2, 0, true),
    RELOADCONFIG(2, 1, true, argsBuilder(Arrays.stream(ConfigType.values()).map(x -> x.name().toLowerCase()).toArray(String[]::new), '|')),
    TOOL(2, 1, true, "wand|refillChest");

    private final int permissionLevel;
    private final int numberOfNeededArguments;
    private final boolean playerExecutorOnly;
    private final String[] arguments;

    Subcommand(int permissionLevel, int numberOfNeededArguments, boolean playerExecutorOnly, String... arguments) {
        this.permissionLevel = permissionLevel;
        this.numberOfNeededArguments = numberOfNeededArguments;
        this.playerExecutorOnly = playerExecutorOnly;
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
        return numberOfNeededArguments <= args.length - 1 && arguments.length >= args.length - 1;
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

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public static int checkArgs(Subcommand subcommand, String[] args) {
        int i = 1;
        for (String currentArg : subcommand.getArguments()) {
            if (currentArg.charAt(0) == '*') //Optional argument is always at the end
                return 0;
            String[] currentArgSeparated = currentArg.split("\\|");
            boolean matches = false;
            for (String currentSubArg : currentArgSeparated) {
                if (!(currentSubArg.charAt(0) == '<')) {
                    if (currentSubArg.equalsIgnoreCase(args[i])) {
                        matches = true;
                        break;
                    }
                } else {
                    if (currentSubArg.equals("<number>")) {
                        if (isInteger(args[i]))
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
                return i; //Position of incorrect argument
        }
        return 0; //All arguments are correct
    }

}
