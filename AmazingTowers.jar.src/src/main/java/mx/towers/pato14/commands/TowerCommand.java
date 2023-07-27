package mx.towers.pato14.commands;

import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.tasks.Start;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.utils.Config;
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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import mx.towers.pato14.AmazingTowers;
import org.bukkit.command.CommandExecutor;
import org.bukkit.permissions.PermissionAttachment;

public class TowerCommand implements CommandExecutor
{
    private final AmazingTowers plugin;
    private final ArrayList<Player> senderPlayer;
    private final HashMap<String, Long> cooldown;
    public TowerCommand(final AmazingTowers plugin) {
        this.senderPlayer = new ArrayList<>();
        this.plugin = plugin;
        this.cooldown = new HashMap<>();
    }

    public static World createWorld(String name) {
        final WorldCreator wc = new WorldCreator(name);
        wc.type(WorldType.FLAT);
        wc.generateStructures(false);
        wc.generatorSettings("2;0;1;");
        final World world = Bukkit.createWorld(wc);
        world.setAutoSave(false);
        world.setSpawnLocation(0, 0, 0);
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("mobGriefing", "false");
        world.setGameRuleValue("doDaylightCycle", "false");
        return world;
    }

    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (args.length < 1  || Subcommand.isValidSubcommand(args[0]) == null || args[0].equalsIgnoreCase(Subcommand.HELP.name())) {
            sender.sendMessage(Subcommand.listOfCommands());
            return true;
        }
        Subcommand arg0 = Subcommand.isValidSubcommand(args[0]);
        assert arg0 != null;
        if (!arg0.hasPermission(sender)) {
            sender.sendMessage("No tienes permiso para ejecutar este comando.");
            return true;
        }
        if (!arg0.checkCorrectSender(sender)) {
            sender.sendMessage("Solo un jugador puede ejecutar este comando.");
            return true;
        }
        if (!arg0.correctNumberOfArguments(args)) {
            sender.sendMessage(arg0.getCorrectUse());
            return true;
        }
        if (args.length > 1) {
            int argError = Subcommand.checkArgs(arg0, args);
            if (argError > 0) {
                sender.sendMessage("Error en el argumento " + argError);
                sender.sendMessage(arg0.getCorrectUse());
                return true;
            }
        }
        GameInstance gameInstance = null;
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
            gameInstance = this.plugin.getGameInstance(player);
        }
        switch (arg0) {
            case STATS:
                if (this.plugin.getGlobalConfig().getBoolean("Options.mysql.active")) {
                    if (!cooldown.containsKey(sender.getName()) || System.currentTimeMillis() - cooldown.get(sender.getName()) > 3000) {
                        FindOneCallback.findPlayerAsync(args[1], this.plugin, result -> {
                            if (result == null) {
                                sender.sendMessage("§4No se ha encontrado ese jugador");
                            } else {
                                for (StatType statType : StatType.values())
                                    sender.sendMessage("§7" + statType.getText() + ": " + statType.getColor() + "§l" +
                                            result[statType.getIndex() - 3]);
                            }
                        });
                        cooldown.put(sender.getName(), System.currentTimeMillis());
                    } else {
                        sender.sendMessage("§4Tienes que esperar " + "§6" + (3000 - (System.currentTimeMillis() - cooldown.get(sender.getName())))/1000 + " §4segundos antes de poder ejecutar este comando.");
                    }
                } else {
                    sender.sendMessage("La base de datos está desactivada en la configuración del plugin");
                }
                break;
            case SPECTATOR:
                if (sender instanceof Player && ((Player) sender).getGameMode().equals(GameMode.SPECTATOR))
                    Dar.DarItemsJoin((Player) sender, GameMode.ADVENTURE);
                else
                    sender.sendMessage("§4Solo puedes ejecutar este comando estando en modo espectador");
                break;
            case ORGANIZER:
                if (args[1].equals(this.plugin.getGlobalConfig().getString("Permissions.password.organizer"))) {
                    PermissionAttachment organizer;
                    organizer = sender.addAttachment(this.plugin);
                    this.plugin.getPermissions().put(sender.getName(), organizer);
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
                    sender.sendMessage("§4Este comando solo se puede usar antes de empezar la partida");
                break;
            case RULE:
                assert gameInstance != null;
                gameInstance.getRules().replace(Rule.valueOf(args[1].toUpperCase()), Boolean.parseBoolean(args[2].toLowerCase()));
                sender.sendMessage("Set " + args[1].toLowerCase() + " §rto §e" + args[2].toLowerCase());
                break;
            case SETSCORE:
                assert gameInstance != null;
                if (gameInstance.getGame().getGameState().equals(GameState.GAME)) {
                    gameInstance.getGame().getTeams().getTeam(TeamColor.valueOf(args[1].toUpperCase())).setPoints(Integer.parseInt(args[2]));
                    Bukkit.broadcastMessage(AmazingTowers.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("messages.PointsScored-Messages.setpointsCommand") +
                            gameInstance.getGame().getTeams().scores()));
                    int pointsToWin = gameInstance.getConfig(ConfigType.CONFIG).getInt("Options.Points");
                    for (Team team: gameInstance.getGame().getTeams().getTeams()) {
                        if (team.getPoints() >= pointsToWin) {
                            gameInstance.getGame().getFinish().Fatality(team.getTeamColor());
                            gameInstance.getGame().setGameState(GameState.FINISH);
                        }
                    }
                } else
                    sender.sendMessage("§4You can only execute this command during a match.");
                break;
            case JOINTEAM:
                assert gameInstance != null;
                Player p = Bukkit.getPlayer(args[2]);
                if (p != null && gameInstance.getGame().getPlayers().contains(p)) {
                    Team pTeam = gameInstance.getGame().getTeams().getTeamByPlayer(p);
                    if (pTeam != null)
                       pTeam.removePlayer(p);
                    gameInstance.getGame().getTeams().getTeam(TeamColor.valueOf(args[1])).addPlayer(p);
                    Dar.darItemsJoinTeam(p);
                } else
                    sender.sendMessage("Ese jugador no está en esta partida.");
                break;
            case TPWORLD:
                assert player != null;
                if (Bukkit.getWorld(args[1]) != null) {
                    player.teleport(Bukkit.getWorld(args[1]).getSpawnLocation());
                    player.sendMessage("Teleportation to the world §a" + args[1] + " successfully...");
                }
                player.sendMessage("§fThe world §a" + args[1] + "§f doesn't exist");
                break;
            case CREATEWORLD:
                boolean success = false;
                boolean setAll = args[1].equalsIgnoreCase("all");
                if (Bukkit.getWorld(args[1]) != null)
                    sender.sendMessage("§fThe world §c" + args[1] + "§f already exists!");
                else {
                    for (GameInstance gameInstance1 : AmazingTowers.getGameInstances().values()) {
                        if (gameInstance1.getWorld() != null || !(setAll || args[1].equals(gameInstance1.getName())))
                            continue;
                        gameInstance1.linkWorld(createWorld(gameInstance1.getName()));
                        sender.sendMessage("The world §a" + gameInstance1.getName() + "§f was created§a successfully...");
                        success = true;
                    }
                }
                if (!success) {
                    if (setAll)
                        sender.sendMessage("All instances already have a world linked to them.");
                    else
                        sender.sendMessage("That instance doesn't exist.");
                } else
                    sender.sendMessage("To go to another world, use /tt tpWorld <worldName>");
                break;
            case BACKUPWORLD:
                assert gameInstance != null;
                final File backup = new File(this.plugin.getDataFolder().getAbsolutePath() + "/backup", gameInstance.getWorld().getName());
                if (backup.exists())
                    player.sendMessage("§fThe folder §a'" + gameInstance.getWorld().getName() + "' §falready exists in the backup folder!");
                else if (!this.senderPlayer.contains(player)) {
                    this.senderPlayer.add(player);
                    player.sendMessage("§fDo you want to save the world §aTheTowers §fin the backup folder? ");
                    player.sendMessage("§fIf you want to save it, execute again the command §a/towers backupWorld");
                }
                else {
                    final File world3 = new File(Bukkit.getWorldContainer().getAbsolutePath(), gameInstance.getWorld().getName());
                    if (!world3.exists())
                        player.sendMessage("§fThe folder of the world §c" + gameInstance.getWorld().getName() + " §fdoesn't exist");
                    gameInstance.getWorld().save();
                    WorldReset.copyWorld(world3, backup);
                    final File[] ficheros = backup.listFiles();
                    if (ficheros == null) {
                        player.sendMessage("Error while trying to do a backup of the world.");
                        break;
                    }
                    for (File fichero : ficheros) {
                        if (fichero.getName().equals("session.lock") || fichero.getName().equals("uid.dat")) {
                            if (!fichero.delete()) {
                                player.sendMessage("Error while trying to do a backup of the world.");
                                break;
                            }
                        }
                    }
                    player.sendMessage("§fThe folder §a" + gameInstance.getWorld().getName() + " §fcopied to the backup folder §asuccessfully");
                    this.senderPlayer.remove(player);
                }
                break;
            case LOADWORLD:
                assert player != null;
                final File world2 = new File(Bukkit.getWorldContainer().getAbsolutePath(), args[1]);
                if (!world2.exists())
                    player.sendMessage("§fThe folder of the world §c" + args[1] + " §fdoesn't exist");
                else if (Bukkit.getWorld(args[1]) == null) {
                    player.sendMessage("Loading the world §a" + args[1] + "§f...");
                    new WorldCreator(args[1]).createWorld();
                    player.sendMessage("The world §a" + args[1] + " §floaded §asuccesfully...");
                } else
                    player.sendMessage("§fThe world §a" + args[1] + " §fis already loaded!");
                break;
            case SETREGION:
                assert player != null;
                final Location loc = Location.valueOf(args[1].toUpperCase());
                final TeamColor teamColor = args.length < 3 || !TeamColor.isTeamColor(args[2]) ? null : TeamColor.valueOf(args[2].toUpperCase());
                if (loc.needsTeamColor() && teamColor == null) {
                    player.sendMessage("Hace falta especificar un color de equipo.");
                    break;
                }
                final Config locations = gameInstance.getConfig(ConfigType.LOCATIONS);
                final String path = loc.getPath(teamColor);
                if (!loc.getLocationType().equals(LocationType.GENERATOR)) {
                    if (loc.getLocationType().equals(LocationType.POINT)) {
                        if (loc.isList()) {
                            List<String> list = locations.getStringList(path);
                            list.add(Locations.getLocationStringCenter(player.getLocation(), true));
                            locations.set(path, list);
                        } else {
                            locations.set(path, Locations.getLocationStringCenter(player.getLocation(), true));
                        }
                        player.sendMessage("§7Defined point of §a" + args[1]);
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
                        } else {
                            locations.set(path, corners);
                        }
                        player.sendMessage("§7Defined area corner §a1 §7and §a2 §7of §a" + args[1]);
                    }
                } else {
                    @SuppressWarnings("unchecked")
                    List<Map<String, String>> generators = locations.getList(path) == null ? new ArrayList<>() : locations
                            .getMapList(path).stream().map(o -> (Map<String, String>) o).collect(Collectors.toList());
                    HashMap<String, String> newGenerator = new HashMap<>();
                    newGenerator.put("item", plugin.getNms().serializeItemStack(player.getItemInHand()));
                    newGenerator.put("coords", Locations.getLocationStringCenter(player.getLocation(), true));
                    generators.add(newGenerator);
                    locations.set(path, generators);
                    player.sendMessage("§7Defined generator");
                }
                locations.saveConfig();
                break;
            case HELP:
                sender.sendMessage(Subcommand.listOfCommands());
            case VAULTINFO:
                if (Bukkit.getPluginManager().getPlugin("Vault") == null)
                    sender.sendMessage("§cThe vault plugin don't exist");
                else if (this.plugin.getGlobalConfig().getBoolean("Options.Rewards.vault")) {
                    final String format = ChatColor.GRAY + "%s: [%s]";
                    sender.sendMessage("§7*--------------*");
                    sender.sendMessage(" §f*Vault* ");
                    sender.sendMessage((SetupVault.getVaultEconomy() != null) ? String.format(format, "Economy", SetupVault.getVaultEconomy().getName()) : String.format(format, "Economy", "NONE"));
                    sender.sendMessage((SetupVault.getVaultChat() != null) ? String.format(format, "Chat", SetupVault.getVaultChat().getName()) : String.format(format, "Chat", "NONE"));
                    sender.sendMessage("§7*--------------*");
                } else
                    sender.sendMessage("§cThe vault option is inactive in the config");
                break;
            case RELOADCONFIG:
                assert gameInstance != null;
                for (ConfigType configType : ConfigType.values()) {
                    if (args[1].equalsIgnoreCase(configType.toString())) {
                        gameInstance.getConfig(configType).reloadConfig();
                        if (configType.equals(ConfigType.BOOK))
                            gameInstance.getGame().getItemBook().createBookItem();
                        sender.sendMessage("§aReloaded " +  configType.toString().toLowerCase() + " config successfully§f!");
                        break;
                    }
                }
                break;
            case TOOL:
                assert player != null;
                for (Tool tool : Tool.values()) {
                    if (args[1].equalsIgnoreCase(tool.toString())) {
                        player.getInventory().addItem(tool.getItem().clone());
                        player.sendMessage(tool.getMsg());
                        break;
                    }
                }
                break;
        }
        return false;
    }
}