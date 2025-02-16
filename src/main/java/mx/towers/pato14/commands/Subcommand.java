package mx.towers.pato14.commands;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.team.TeamColor;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.mysql.IConnexion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public enum Subcommand { //* = optional argument, always at the end if it exists. $ = argument needed if run from the console
    STATS(PermissionLevel.NONE, 1, false, false, "<player>", "*<tableName>"),
    SPECTATOR(PermissionLevel.NONE, 0, true, true),
    ORGANIZER(PermissionLevel.NONE, 1, true, true, "<password>", "$<instanceName>"),
    LOBBY(PermissionLevel.NONE, 0, true, false),
    COUNT(PermissionLevel.ORGANIZER, 1, false, true, "stop|start|<number>", "$<instanceName>"),
    RULE(PermissionLevel.ORGANIZER, 2, false, true, argsBuilder(Arrays.stream(Rule.values()).map(x -> x.name().toLowerCase()).toArray(String[]::new), '|'), "true|false", "$<instanceName>"),
    SETSCORE(PermissionLevel.ORGANIZER, 2, false, true, "%team_colors%", "<number>", "$<instanceName>"),
    SETLIVES(PermissionLevel.ORGANIZER, 2, false, true, "%team_colors%", "<number>", "$<instanceName>"),
    JOINTEAM(PermissionLevel.ORGANIZER, 2, false, true, "%team_colors%", "<onlinePlayer>", "$<instanceName>"),
    LEAVETEAM(PermissionLevel.ORGANIZER, 1, false, true, "<onlinePlayer>", "$<instanceName>"),
    TPWORLD(PermissionLevel.NONE, 1, false, false, "<worldName>", "*<onlinePlayer>"),
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
    SAVESETTINGS(PermissionLevel.ORGANIZER, 0, false, true, "$<instanceName>"),
    BOOK(PermissionLevel.ORGANIZER, 0, true, true),
    PARKOURPRIZE(PermissionLevel.ADMIN, 1, false, false, "<player>"),
    KITS(PermissionLevel.NONE, 0, true, true, "*modify"),
    SETDATABASE(PermissionLevel.ORGANIZER, 0, true, true, "*<tableName>"),
    ENDMATCH(PermissionLevel.ORGANIZER, 0, true, true, "*%team_colors%"),
    PICKS(PermissionLevel.ORGANIZER, 1, true, true, "add|remove|newCaptains|reloadPicks", "*<player>"),
    BUILD(PermissionLevel.ORGANIZER, 0, true, false),
    DEBUG(PermissionLevel.ADMIN, 0, true, false);

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

    public PermissionLevel getPermissionLevel() {
        return permissionLevel;
    }

    public String[] getArguments() {
        return arguments;
    }

    public static String argsBuilder(String[] array, char separator) {
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

    public static List<String> getListAvailableSubcommand(CommandSender commandSender) {
        return Arrays.stream(Subcommand.values()).filter(o -> o.hasPermission(commandSender))
                .map(o -> o.toString().toLowerCase()).collect(Collectors.toList());
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
        if (this.arguments.length > 0 && this.arguments[this.arguments.length - 1].contains("..."))
            return true;
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
                    switch (currentSubArg) {
                        case "<number>":
                            if (Utils.isInteger(args[i]))
                                matches = true;
                            break;
                        case "<instanceName>":
                            if ((gameInstance = AmazingTowers.getGameInstance(Bukkit.getWorld(args[i]))) != null)
                                matches = true;
                            break;
                        case "<tableName>":
                            if (AmazingTowers.connexion.isAValidTable(args[1]))
                                matches = true;
                            break;
                        default:
                            matches = true;
                            break;
                    }
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

    public List<String> autocompleteArgs(int argNumber, int numberOfTeams) {
        if (this.arguments.length <= argNumber)
            return null;
        String fullArg = arguments[argNumber];
        if (fullArg.charAt(0) == '*' || fullArg.charAt(0) == '$')
            fullArg = fullArg.substring(1);
        Set<String> toret = new HashSet<>();
        String[] args = fullArg.split("\\|");
        for (String arg : args) {
            switch (arg) {
                case "<player>":
                case "<onlinePlayer>":
                    toret.addAll(AmazingTowers.getAllOnlinePlayers().stream().map(Player::getName).collect(Collectors.toSet()));
                    break;
                case "<tableName>":
                    toret.addAll(AmazingTowers.connexion.getTables());
                    toret.add(IConnexion.ALL_TABLES);
                    break;
                case "<worldName>":
                    toret.addAll(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toSet()));
                case "<instanceName>":
                    toret.addAll(Arrays.stream(AmazingTowers.getGameInstances()).map(TowersWorldInstance::getInternalName)
                            .collect(Collectors.toList()));
                    break;
                case "%team_colors%":
                    toret.addAll(TeamColor.getMatchTeams(numberOfTeams).stream().map(o -> o.toString().toLowerCase()).collect(Collectors.toSet()));
                    break;
                default:
                    toret.add(arg);
            }
        }
        return new ArrayList<>(toret);
    }
}
