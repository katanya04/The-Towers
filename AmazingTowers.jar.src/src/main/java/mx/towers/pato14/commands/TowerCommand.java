package mx.towers.pato14.commands;

import me.katanya04.anotherguiplugin.actionItems.ActionItem;
import me.katanya04.anotherguiplugin.actionItems.MenuItem;
import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.tasks.Start;
import mx.towers.pato14.game.team.ITeam;
import mx.towers.pato14.game.team.TeamColor;
import mx.towers.pato14.utils.files.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.items.ItemsEnum;
import mx.towers.pato14.utils.items.Skulls;
import mx.towers.pato14.utils.locations.Locations;
import mx.towers.pato14.utils.mysql.Callback;
import mx.towers.pato14.utils.mysql.IConnexion;
import mx.towers.pato14.utils.rewards.SetupVault;
import mx.towers.pato14.utils.stats.Rank;
import mx.towers.pato14.utils.stats.StatType;
import mx.towers.pato14.utils.wand.WandCoords;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TowerCommand implements TabExecutor {
    private final ArrayList<CommandSender> senderPlayer = new ArrayList<>();
    private final LinkedHashMap<String, Long> cooldown = new LinkedHashMap<String, Long>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Long> eldest) {
            return size() > 50;
        }
    };
    @Override
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
            Utils.sendMessage("You don't have permission to execute this command.", MessageType.ERROR, sender);
            return true;
        }
        if (!subcommand.checkCorrectSender(sender)) {
            Utils.sendMessage("Only a player can execute this command.", MessageType.ERROR, sender);
            return true;
        }
        if (!subcommand.correctNumberOfArguments(args, sender)) {
            Utils.sendMessage(subcommand.getCorrectUse(numberOfTeams), MessageType.INFO, sender);
            return true;
        }
        if (args.length > 1) {
            Map.Entry<Integer, GameInstance> argErrorAndGameInstance = Subcommand.checkArgs(subcommand, args, numberOfTeams, sender);
            if (argErrorAndGameInstance.getKey() > 0) {
                Utils.sendMessage("Error on argument " + argErrorAndGameInstance.getKey(), MessageType.ERROR, sender);
                Utils.sendMessage(subcommand.getCorrectUse(numberOfTeams), MessageType.INFO, sender);
                return true;
            }
            if (argErrorAndGameInstance.getValue() != null)
                gameInstance = argErrorAndGameInstance.getValue();
        }
        if (subcommand.needsAGameInstance() && gameInstance == null) {
            Utils.sendMessage("An instance of a world has to be specified.", MessageType.ERROR, sender);
            return true;
        }
        AmazingTowers.logger.logTowersCommand(sender, subcommand, args);
        switch (subcommand) {
            case STATS:
                if (AmazingTowers.isConnectedToDatabase()) {
                    if (!cooldown.containsKey(sender.getName()) || System.currentTimeMillis() - cooldown.get(sender.getName()) > 3000) {
                        String tableName = IConnexion.ALL_TABLES;
                        if (args.length > 2 && AmazingTowers.connexion.isAValidTable(args[2]))
                            tableName = args[2];
                        Callback.findPlayerAsync(args[1], tableName, result -> {
                            if (result == null) {
                                Utils.sendMessage("Player wasn't found in the database", MessageType.WARNING, sender);
                            } else {
                                for (StatType statType : StatType.values()) {
                                    sender.sendMessage("§7" + statType.getText() + ": " + statType.getColor() + "§l" +
                                            result.getStat(statType));
                                }
                                Rank rank = Rank.getTotalRank(result);
                                sender.sendMessage("§7§lRANGO: " + rank.getColor() + "§l" + rank.name());
                            }
                        });
                        cooldown.put(sender.getName(), System.currentTimeMillis());
                    } else {
                        Utils.sendMessage("You have to wait " + "§6" + (3000 - (System.currentTimeMillis() - cooldown.get(sender.getName())))/1000 + " §4seconds before executing that command again.", MessageType.ERROR, sender);
                    }
                } else {
                    Utils.sendMessage("The database is disabled in the plugin configuration", MessageType.INFO, sender);
                }
                break;
            case SPECTATOR:
                assert gameInstance != null;
                assert player != null;
                if (player.getGameMode().equals(GameMode.SPECTATOR) && gameInstance.getGame().getTeams().getTeamByPlayer(player.getName()) == null)
                    gameInstance.getGame().spawn(player);
                else
                    Utils.sendMessage("You can only execute this command when on spectator mode and not being part of a team", MessageType.INFO, sender);
                break;
            case ORGANIZER:
                assert gameInstance != null;
                String password = AmazingTowers.getGlobalConfig().getString("permissions.password.organizer");
                if (password != null && !password.isEmpty() && args[1].equals(password)) {
                    PermissionAttachment organizer;
                    organizer = sender.addAttachment(AmazingTowers.getPlugin());
                    gameInstance.getPermissions().put(sender.getName(), organizer);
                    organizer.setPermission("towers.organizer", true);
                }
                break;
            case LOBBY:
                assert player != null;
                if (AmazingTowers.getLobby() != null)
                    Utils.tpToWorld(AmazingTowers.getLobby().getWorld(), player);
                else
                    Utils.sendMessage("A main lobby doesn't exist", MessageType.ERROR, sender);
                break;
            case COUNT:
                assert gameInstance != null;
                if (gameInstance.getGame().getGameState().ordinal() < 2) {
                    Start start = gameInstance.getGame().getStart();
                    if (args[1].equals("stop"))
                        start.stopCount();
                    else if (args[1].equals("start"))
                        start.continueFromCommand();
                    else
                        start.setCountDown(Integer.parseInt(args[1]));
                } else
                    Utils.sendMessage("This command can only be executed before the match start", MessageType.ERROR, sender);
                break;
            case RULE:
                assert gameInstance != null;
                Rule.setRuleValue(gameInstance, Rule.valueOf(args[1].toUpperCase()), Boolean.parseBoolean(args[2].toLowerCase()));
                Utils.sendMessage("Set " + args[1].toLowerCase() + " §rto §e" + args[2].toLowerCase(), MessageType.INFO, sender);
                break;
            case SETSCORE:
                assert gameInstance != null;
                if (gameInstance.getGame().getGameState().equals(GameState.GAME)) {
                    ITeam team = gameInstance.getGame().getTeams().getTeam(TeamColor.valueOf(args[1].toUpperCase()));
                    team.setPoints(Integer.parseInt(args[2]));
                    gameInstance.broadcastMessage(gameInstance.getConfig(ConfigType.MESSAGES).getString("scorePoint.setScoresCommand")
                            .replace("{Scores}", gameInstance.getGame().getTeams().scores()), true);
                    if (gameInstance.getGame().getTeams().checkWin(team.getTeamColor()))
                        gameInstance.getGame().getTeams().win(team.getTeamColor());
                } else
                    Utils.sendMessage("You can only execute this command during a match.", MessageType.ERROR, sender);
                break;
            case SETLIVES:
                assert gameInstance != null;
                if (gameInstance.getGame().getGameState().equals(GameState.GAME)) {
                    ITeam team = gameInstance.getGame().getTeams().getTeam(TeamColor.valueOf(args[1].toUpperCase()));
                    team.setLives(Integer.parseInt(args[2]));
                    gameInstance.broadcastMessage(gameInstance.getConfig(ConfigType.MESSAGES).getString("scorePoint.setScoresCommand")
                            .replace("{Scores}", gameInstance.getGame().getTeams().scores()), true);
                    if (gameInstance.getGame().getTeams().checkNoLives(team.getTeamColor()))
                        gameInstance.getGame().getTeams().loseRespawn(team.getTeamColor());
                } else
                    Utils.sendMessage("You can only execute this command during a match.", MessageType.ERROR, sender);
                break;
            case JOINTEAM:
                assert gameInstance != null;
                if (gameInstance.getGame().getGameState() == GameState.CAPTAINS_CHOOSE && gameInstance.getGame().getCaptainsPhase().isCaptain(args[1])) {
                    Utils.sendMessage("You can't move a captain to another team", MessageType.ERROR, sender);
                    break;
                }
                gameInstance.getGame().getCaptainsPhase().removePlayer(args[2]);
                TeamColor teamToJoin = TeamColor.valueOf(args[1].toUpperCase());
                gameInstance.getGame().getTeams().getTeam(teamToJoin).addPlayer(args[2]);
                Player p = Bukkit.getPlayer(args[2]);
                if (p != null && gameInstance.getGame().getPlayers().contains(p))
                    gameInstance.getGame().spawn(p);
                Utils.sendMessage("Moved " + args[2] + " to " + teamToJoin.getColor() + teamToJoin.getName(gameInstance), MessageType.INFO, sender);
                break;
            case LEAVETEAM:
                assert gameInstance != null;
                if (gameInstance.getGame().getGameState() == GameState.CAPTAINS_CHOOSE && gameInstance.getGame().getCaptainsPhase().isCaptain(args[1])) {
                    Utils.sendMessage("You can't remove a captain from its team", MessageType.ERROR, sender);
                    break;
                }
                ITeam teamToLeave = gameInstance.getGame().getTeams().getTeamByPlayer(args[1]);
                if (teamToLeave != null) {
                    teamToLeave.removePlayer(args[1]);
                    if (gameInstance.getGame().getGameState() == GameState.CAPTAINS_CHOOSE)
                        gameInstance.getGame().getCaptainsPhase().addPlayer(args[1]);
                    Utils.sendMessage("Removed player from team " + teamToLeave.getTeamColor().getColor() + teamToLeave.getTeamColor().getName(gameInstance), MessageType.INFO, sender);
                    Player playerLeave = Bukkit.getPlayer(args[1]);
                    if (playerLeave != null && gameInstance.getGame().getPlayers().contains(playerLeave))
                        gameInstance.getGame().spawn(playerLeave);
                }
                Utils.sendMessage("That player is not part of any team", MessageType.ERROR, sender);
                break;
            case TPWORLD:
                World worldDestination = Bukkit.getWorld(args[1]);
                if (args.length > 2 && PermissionLevel.hasPermission(PermissionLevel.ADMIN, PermissionLevel.getPermissionLevel(sender)))
                    player = Bukkit.getPlayer(args[2]);
                else if (player == null) {
                    Utils.sendMessage("You need to specify a player to teleport", MessageType.ERROR, sender);
                    break;
                }
                if (worldDestination != null) {
                    if (player != null) {
                        Utils.tpToWorld(worldDestination, player);
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
                    for (GameInstance gameInstance1 : AmazingTowers.getGameInstances()) {
                        if (gameInstance1.getWorld() != null || !(setAll || args[1].equals(gameInstance1.getInternalName())))
                            continue;
                        Utils.sendMessage("The world §a" + gameInstance1.getInternalName() + "§f was created§a successfully...", MessageType.INFO, sender);
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
                final File backup = new File(AmazingTowers.getPlugin().getDataFolder().getAbsolutePath() + "/backup", gameInstance.getWorld().getName());
                if (backup.exists())
                    Utils.sendMessage("§fThe folder §a'" + gameInstance.getWorld().getName() + "'§f already exists in the backup folder!", MessageType.ERROR, sender);
                else if (!this.senderPlayer.contains(sender)) {
                    this.senderPlayer.add(sender);
                    Utils.sendMessage("§fDo you want to save the world §a" + gameInstance.getInternalName() + "§f in the backup folder? ", MessageType.INFO, sender);
                    Utils.sendMessage("§fIf you want to save it, execute again the command §a/towers backupWorld", MessageType.INFO, sender);
                }
                else {
                    final File world3 = new File(Bukkit.getWorldContainer().getAbsolutePath(), gameInstance.getWorld().getName());
                    if (!world3.exists())
                        Utils.sendMessage("§fThe folder of the world §c" + gameInstance.getWorld().getName() + "§f doesn't exist", MessageType.ERROR, sender);
                    gameInstance.getWorld().save();
                    try {
                        FileUtils.copyDirectory(world3, backup);
                    } catch (IOException ex) {
                        Utils.sendMessage("I/O error when making the backup for " + world3, MessageType.ERROR, sender);
                        break;
                    }
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
                    Utils.sendMessage("A team color needs to be specified.", MessageType.ERROR, sender);
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
                        WandCoords wandCoords = AmazingTowers.getWandCoords(player);
                        if (!wandCoords.isSetPos1()) {
                            Utils.sendMessage("You need to set Pos1 first", MessageType.ERROR, sender);
                            break;
                        }
                        if (!wandCoords.isSetPos2()) {
                            Utils.sendMessage("You need to set Pos2 first", MessageType.ERROR, sender);
                            break;
                        }
                        corners.add(Locations.getLocationStringBlock(wandCoords.getPos1()));
                        corners.add(Locations.getLocationStringBlock(wandCoords.getPos2()));
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
                    List<Map<String, Object>> generators = locations.getList(path) == null ? new ArrayList<>() : locations
                            .getMapList(path).stream().map(o -> (Map<String, Object>) o).collect(Collectors.toList());
                    HashMap<String, Object> newGenerator = new HashMap<>();
                    newGenerator.put("item", player.getItemInHand());
                    newGenerator.put("coords", Locations.getLocationStringCenter(player.getLocation(), false));
                    generators.add(newGenerator);
                    locations.set(path, generators);
                    name = player.getItemInHand().getType().name().toLowerCase();
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
                    Utils.sendMessage("§cThe vault option is disabled in the plugin config", MessageType.INFO, sender);
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
            case SAVESETTINGS:
                assert gameInstance != null;
                gameInstance.getConfig(ConfigType.GAME_SETTINGS).saveConfig();
                gameInstance.getConfig(ConfigType.KITS).saveConfig();
                Utils.sendMessage("Game settings saved correctly", MessageType.INFO, sender);
                break;
            case BOOK:
                assert gameInstance != null;
                assert player != null;
                ((MenuItem<?, Object>) ActionItem.getByName(ItemsEnum.GAME_SETTINGS.name)).getMenu().openMenu(player);
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
                    ((MenuItem<?, Object>) ActionItem.getByName(ItemsEnum.KIT_SELECT.name)).getMenu().openMenu(player);
                else if (player.hasPermission(PermissionLevel.ADMIN.getPermissionName())) {
                    ((MenuItem<?, Object>) ActionItem.getByName(ItemsEnum.MODIFY_KITS.name)).getMenu().openMenu(player);
                } else
                    Utils.sendMessage("You don't have permission to execute this command.", MessageType.ERROR, sender);
                break;
            case SETDATABASE:
                assert gameInstance != null;
                if (args.length == 1) {
                    gameInstance.setTableName(null);
                    Utils.sendMessage("This match isn't linked to a database table now", MessageType.INFO, sender);
                } else if (AmazingTowers.connexion.isAValidTable(args[1])) {
                    gameInstance.setTableName(args[1]);
                    Utils.sendMessage("This match is now linked to the database table " + args[1], MessageType.INFO, sender);
                } else
                    Utils.sendMessage("No database table with that name exists", MessageType.INFO, sender);
                break;
            case ENDMATCH:
                assert gameInstance != null;
                if (args.length >= 2) {
                    if (TeamColor.isTeamColor(args[1]))
                        gameInstance.getGame().getFinish().fatality(TeamColor.valueOf(args[1].toUpperCase()));
                    else
                        Utils.sendMessage("That's not a valid team color", MessageType.ERROR, sender);
                    break;
                }
                gameInstance.getGame().endMatch();
                if (Objects.requireNonNull(gameInstance.getGame().getGameState()) == GameState.EXTRA_TIME)
                    Utils.sendMessage("Redo this action to finish the match definitively", MessageType.INFO, sender);
                else if (Objects.requireNonNull(gameInstance.getGame().getGameState()) != GameState.FINISH)
                    Utils.sendMessage("This action can only be done while a match is taking place", MessageType.ERROR, sender);
                break;
            case PICKS:
                if (gameInstance.getGame().getGameState().matchIsBeingPlayed || gameInstance.getGame().getGameState() == GameState.FINISH)
                    Utils.sendMessage("This command can only be executioned before a match", MessageType.ERROR, sender);
                else if (args[1].equalsIgnoreCase("add")) {
                    if (args.length < 3)
                        Utils.sendMessage("You need to specify a player", MessageType.ERROR, sender);
                    else
                        gameInstance.getGame().getCaptainsPhase().addPlayer(Arrays.copyOfRange(args, 2, args.length));
                } else if (args[1].equalsIgnoreCase("remove")) {
                    if (args.length < 3)
                        Utils.sendMessage("You need to specify a player", MessageType.ERROR, sender);
                    else
                        gameInstance.getGame().getCaptainsPhase().removePlayer(Arrays.copyOfRange(args, 2, args.length));
                } else if (args[1].equalsIgnoreCase("newCaptains")) {
                    gameInstance.getGame().getStart().startCaptainsChoose();
                    gameInstance.getGame().getStart().setCountDown(0);
                } else if (args[1].equalsIgnoreCase("reloadPicks")) {
                    gameInstance.getGame().getCaptainsPhase().setPlayerList(true);
                }
                break;
            case DEBUG:
                //player.getInventory().addItem(Skulls.getPlayerHead("katanya04"));
                player.getInventory().addItem(Skulls.getSkullFromURL("45587da7fe7336e8ab9f791ea5e2cfc8a827ca959567eb9d53a647babf948d5"));
                //player.getInventory().addItem(Skulls.getSkullFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDU1ODdkYTdmZTczMzZlOGFiOWY3OTFlYTVlMmNmYzhhODI3Y2E5NTk1NjdlYjlkNTNhNjQ3YmFiZjk0OGQ1In19fQ=="));
                break;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        List<String> autocomplete = new ArrayList<>();
        if (args.length == 1)
            autocomplete.addAll(Subcommand.getListAvailableSubcommand(commandSender));
        else {
            Subcommand subcommand = Subcommand.isValidSubcommand(args[0]);
            if (subcommand == null || !subcommand.hasPermission(commandSender))
                return null;
            GameInstance gameInstance = null;
            if (commandSender instanceof Entity)
                gameInstance = AmazingTowers.getGameInstance((Entity) commandSender);
            autocomplete.addAll(Objects.requireNonNull(subcommand.autocompleteArgs(args.length - 2,
                    gameInstance == null ? 8 : gameInstance.getNumberOfTeams())));
        }
        return autocomplete.stream().filter(o -> o.regionMatches(true, 0, args[args.length - 1], 0, args[args.length - 1].length())).collect(Collectors.toList());
    }
}