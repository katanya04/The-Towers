package mx.towers.pato14.commands;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.items.BookMenuItem;
import mx.towers.pato14.game.tasks.Start;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.locations.Locations;
import mx.towers.pato14.utils.mysql.FindOneCallback;
import mx.towers.pato14.utils.nms.ReflectionMethods;
import mx.towers.pato14.utils.rewards.SetupVault;
import mx.towers.pato14.utils.stats.Rank;
import mx.towers.pato14.utils.stats.StatType;
import mx.towers.pato14.utils.world.WorldReset;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class TowerCommand implements CommandExecutor
{
    private final AmazingTowers plugin = AmazingTowers.getPlugin();
    private final ArrayList<CommandSender> senderPlayer = new ArrayList<>();
    private final HashMap<String, Long> cooldown = new HashMap<>();

    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        GameInstance gameInstance = null;
        Player player = null;
        if (sender instanceof Entity) {
            gameInstance = AmazingTowers.getGameInstance((Entity) sender);
            if (sender instanceof Player)
                player = (Player) sender;
        }
        int numberOfTeams = gameInstance == null ? TeamColor.getMatchTeams(TeamColor.values().length).size() : gameInstance.getNumberOfTeams();
        Subcommand subcommand;
        if (args.length < 1  || (subcommand = Subcommand.isValidSubcommand(args[0])) == null || args[0].equalsIgnoreCase(Subcommand.HELP.name())) {
            sender.sendMessage(Subcommand.listOfCommands(numberOfTeams, sender));
            return true;
        }
        if (!subcommand.hasPermission(sender)) {
            Utils.sendMessage("No tienes permiso para ejecutar este comando.", MessageType.ERROR, sender);
            return true;
        }
        if (!subcommand.checkCorrectSender(sender)) {
            Utils.sendMessage("Solo un jugador puede ejecutar este comando.", MessageType.ERROR, sender);
            return true;
        }
        if (!subcommand.correctNumberOfArguments(args, sender)) {
            Utils.sendMessage(subcommand.getCorrectUse(numberOfTeams), MessageType.INFO, sender);
            return true;
        }
        if (args.length > 1) {
            Map.Entry<Integer, GameInstance> argErrorAndGameInstance = Subcommand.checkArgs(subcommand, args, numberOfTeams, sender);
            if (argErrorAndGameInstance.getKey() > 0) {
                Utils.sendMessage("Error en el argumento " + argErrorAndGameInstance.getKey(), MessageType.ERROR, sender);
                Utils.sendMessage(subcommand.getCorrectUse(numberOfTeams), MessageType.INFO, sender);
                return true;
            }
            if (argErrorAndGameInstance.getValue() != null)
                gameInstance = argErrorAndGameInstance.getValue();
        }
        if (subcommand.needsAGameInstance() && gameInstance == null) {
            Utils.sendMessage("Se necesita especificar una instancia de The Towers.", MessageType.ERROR, sender);
            return true;
        }
        switch (subcommand) {
            case STATS:
                if (AmazingTowers.isConnectedToDatabase()) {
                    if (!cooldown.containsKey(sender.getName()) || System.currentTimeMillis() - cooldown.get(sender.getName()) > 3000) {
                        FindOneCallback.findPlayerAsync(args[1], this.plugin, result -> {
                            if (result == null) {
                                Utils.sendMessage("No se ha encontrado ese jugador", MessageType.WARNING, sender);
                            } else {
                                for (StatType statType : StatType.values()) {
                                    sender.sendMessage("§7" + statType.getText() + ": " + statType.getColor() + "§l" +
                                            result[statType.getIndex() - 3]);
                                }
                                sender.sendMessage("§7§lRANGO: " + Rank.getTotalRank(result).toText());
                            }
                        });
                        cooldown.put(sender.getName(), System.currentTimeMillis());
                    } else {
                        Utils.sendMessage("Tienes que esperar " + "§6" + (3000 - (System.currentTimeMillis() - cooldown.get(sender.getName())))/1000 + " §4segundos antes de poder ejecutar este comando.", MessageType.ERROR, sender);
                    }
                } else {
                    Utils.sendMessage("La base de datos está desactivada en la configuración del plugin", MessageType.INFO, sender);
                }
                break;
            case SPECTATOR:
                assert gameInstance != null;
                assert player != null;
                if (player.getGameMode().equals(GameMode.SPECTATOR) &&
                        !gameInstance.getGame().getTeams().containsNoRespawnPlayer(player.getName()))
                    Utils.joinGame(player);
                else
                    Utils.sendMessage("Solo puedes ejecutar este comando estando en modo espectador y sin ser parte de ningún equipo", MessageType.INFO, sender);
                break;
            case ORGANIZER:
                assert gameInstance != null;
                String password = AmazingTowers.getGlobalConfig().getString("permissions.password.organizer");
                if (password != null && !password.isEmpty() && args[1].equals(password)) {
                    PermissionAttachment organizer;
                    organizer = sender.addAttachment(this.plugin);
                    gameInstance.getPermissions().put(sender.getName(), organizer);
                    organizer.setPermission("towers.organizer", true);
                }
                break;
            case LOBBY:
                assert player != null;
                if (AmazingTowers.getLobby() != null)
                    Utils.tpToWorld(AmazingTowers.getLobby().getWorld(), player);
                else
                    Utils.sendMessage("No existe lobby principal", MessageType.ERROR, sender);
                break;
            case COUNT:
                assert gameInstance != null;
                if (gameInstance.getGame().getGameState().ordinal() < 2) {
                    Start start = gameInstance.getGame().getStart();
                    if (args[1].equals("stop"))
                        start.stopCount();
                    else if (args[1].equals("start")) {
                        if (!start.hasStarted()) {
                            gameInstance.getGame().setGameState(GameState.PREGAME);
                            start.setRunFromCommand(true);
                            start.setHasStarted(true);
                            start.gameStart();
                        }
                        start.continueCount();
                    } else
                        start.setSeconds(Integer.parseInt(args[1]));
                } else
                    Utils.sendMessage("Este comando solo se puede usar antes de empezar la partida", MessageType.ERROR, sender);
                break;
            case RULE:
                assert gameInstance != null;
                gameInstance.getRules().replace(Rule.valueOf(args[1].toUpperCase()), Boolean.parseBoolean(args[2].toLowerCase()));
                gameInstance.getConfig(ConfigType.GAME_SETTINGS).set("rules." + Utils.macroCaseToCamelCase(args[1].toUpperCase()), Boolean.parseBoolean(args[2].toLowerCase()));
                Utils.sendMessage("Set " + args[1].toLowerCase() + " §rto §e" + args[2].toLowerCase(), MessageType.INFO, sender);
                break;
            case SETSCORE:
                assert gameInstance != null;
                if (gameInstance.getGame().getGameState().equals(GameState.GAME)) {
                    Team team = gameInstance.getGame().getTeams().getTeam(TeamColor.valueOf(args[1].toUpperCase()));
                    team.setPoints(Integer.parseInt(args[2]));
                    gameInstance.broadcastMessage(gameInstance.getConfig(ConfigType.MESSAGES).getString("scorePoint.setScoresCommand")
                            .replace("{Scores}", gameInstance.getGame().getTeams().scores()), true);
                    int pointsToWin = gameInstance.getConfig(ConfigType.CONFIG).getInt("options.pointsToWin");
                    if (team.getPoints() >= pointsToWin && !gameInstance.getRules().get(Rule.BEDWARS_STYLE)) {
                        gameInstance.getGame().getFinish().Fatality(team.getTeamColor());
                        gameInstance.getGame().setGameState(GameState.FINISH);
                    }
                } else
                    Utils.sendMessage("You can only execute this command during a match.", MessageType.ERROR, sender);
                break;
            case JOINTEAM:
                assert gameInstance != null;
                Player p = Bukkit.getPlayer(args[2]);
                if (p != null && gameInstance.getGame().getPlayers().contains(p)) {
                    gameInstance.getGame().getTeams().getTeam(TeamColor.valueOf(args[1].toUpperCase())).addPlayer(p.getName());
                    Utils.joinGame(p);
                } else
                    Utils.sendMessage("Ese jugador no está en esta partida.", MessageType.ERROR, sender);
                break;
            case TPWORLD:
                World worldDestination = Bukkit.getWorld(args[1]);
                Player player1 = Bukkit.getPlayer(args[2]);
                if (worldDestination != null) {
                    if (player1 != null) {
                        Utils.tpToWorld(worldDestination, player1);
                        Utils.sendMessage("Teleportation to the world §a" + args[1] + " successfully", MessageType.INFO, sender);
                    } else
                        Utils.sendMessage("The player §a" + args[1] + " isn't online right now", MessageType.ERROR, sender);
                } else
                    Utils.sendMessage("§fThe world §a" + args[1] + "§f doesn't exist or is not loaded (/towers loadWorld <worldName>)", MessageType.ERROR, sender);
                break;
            case CREATEWORLD:
                boolean success = false;
                boolean setAll = args[1].equalsIgnoreCase("all");
                if (Bukkit.getWorld(args[1]) != null)
                    Utils.sendMessage("§fThe world §c" + args[1] + "§f already exists", MessageType.ERROR, sender);
                else {
                    for (GameInstance gameInstance1 : AmazingTowers.getGameInstances().values()) {
                        if (gameInstance1.getWorld() != null || !(setAll || args[1].equals(gameInstance1.getName())))
                            continue;
                        Utils.sendMessage("The world §a" + gameInstance1.getName() + "§f was created§a successfully...", MessageType.INFO, sender);
                        success = true;
                    }
                }
                if (!success) {
                    if (setAll)
                        Utils.sendMessage("All instances already have a world linked to them.", MessageType.INFO, sender);
                    else
                        Utils.sendMessage("That instance doesn't exist.", MessageType.ERROR, sender);
                } else
                    Utils.sendMessage("To go to another world, use /tt tpWorld <worldName>", MessageType.INFO, sender);
                break;
            case BACKUPWORLD:
                assert gameInstance != null;
                final File backup = new File(this.plugin.getDataFolder().getAbsolutePath() + "/backup", gameInstance.getWorld().getName());
                if (backup.exists())
                    Utils.sendMessage("§fThe folder §a'" + gameInstance.getWorld().getName() + "'§f already exists in the backup folder!", MessageType.ERROR, sender);
                else if (!this.senderPlayer.contains(sender)) {
                    this.senderPlayer.add(sender);
                    Utils.sendMessage("§fDo you want to save the world §a" + gameInstance.getName() + "§f in the backup folder? ", MessageType.INFO, sender);
                    Utils.sendMessage("§fIf you want to save it, execute again the command §a/towers backupWorld", MessageType.INFO, sender);
                }
                else {
                    final File world3 = new File(Bukkit.getWorldContainer().getAbsolutePath(), gameInstance.getWorld().getName());
                    if (!world3.exists())
                        Utils.sendMessage("§fThe folder of the world §c" + gameInstance.getWorld().getName() + "§f doesn't exist", MessageType.ERROR, sender);
                    gameInstance.getWorld().save();
                    WorldReset.copyWorld(world3, backup);
                    final File[] ficheros = backup.listFiles();
                    if (ficheros == null) {
                        Utils.sendMessage("Error while trying to do a backup of the world", MessageType.ERROR, sender);
                        break;
                    }
                    for (File fichero : ficheros) {
                        if (fichero.getName().equals("session.lock") || fichero.getName().equals("uid.dat")) {
                            if (!fichero.delete()) {
                                Utils.sendMessage("Error while trying to do a backup of the world", MessageType.ERROR, sender);
                                break;
                            }
                        }
                    }
                    Utils.sendMessage("§fThe folder §a" + gameInstance.getWorld().getName() + "§f was copied to the backup folder§a successfully", MessageType.INFO, sender);
                    this.senderPlayer.remove(sender);
                }
                break;
            case LOADWORLD:
                final File world2 = new File(Bukkit.getWorldContainer().getAbsolutePath(), args[1]);
                if (!world2.exists())
                    Utils.sendMessage("§fThe folder of the world §c" + args[1] + "§f doesn't exist", MessageType.ERROR, sender);
                else if (Bukkit.getWorld(args[1]) == null) {
                    Utils.sendMessage("Loading the world §a" + args[1], MessageType.INFO, sender);
                    new WorldCreator(args[1]).createWorld();
                    Utils.sendMessage("The world §a" + args[1] + "§f loaded§a successfully", MessageType.INFO, sender);
                } else
                    Utils.sendMessage("§fThe world §a" + args[1] + "§f is already loaded!", MessageType.INFO, sender);
                break;
            case SETREGION:
                assert player != null;
                assert gameInstance != null;
                final Location loc = Location.valueOf(args[1].toUpperCase());
                final TeamColor teamColor = args.length < 3 || !TeamColor.isTeamColor(args[2]) ? null : TeamColor.valueOf(args[2].toUpperCase());
                if (loc.needsTeamColor() && teamColor == null) {
                    Utils.sendMessage("Hace falta especificar un color de equipo.", MessageType.ERROR, sender);
                    break;
                }
                LocationType locationType = loc.getLocationType();
                final Config locations = gameInstance.getConfig(ConfigType.LOCATIONS);
                final String path = loc.getPath(teamColor);
                String name;
                if (!locationType.equals(LocationType.GENERATOR)) {
                    if (locationType.equals(LocationType.POINT)) {
                        if (loc.isList()) {
                            List<String> list = locations.getStringList(path);
                            list.add(Locations.getLocationStringCenter(player.getLocation(), true));
                            locations.set(path, list);
                        } else
                            locations.set(path, Locations.getLocationStringCenter(player.getLocation(), true));
                    } else {
                        List<String> corners = new ArrayList<>();
                        corners.add(this.plugin.getWand().getPos1());
                        corners.add(this.plugin.getWand().getPos2());
                        if (loc.isList()) {
                            @SuppressWarnings("unchecked")
                            List<List<String>> list = locations.getList(path) == null ? new ArrayList<>() :
                                    locations.getList(path).stream().filter(o -> o instanceof List).map(o -> (List<String>) o)
                                            .collect(Collectors.toList());
                            list.add(corners);
                            locations.set(path, list);
                        } else
                            locations.set(path, corners);
                    }
                    name = loc.getName(teamColor);
                } else {
                    @SuppressWarnings("unchecked")
                    List<Map<String, String>> generators = locations.getList(path) == null ? new ArrayList<>() : locations
                            .getMapList(path).stream().map(o -> (Map<String, String>) o).collect(Collectors.toList());
                    HashMap<String, String> newGenerator = new HashMap<>();
                    newGenerator.put("item", ReflectionMethods.serializeItemStack(player.getItemInHand()));
                    newGenerator.put("coords", Locations.getLocationStringCenter(player.getLocation(), false));
                    generators.add(newGenerator);
                    locations.set(path, generators);
                    name = player.getItemInHand().getItemMeta().getDisplayName();
                }
                Utils.sendMessage("§7Defined " + locationType.toString().toLowerCase() + " of§a " + name, MessageType.INFO, sender);
                locations.saveConfig();
                break;
            case HELP:
                sender.sendMessage(Subcommand.listOfCommands(numberOfTeams, sender));
            case VAULTINFO:
                if (Bukkit.getPluginManager().getPlugin("Vault") == null)
                    Utils.sendMessage("§cThe vault plugin doesn't exist", MessageType.INFO, sender);
                else if (AmazingTowers.getGlobalConfig().getBoolean("options.rewards.vault")) {
                    final String format = ChatColor.GRAY + "%s: [%s]";
                    Utils.sendMessage("§7*--------------*", MessageType.NO_PREFIX, sender);
                    Utils.sendMessage(" §f*Vault* ", MessageType.NO_PREFIX, sender);
                    Utils.sendMessage((SetupVault.getVaultEconomy() != null) ? String.format(format, "Economy", SetupVault.getVaultEconomy().getName()) : String.format(format, "Economy", "NONE"), MessageType.NO_PREFIX, sender);
                    Utils.sendMessage((SetupVault.getVaultChat() != null) ? String.format(format, "Chat", SetupVault.getVaultChat().getName()) : String.format(format, "Chat", "NONE"), MessageType.NO_PREFIX, sender);
                    Utils.sendMessage("§7*--------------*", MessageType.NO_PREFIX, sender);
                } else
                    Utils.sendMessage("§cThe vault option is inactive in the config", MessageType.INFO, sender);
                break;
            case RELOADCONFIG:
                assert gameInstance != null;
                for (ConfigType configType : ConfigType.values()) {
                    if (args[1].equalsIgnoreCase(configType.toString())) {
                        gameInstance.getConfig(configType).reloadConfig();
                        Utils.sendMessage("§aReloaded " +  configType.toString().toLowerCase() + " config successfully", MessageType.INFO, sender);
                        break;
                    }
                }
                break;
            case TOOL:
                assert player != null;
                for (Tool tool : Tool.values()) {
                    if (args[1].equalsIgnoreCase(tool.toString())) {
                        player.getInventory().addItem(tool.getItem().clone());
                        Utils.sendMessage(tool.getMsg(), MessageType.INFO, sender);
                        break;
                    }
                }
                break;
            case TIMER:
                assert gameInstance != null;
                if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")) {
                    gameInstance.getConfig(ConfigType.GAME_SETTINGS).set("timer.activated", args[1].toLowerCase());
                    Utils.sendMessage("Timer turned§f§l " + (Boolean.parseBoolean(args[1].toLowerCase()) ? "on" : "off"), MessageType.INFO, sender);
                } else {
                    gameInstance.getConfig(ConfigType.GAME_SETTINGS).set("timer.time", Utils.intTimeToString(Integer.parseInt(args[1])));
                    Utils.sendMessage("Time set to§f§l " + Utils.intTimeToString(Integer.parseInt(args[1])), MessageType.INFO, sender);
                }
                gameInstance.getGame().getTimer().update(gameInstance);
                break;
            case WHITELIST:
            case BLACKLIST:
                assert gameInstance != null;
                List<String> players = gameInstance.getConfig(ConfigType.GAME_SETTINGS).getStringList(args[0].toLowerCase() + ".players");
                if (args[1].equalsIgnoreCase("list")) {
                    Utils.sendMessage("§7*--------------*", MessageType.NO_PREFIX, sender);
                    Utils.sendMessage(" §f*" + Utils.firstCapitalized(args[0].toLowerCase()) + "* (" + (Boolean.parseBoolean(gameInstance.getConfig(ConfigType.GAME_SETTINGS).getString(args[0].toLowerCase() + ".activated")) ? "§aOn" + "§f)" : "§cOff" + "§f)"), MessageType.NO_PREFIX, sender);
                    if (players == null || players.isEmpty())
                        Utils.sendMessage(" §7(Empty)", MessageType.NO_PREFIX, sender);
                    for (String pl : gameInstance.getConfig(ConfigType.GAME_SETTINGS).getStringList(args[0].toLowerCase() + ".players")) {
                        Utils.sendMessage(" §7" + pl, MessageType.NO_PREFIX, sender);
                    }
                    Utils.sendMessage("§7*--------------*", MessageType.NO_PREFIX, sender);
                } else if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")) {
                    gameInstance.getConfig(ConfigType.GAME_SETTINGS).set(args[0].toLowerCase() + ".activated", args[1].toLowerCase());
                    Utils.sendMessage(Utils.firstCapitalized(args[0].toLowerCase()) + " turned§f§l " + (Boolean.parseBoolean(args[1].toLowerCase()) ? "on" : "off"), MessageType.INFO, sender);
                    gameInstance.updateLists();
                } else {
                    if (args.length < 3)
                        Utils.sendMessage("You need to specify a player", MessageType.ERROR, sender);
                    else if (args[1].equalsIgnoreCase("add")) {
                        if (!players.contains(args[2])) {
                            players.add(args[2]);
                            gameInstance.getConfig(ConfigType.GAME_SETTINGS).set(args[0].toLowerCase() + ".players", players);
                            Utils.sendMessage("Player successfully added to the " + args[0].toLowerCase(), MessageType.INFO, sender);
                            gameInstance.updateLists();
                        } else
                            Utils.sendMessage("That player is already in the " + args[0].toLowerCase(), MessageType.INFO, sender);
                    } else if (args[1].equalsIgnoreCase("remove")) {
                        if (players.contains(args[2])) {
                            players.remove(args[2]);
                            gameInstance.getConfig(ConfigType.GAME_SETTINGS).set(args[0].toLowerCase() + ".players", players);
                            Utils.sendMessage("Player successfully removed from the " + args[0].toLowerCase(), MessageType.INFO, sender);
                        } else
                            Utils.sendMessage("That player isn't in the " + args[0].toLowerCase(), MessageType.INFO, sender);
                        gameInstance.updateLists();
                    }
                }
                break;
            case MODIFYSETTING:
                assert gameInstance != null;
                assert player != null;
                String[] argSplit = args[1].split(";");
                Config settings = gameInstance.getConfig(ConfigType.valueOf(Utils.camelCaseToMacroCase(argSplit[0])));
                BookMenuItem currentMenu = gameInstance.getHotbarItems().getBookMenu(argSplit[0] + ";" + argSplit[1].split("\\.")[0]);
                if (argSplit.length == 2) {
                    if (args.length < 3) { //gameSettings;timer.time
                        String currentValue = settings.getString(argSplit[1]);
                        if (currentValue.equalsIgnoreCase("true")) {
                            settings.set(argSplit[1], "false");
                            currentMenu.updateSettings(gameInstance, args[1]);
                            currentMenu.openMenu(player);
                        } else if (currentValue.equalsIgnoreCase("false")) {
                            settings.set(argSplit[1], "true");
                            currentMenu.updateSettings(gameInstance, args[1]);
                            currentMenu.openMenu(player);
                        } else {
                            currentMenu.modifyConfigString(player, args[1]);
                        }
                    } else { //gameSettings;whitelist.players add
                        if (args[2].equalsIgnoreCase("add")) {
                            currentMenu.modifyConfigString(player, args[1]);
                        } else if (args[2].equalsIgnoreCase("remove")) {
                            Object currentValue = settings.get(argSplit[1]);
                            if (currentValue instanceof ConfigurationSection) { // kits;Kits.test remove
                                settings.set(((ConfigurationSection) currentValue).getCurrentPath(), null);
                            }
                            currentMenu.updateSettings(gameInstance, args[1] + " " + args[2]);
                            currentMenu.openMenu(player);
                        }
                    }
                } else if (argSplit.length == 3) { //gameSettings;whitelist.players;Marco2124 remove
                    Object currentValue = settings.get(argSplit[1]);
                    if (args[2].equalsIgnoreCase("remove")) {
                        if (currentValue instanceof Collection) {
                            ((Collection<?>) currentValue).remove(argSplit[2]);
                            settings.set(argSplit[1], currentValue);
                        }
                        currentMenu.updateSettings(gameInstance, args[1] + " " + args[2]);
                        currentMenu.openMenu(player);
                    }
                } else
                    Utils.sendMessage("Incorrect path", MessageType.ERROR, sender);
                break;
            case SAVESETTINGS:
                assert gameInstance != null;
                gameInstance.getConfig(ConfigType.GAME_SETTINGS).saveConfig();
                gameInstance.getConfig(ConfigType.KITS).saveConfig();
                Utils.sendMessage("Game settings saved correctly", MessageType.INFO, sender);
                break;
            case BOOK:
                assert gameInstance != null;
                assert player != null;
                player.getInventory().addItem(gameInstance.getHotbarItems().getModifyGameSettings());
                Utils.sendMessage("Gave settings book to §f" + player.getName(), MessageType.INFO, sender);
                break;
            case PARKOURPRIZE:
                Player player2 = Bukkit.getPlayer(args[1]);
                ItemStack prize = AmazingTowers.getLobby().getLobbyParkourPrize();
                if (player2 == null)
                    Utils.sendMessage("The player " + args[1] + " isn't online right now", MessageType.ERROR, sender);
                else if (!player2.getInventory().contains(prize))
                    player2.getInventory().addItem(prize);
                break;
            case KITS:
                assert gameInstance != null;
                assert player != null;
                if (args.length < 2)
                    gameInstance.getHotbarItems().getSelectKit().interact(player, gameInstance);
                else if (player.hasPermission(PermissionLevel.ADMIN.getPermissionName())) {
                        ReflectionMethods.openBook(player, gameInstance.getHotbarItems().getModifyGameSettings().getModifyKits());
                } else
                    Utils.sendMessage("No tienes permiso para ejecutar este comando.", MessageType.ERROR, sender);
        }
        return false;
    }
}