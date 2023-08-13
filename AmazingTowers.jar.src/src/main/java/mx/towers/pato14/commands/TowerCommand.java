package mx.towers.pato14.commands;

import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.tasks.Start;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.enums.Location;
import mx.towers.pato14.utils.mysql.FindOneCallback;
import org.bukkit.*;
import mx.towers.pato14.utils.world.WorldReset;
import mx.towers.pato14.utils.rewards.SetupVault;
import net.md_5.bungee.api.ChatColor;
import mx.towers.pato14.utils.locations.Locations;
import java.io.File;

import mx.towers.pato14.game.utils.Dar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import mx.towers.pato14.AmazingTowers;
import org.bukkit.command.CommandExecutor;
import org.bukkit.permissions.PermissionAttachment;

public class TowerCommand implements CommandExecutor
{
    private final AmazingTowers plugin = AmazingTowers.getPlugin();
    private final ArrayList<CommandSender> senderPlayer = new ArrayList<>();
    private final HashMap<String, Long> cooldown = new HashMap<>();

    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        GameInstance gameInstance = null;
        Player player = null;
        if (sender instanceof Entity) {
            gameInstance = this.plugin.getGameInstance((Entity) sender);
            if (sender instanceof Player)
                player = (Player) sender;
        }
        int numberOfTeams = gameInstance == null ? TeamColor.getMatchTeams(TeamColor.values().length).size() : gameInstance.getNumberOfTeams();
        Subcommand subcommand;
        if (args.length < 1  || (subcommand = Subcommand.isValidSubcommand(args[0])) == null || args[0].equalsIgnoreCase(Subcommand.HELP.name())) {
            sender.sendMessage(Subcommand.listOfCommands(numberOfTeams));
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
                if (this.plugin.getGlobalConfig().getBoolean("options.mysql.active")) {
                    if (!cooldown.containsKey(sender.getName()) || System.currentTimeMillis() - cooldown.get(sender.getName()) > 3000) {
                        FindOneCallback.findPlayerAsync(args[1], this.plugin, result -> {
                            if (result == null) {
                                Utils.sendMessage("No se ha encontrado ese jugador", MessageType.WARNING, sender);
                            } else {
                                for (StatType statType : StatType.values())
                                    sender.sendMessage("§7" + statType.getText() + ": " + statType.getColor() + "§l" +
                                            result[statType.getIndex() - 3]);
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
                if (sender instanceof Player && ((Player) sender).getGameMode().equals(GameMode.SPECTATOR) &&
                        !gameInstance.getGame().getTeams().containsNoRespawnPlayer(sender.getName()))
                    Dar.DarItemsJoin((Player) sender, GameMode.ADVENTURE);
                else
                    Utils.sendMessage("Solo puedes ejecutar este comando estando en modo espectador y sin ser parte de ningún equipo", MessageType.INFO, sender);
                break;
            case ORGANIZER:
                assert gameInstance != null;
                String password = this.plugin.getGlobalConfig().getString("permissions.password.organizer");
                if (password != null && !password.isEmpty() && args[1].equals(password)) {
                    PermissionAttachment organizer;
                    organizer = sender.addAttachment(this.plugin);
                    gameInstance.getPermissions().put(sender.getName(), organizer);
                    organizer.setPermission("towers.organizer", true);
                }
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
                    Team pTeam = gameInstance.getGame().getTeams().getTeamByPlayer(p.getName());
                    if (pTeam != null)
                       pTeam.removePlayer(p);
                    gameInstance.getGame().getTeams().getTeam(TeamColor.valueOf(args[1].toUpperCase())).setPlayerState(p.getName(), PlayerState.ONLINE);
                    Dar.darItemsJoinTeam(p);
                } else
                    Utils.sendMessage("Ese jugador no está en esta partida.", MessageType.ERROR, sender);
                break;
            case TPWORLD:
                assert player != null;
                World worldDestination = Bukkit.getWorld(args[1]);
                if (worldDestination != null) {
                    Utils.tpToWorld(worldDestination, player);
                    Utils.sendMessage("Teleportation to the world §a" + args[1] + " successfully", MessageType.INFO, sender);
                } else
                    Utils.sendMessage("§fThe world §a" + args[1] + "§f doesn't exist", MessageType.ERROR, sender);
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
                        gameInstance1.linkWorld(Utils.createEmptyWorld(gameInstance1.getName()));
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
                    newGenerator.put("item", plugin.getNms().serializeItemStack(player.getItemInHand()));
                    newGenerator.put("coords", Locations.getLocationStringCenter(player.getLocation(), true));
                    generators.add(newGenerator);
                    locations.set(path, generators);
                    name = player.getItemInHand().getItemMeta().getDisplayName();
                }
                Utils.sendMessage("§7Defined " + locationType.toString().toLowerCase() + " of§a " + name, MessageType.INFO, sender);
                locations.saveConfig();
                break;
            case HELP:
                sender.sendMessage(Subcommand.listOfCommands(numberOfTeams));
            case VAULTINFO:
                if (Bukkit.getPluginManager().getPlugin("Vault") == null)
                    Utils.sendMessage("§cThe vault plugin doesn't exist", MessageType.INFO, sender);
                else if (this.plugin.getGlobalConfig().getBoolean("options.rewards.vault")) {
                    final String format = ChatColor.GRAY + "%s: [%s]";
                    Utils.sendMessage("§7*--------------*", MessageType.INFO, sender);
                    Utils.sendMessage(" §f*Vault* ", MessageType.INFO, sender);
                    Utils.sendMessage((SetupVault.getVaultEconomy() != null) ? String.format(format, "Economy", SetupVault.getVaultEconomy().getName()) : String.format(format, "Economy", "NONE"), MessageType.INFO, sender);
                    Utils.sendMessage((SetupVault.getVaultChat() != null) ? String.format(format, "Chat", SetupVault.getVaultChat().getName()) : String.format(format, "Chat", "NONE"), MessageType.INFO, sender);
                    Utils.sendMessage("§7*--------------*", MessageType.INFO, sender);
                } else
                    Utils.sendMessage("§cThe vault option is inactive in the config", MessageType.INFO, sender);
                break;
            case RELOADCONFIG:
                assert gameInstance != null;
                for (ConfigType configType : ConfigType.values()) {
                    if (args[1].equalsIgnoreCase(configType.toString())) {
                        gameInstance.getConfig(configType).reloadConfig();
                        if (configType.equals(ConfigType.BOOK))
                            gameInstance.getGame().getItemBook().createBookItem();
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
        }
        return false;
    }
}