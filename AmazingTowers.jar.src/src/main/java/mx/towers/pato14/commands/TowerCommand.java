
package mx.towers.pato14.commands;

import mx.towers.pato14.game.tasks.Start;
import mx.towers.pato14.utils.enums.StatType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.Rule;
import mx.towers.pato14.utils.enums.Team;
import mx.towers.pato14.utils.mysql.FindOneCallback;
import org.bukkit.*;
import mx.towers.pato14.utils.world.WorldReset;
import mx.towers.pato14.utils.rewards.SetupVault;
import net.md_5.bungee.api.ChatColor;
import mx.towers.pato14.utils.locations.Locations;
import java.io.File;

import mx.towers.pato14.utils.locations.Detectoreishon;
import mx.towers.pato14.game.utils.Dar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.inventory.ItemStack;
import mx.towers.pato14.AmazingTowers;
import org.bukkit.command.CommandExecutor;
import org.bukkit.permissions.PermissionAttachment;

public class TowerCommand implements CommandExecutor
{
    private final AmazingTowers a;
    private ItemStack itemChestRefill;
    private ArrayList<Player> senderPlayer;
    private final HashMap<String, Long> cooldown;
    public TowerCommand(final AmazingTowers a) {
        this.senderPlayer = new ArrayList<Player>();
        this.a = a;
        this.itemChestRefill = new ItemStack(Material.IRON_SPADE);
        final ItemMeta metaItem = this.itemChestRefill.getItemMeta();
        metaItem.setDisplayName("§aSelect and Remove Chest Refill");
        this.itemChestRefill.setItemMeta(metaItem);
        this.cooldown = new HashMap<>();
    }
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
    public static boolean isNumericInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
    public static boolean isRule(String str) {
        try {
            Rule.valueOf(str.toUpperCase());
            return true;
        } catch(IllegalArgumentException e){
            return false;
        }
    }
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        PermissionAttachment organizer;
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("stats")) {
                if (!cooldown.containsKey(sender.getName()) || System.currentTimeMillis() - cooldown.get(sender.getName()) > 3000) {
                    if (this.a.getConfig().getBoolean("Options.mysql.active")) {
                        String playername = null;
                        if (sender instanceof Player && args.length == 1) {
                            playername = sender.getName();
                        } else if (args.length > 1) {
                            playername = args[1];
                        }
                        FindOneCallback.findPlayerAsync(playername, this.a, result -> {
                            if (result == null) {
                                sender.sendMessage("§4No se ha encontrado ese jugador");
                                return;
                            } else {
                                sender.sendMessage("§7§lEstadisticas:");
                                sender.sendMessage("§7Kills: " + "§3§l" + result[0]);
                                sender.sendMessage("§7Muertes: " + "§4§l" + result[1]);
                                sender.sendMessage("§7Puntos: " + "§6§l" + result[2]);
                                sender.sendMessage("§7Partidas jugadas: " + "§a§l" + result[3]);
                                sender.sendMessage("§7Partidas ganadas: " + "§b§l" + result[4]);
                                sender.sendMessage("§7Bloques rotos: " + "§d§l" + result[5]);
                                sender.sendMessage("§7Bloques colocados: " + "§2§l" + result[6]);
                            }
                        });
                    } else {
                        sender.sendMessage("MySQL is turn off in the plugin configuration");
                    }
                    cooldown.put(sender.getName(), System.currentTimeMillis());
                } else {
                    sender.sendMessage("§4Tienes que esperar " + "§6" + (3000 - (System.currentTimeMillis() - cooldown.get(sender.getName())))/1000 + " §4segundos antes de poder ejecutar este comando.");
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("spect")) {
                if (sender instanceof Player && ((Player) sender).getGameMode().equals(GameMode.SPECTATOR)) {
                    Dar.DarItemsJoin((Player) sender, GameMode.ADVENTURE);
                } else {
                    sender.sendMessage("§4Solo puedes ejecutar este comando estando en modo espectador");
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("organizer")) {
                if (sender instanceof Player) {
                    if (args.length >= 2 && args[1].equals(this.a.getConfig().getString("Permissions.password.organizer"))) {
                        organizer = sender.addAttachment(this.a);
                        this.a.getPermissions().put(sender.getName(), organizer);
                        organizer.setPermission("towers.organizer", true);
                    }
                }
                return true;
            }
        }
        if (!sender.hasPermission("towers.admin")) {
            if (!sender.hasPermission("towers.organizer") || (!(args.length > 0 && (args[0].equals("count") || args[0].equals("rule") || args[0].equals("setPoints") || args[0].equals("joinTeam"))))) {
                sender.sendMessage("§fPlugin §aTheTowers(AmazingTowers) §fcreated by §aPato14, §fupdated by §aMarco2124 §fv2.3b");
                return true;
            }
        }
        if (args.length > 0) {
            final String st = args[0];
            Label_3759: {
                final String s;
                switch (s = st) {
                    case "debug": {
                        if (args.length >= 2) {
                            if (args[1].equals("addPlayer")) {
                                if (args.length >= 3) {
                                    a.getGame().getStats().setHashStats(args[2]);
                                    a.getGame().getStats().addOne(args[2], StatType.KILLS);
                                    a.getGame().getStats().addOne(args[2], StatType.KILLS);
                                }
                            }
                        } else
                            sender.sendMessage("Do not use unless you know what you are doing!");
                        return true;
                    }
                    case "count": {
                        if (GameState.isState(GameState.LOBBY) || GameState.isState(GameState.PREGAME)) {
                            Start start = this.a.getGame().getStart();
                            if (args.length >= 2 && args[1].equals("stop")) {
                                start.stopCount();
                            } else if (args.length >= 2 && args[1].equals("start")) {
                                if (!start.hasStarted()) {
                                    GameState.setState(GameState.PREGAME);
                                    start.setRunFromCommand(true);
                                    start.setHasStarted(true);
                                    start.gameStart();
                                }
                                start.continueCount();
                            } else if (args.length >= 2 && isNumericInt(args[1])) {
                                start.setSeconds(Integer.parseInt(args[1]));
                            } else {
                                sender.sendMessage("§fUsage: §a/towers §fcount §e<start|stop|number>");
                            }
                        } else {
                            sender.sendMessage("§4Este comando solo se puede usar antes de empezar la partida");
                        }
                        return true;
                    }
                    case "rule": {
                        if (args.length >= 2 && isRule(args[1])) {
                            if (args.length >= 3 && (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")))
                                Rule.valueOf(args[1].toUpperCase()).setCurrentState(Boolean.parseBoolean(args[2]));
                            sender.sendMessage("Set " + args[1].toLowerCase() + " §rto §e" + Rule.valueOf(args[1].toUpperCase()).getCurrentState());
                        } else {
                            StringBuilder sb = new StringBuilder();
                            sb.append("Usage: §a/towers §frule §e<");
                            for (Rule r: Rule.values()) {
                                sb.append(r.toString().toLowerCase());
                                sb.append("|");
                            }
                            sb.deleteCharAt(sb.length()-1);
                            sb.append("> <true|false>");
                            sender.sendMessage(sb.toString());
                        }
                        return false;
                    }
                    case "setPoints": {
                        if (GameState.isState(GameState.GAME)) {
                            if (args.length > 2) {
                                if (isNumeric(args[2]) && (args[1].equals("red") || args[1].equals("blue"))) {
                                    if (args[1].equals("red"))
                                        this.a.getGame().getTeams().redPoints = Integer.parseInt(args[2]);
                                    else
                                        this.a.getGame().getTeams().bluePoints = Integer.parseInt(args[2]);
                                    Bukkit.broadcastMessage(this.a.getColor(this.a.getMessages().getString("messages.PointsScored-Messages.setpointsCommand")
                                            .replace("%PointsRed%", String.valueOf((this.a.getGame().getTeams()).redPoints))
                                            .replace("%PointsBlue%", String.valueOf((this.a.getGame().getTeams()).bluePoints))));
                                    if ((this.a.getGame().getTeams()).bluePoints >= this.a.getConfig().getInt("Options.Points")) {
                                        this.a.getGame().getFinish().Fatality(Team.BLUE);
                                        GameState.setState(GameState.FINISH);
                                    } else if ((this.a.getGame().getTeams()).redPoints >= this.a.getConfig().getInt("Options.Points")) {
                                        this.a.getGame().getFinish().Fatality(Team.RED);
                                        GameState.setState(GameState.FINISH);
                                    }
                                } else  {
                                    sender.sendMessage("§fUsage: §a/towers §fsetPoints §e<blue|red> <number>");
                                    return false;
                                }
                            } else {
                                {
                                    sender.sendMessage("§fUsage: §a/towers §fsetPoints §e<blue|red> <number>");
                                    return false;
                                }
                            }
                        } else {
                            sender.sendMessage("§4You can only execute this command during a match.");
                            return false;
                        }
                        return true;
                    }
                    case "joinTeam": {
                        if (!(sender instanceof Player)) {
                            return false;
                        }
                        if (args.length >= 2) {
                            final Player player = (Player) sender;
                            if (args[1].equals("red")) {
                                if (this.a.getGame().getTeams().getBlue().containsPlayer(player.getName())) {
                                    this.a.getGame().getTeams().getBlue().removePlayer(player);
                                }
                                this.a.getGame().getTeams().getRed().addPlayer((OfflinePlayer) player);
                                Dar.darItemsJoinTeam(player);
                                return false;
                            }
                            if (args[1].equals("blue")) {
                                if (this.a.getGame().getTeams().getRed().containsPlayer(player.getName())) {
                                    this.a.getGame().getTeams().getRed().removePlayer(player);
                                }
                                this.a.getGame().getTeams().getBlue().addPlayer((OfflinePlayer) player);
                                Dar.darItemsJoinTeam(player);
                                return false;
                            }
                        }
                        sender.sendMessage("§fUsage: §a/towers §fjoinTeam §e<blue|red>");
                        return false;
                    }
                    case "locations": {
                        if (!(sender instanceof Player)) {
                            return false;
                        }
                        final Player player = (Player)sender;
                        if (args.length == 1) {
                            player.sendMessage(Detectoreishon.getLocatioshionsTruee(false));
                            return false;
                        }
                        player.sendMessage("§a/towers §flocations");
                        return false;
                    }
                    case "tpWorld": {
                        if (!(sender instanceof Player)) {
                            return false;
                        }
                        final Player player = (Player)sender;
                        if (args.length != 2) {
                            player.sendMessage("§fUsage: §a/towers §ftpWorld §e<worldName)");
                            return false;
                        }
                        if (Bukkit.getWorld(args[1]) != null) {
                            player.teleport(Bukkit.getWorld(args[1]).getSpawnLocation());
                            player.sendMessage("Teleportation to the world §a" + args[1] + " successfully...");
                            return false;
                        }
                        player.sendMessage("§fThe world §a" + args[1] + " §fnot exist");
                        return false;
                    }
                    case "createWorld": {
                        if (!(sender instanceof Player)) {
                            return false;
                        }
                        final Player player = (Player)sender;
                        if (args.length != 3) {
                            player.sendMessage("Usage: §a/towers §fcreateWorld §e<nameWorld> emptyWorld");
                            return false;
                        }
                        if (Bukkit.getWorld(args[1]) != null) {
                            player.sendMessage("§fThe world §c" + args[1] + " §fnot exist!");
                            return false;
                        }
                        if (args[2].equals("emptyWorld")) {
                            player.sendMessage("Creating world §a" + args[1] + "§f...");
                            final WorldCreator wc = new WorldCreator(args[1]);
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
                            player.sendMessage("The world §a" + args[1] + " §fcreated §asuccesfully...");
                            return false;
                        }
                        player.sendMessage("Usage: §a/towers §fcreateWorld §e<nameWorld> emptyWorld");
                        return false;
                    }
                    case "protect": {
                        if (!(sender instanceof Player)) {
                            return false;
                        }
                        final Player player = (Player)sender;
                        if (args.length != 2) {
                            player.sendMessage("Usage: §a/towers §fprotect §e<blue|red|lobby|redbridge|bluebridge|redchestroom1|redchestroom2|bluechestroom1|bluechestroom2|redpoint|bluepoint>");
                            return false;
                        }
                        if (!args[1].equals("blue") && !args[1].equals("red") && !args[1].equals("lobby") && !args[1].equals("bluebridge")
                                && !args[1].equals("redbridge") && !args[1].equals("redchestroom1") && !args[1].equals("redchestroom2")
                                && !args[1].equals("bluechestroom1") && !args[1].equals("bluechestroom2")
                                && !args[1].equals("redpoint") && !args[1].equals("bluepoint")) {
                            player.sendMessage("Usage: §a/towers §fprotect §e<blue|red|lobby|redbridge|bluebridge|redchestroom1|redchestroom2|bluechestroom1|bluechestroom2|redpoint|bluepoint>");
                            return false;
                        }
                        if (!this.a.getWand().equalsPos1("") && !this.a.getWand().equalsPos2("")) {
                            if (this.a.getWand().getPos1() != null) {
                                this.a.getLocations().set("LOCATIONS.PROTECT." + args[1].toUpperCase() + ".1", (Object)this.a.getWand().getPos1());
                                this.a.getLocations().saveConfig();
                            }
                            if (this.a.getWand().getPos2() != null) {
                                this.a.getLocations().set("LOCATIONS.PROTECT." + args[1].toUpperCase() + ".2", (Object)this.a.getWand().getPos2());
                                this.a.getLocations().saveConfig();
                            }
                            this.a.getWand().clearStrings();
                            player.sendMessage("§7Defined protection corner §a1 §7and §a2 §7of §a" + args[1]);
                            return false;
                        }
                        return false;
                    }
                    case "help": {
                        sender.sendMessage("§7*----------------------------------------------*");
                        sender.sendMessage("");
                        sender.sendMessage("  §7AmazingTowers §f2.3b");
                        sender.sendMessage("");
                        sender.sendMessage("§a->Commands<-");
                        sender.sendMessage("");
                        sender.sendMessage("§f§lWorld");
                        sender.sendMessage("§f/towers §abackupWorld");
                        sender.sendMessage("§f/towers §aloadWorld §e<nameWorld>");
                        sender.sendMessage("§f/towers §acreateWorld §e<nameWorld> emptyWorld");
                        sender.sendMessage("§f/towers §atpWorld §e<nameWorld>");
                        sender.sendMessage("");
                        sender.sendMessage("§f§lGame");
                        sender.sendMessage("§f/towers §asetspawn §e<blue|red|lobby|iron|xpbottles|lapislazuli>");
                        sender.sendMessage("§f/towers §asetpool §e<blue|red>");
                        sender.sendMessage("§f/towers §asetborder");
                        sender.sendMessage("§f/towers §aprotect §e<blue|red|lobby|redbridge|bluebridge|redchestroom1|redchestroom2|bluechestroom1|bluechestroom2|redpoint|bluepoint>");
                        sender.sendMessage("§f/towers §aprotectChest §e<red|blue>");
                        sender.sendMessage("§f/towers §areloadConfig §e<config|scoreboard|messages>");
                        sender.sendMessage("§f/towers §atool §e<wand|refillChest>");
                        sender.sendMessage("§f/towers §alocations");
                        sender.sendMessage("§f/towers §asetPoints §e<blue|red> <number>");
                        sender.sendMessage("§f/towers §astats §e<player>");
                        sender.sendMessage("§f/towers §arule §e<rule> <true|false>");
                        sender.sendMessage("§f/towers §aspect");
                        sender.sendMessage("§f/towers §ajoinTeam §e<blue|red>");
                        sender.sendMessage("§f/towers §acount §e<start|stop|number>");
                        sender.sendMessage("");
                        sender.sendMessage("§7*----------------------------------------------*");
                        return false;
                    }
                    case "tool": {
                        if (!(sender instanceof Player)) {
                            return false;
                        }
                        final Player player = (Player)sender;
                        if (args.length != 2) {
                            player.sendMessage("Usage: §a/towers §ftool §e<wand|refillChest");
                            return false;
                        }
                        if (args[1].equals("wand")) {
                            player.getInventory().addItem(new ItemStack[] { this.a.getWand().getItem() });
                            player.sendMessage("§aLeft Click §7to set §aPos1 §7and §aRight click §7to set §aPos2");
                            return false;
                        }
                        if (args[1].equals("refillChest")) {
                            player.getInventory().addItem(new ItemStack[] { this.itemChestRefill });
                            player.sendMessage("§aLeft Click §7to set chest Refill and §cRight Click §7to remove chest location in Config!");
                            return false;
                        }
                        player.sendMessage("Usage: §a/towers §ftool §e<wand|refillChest>");
                        return false;
                    }
                    case "protectChest": {
                        if (!(sender instanceof Player)) {
                            return false;
                        }
                        final Player player = (Player)sender;
                        if (args.length != 2) {
                            player.sendMessage("Usage: §a/towers protectChest §e<blue|red>");
                            return false;
                        }
                        if (!args[1].equals("blue") && !args[1].equals("red")) {
                            player.sendMessage("Usage: §a/towers protectChest §e<blue|red>");
                            return false;
                        }
                        if (!this.a.getWand().equalsPos1("") && !this.a.getWand().equalsPos2("")) {
                            if (this.a.getWand().getPos1() != null) {
                                this.a.getLocations().set("LOCATIONS.PROTECT." + args[1].toUpperCase() + ".CHEST.1", (Object)this.a.getWand().getPos1());
                                this.a.getLocations().saveConfig();
                            }
                            if (this.a.getWand().getPos2() != null) {
                                this.a.getLocations().set("LOCATIONS.PROTECT." + args[1].toUpperCase() + ".CHEST.2", (Object)this.a.getWand().getPos2());
                                this.a.getLocations().saveConfig();
                            }
                            this.a.getWand().clearStrings();
                            player.sendMessage("§7Defined protection corner §a1 §7and §a2 §7of §a" + args[1] + " §7chest");
                            return false;
                        }
                        return false;
                    }
                    case "reloadConfig": {
                        if (args.length != 2) {
                            return false;
                        }
                        if (args[1].equals("config")) {
                            this.a.getConfig().reloadConfig();
                            sender.sendMessage("§aReload Config successfully§f!");
                            return false;
                        }
                        if (args[1].equals("scoreboard")) {
                            this.a.getScoreboard().reloadConfig();
                            sender.sendMessage("§aReload scoreboard config successfully§f!");
                            return false;
                        }
                        if (args[1].equals("messages")) {
                            this.a.getMessages().reloadConfig();
                            sender.sendMessage("§aReload messages config successfully§f!");
                            return false;
                        }
                        if (args[1].equals("locations")) {
                            this.a.getLocations().reloadConfig();
                            sender.sendMessage("§aReload locations config successfully§f!");
                            return false;
                        }
                        if (args[1].equals("book")) {
                            this.a.getBook().reloadConfig();
                            this.a.getGame().getItemBook().createBookItem();
                            sender.sendMessage("§aReload book config successfully§f!");
                            return false;
                        }
                        sender.sendMessage("§fUsage: §a/towers §freloadConfig §e<config|scoreboard|messages|book>");
                        return false;
                    }
                    case "setborder": {
                        if (!(sender instanceof Player)) {
                            return false;
                        }
                        final Player player = (Player)sender;
                        if (args.length != 1) {
                            player.sendMessage("Usage: §a/towers §fsetborder");
                            return false;
                        }
                        if (!this.a.getWand().equalsPos1("") && !this.a.getWand().equalsPos2("")) {
                            if (this.a.getWand().getPos1() != null) {
                                this.a.getLocations().set("LOCATIONS.PROTECT.BORDER.1", (Object)this.a.getWand().getPos1());
                                this.a.getLocations().saveConfig();
                            }
                            if (this.a.getWand().getPos2() != null) {
                                this.a.getLocations().set("LOCATIONS.PROTECT.BORDER.2", (Object)this.a.getWand().getPos2());
                                this.a.getLocations().saveConfig();
                            }
                            this.a.getWand().clearStrings();
                            player.sendMessage("§7Defined corner §a1 §7and §a2 §7of the border");
                            return false;
                        }
                        return false;
                    }
                    case "backupWorld": {
                        break;
                    }
                    case "loadWorld": {
                        if (!(sender instanceof Player)) {
                            return false;
                        }
                        final Player player = (Player)sender;
                        if (args.length != 2) {
                            player.sendMessage("Usage: §a/towers §floadWorld §e<worldName>");
                            return false;
                        }
                        final File world2 = new File(Bukkit.getWorldContainer().getAbsolutePath(), args[1]);
                        if (!world2.exists()) {
                            player.sendMessage("§fThe folder of the world §c" + args[1] + " §fnot exist");
                            return false;
                        }
                        if (Bukkit.getWorld(args[1]) == null) {
                            player.sendMessage("Loading the world §a" + args[1] + "§f...");
                            new WorldCreator(args[1]).createWorld();
                            player.sendMessage("The world §a" + args[1] + " §floaded §asuccesfully...");
                            return false;
                        }
                        player.sendMessage("§fThe world §a" + args[1] + " §fis already loaded!");
                        return false;
                    }
                    case "setspawn": {
                        if (!(sender instanceof Player)) {
                            return false;
                        }
                        final Player player = (Player)sender;
                        if (args.length != 2) {
                            player.sendMessage("Usage: §a/towers §fsetspawn §e<blue|red|lobby|iron|xpbottles|lapislazuli>");
                            return false;
                        }
                        if (args[1].equals("lobby") || args[1].equals("red") || args[1].equals("blue")) {
                            final String t = args[1].equals("lobby") ? "LOCATIONS.LOBBY" : (args[1].equals("red") ? "LOCATIONS.RED_SPAWN" : "LOCATIONS.BLUE_SPAWN");
                            this.a.getLocations().set(t, (Object)Locations.getLocationStringCenter(player.getLocation(), true));
                            this.a.getLocations().saveConfig();
                            player.sendMessage("§7Spawn of §e" + args[1].toLowerCase() + " §7defined");
                            return false;
                        }
                        if (args[1].equalsIgnoreCase("iron") || args[1].equalsIgnoreCase("xpbottles") || args[1].equalsIgnoreCase("lapislazuli")) {
                            this.a.getLocations().set("LOCATIONS.GENERATOR." + args[1].toUpperCase(), (Object)Locations.getLocationStringCenter(player.getLocation(), true));
                            this.a.getLocations().saveConfig();
                            player.sendMessage("§7You've defined generator of §e" + args[1].toLowerCase() + " §7successfully");
                            return false;
                        }
                        player.sendMessage("Usage: §a/towers §fsetspawn §e<blue|red|lobby|iron|xpbottles|lapislazuli>");
                        return false;
                    }
                    case "vault-info": {
                        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
                            sender.sendMessage("§cThe vault plugin don't exist");
                            return false;
                        }
                        if (this.a.getConfig().getBoolean("Options.Rewards.vault")) {
                            final String format = ChatColor.GRAY + "%s: [%s]";
                            sender.sendMessage("§7*--------------*");
                            sender.sendMessage(" §f*Vault* ");
                            sender.sendMessage((SetupVault.getVaultEconomy() != null) ? String.format(format, "Economy", SetupVault.getVaultEconomy().getName()) : String.format(format, "Economy", "NONE"));
                            sender.sendMessage((SetupVault.getVaultChat() != null) ? String.format(format, "Chat", SetupVault.getVaultChat().getName()) : String.format(format, "Chat", "NONE"));
                            sender.sendMessage("§7*--------------*");
                            return false;
                        }
                        sender.sendMessage("§cThe vault option is inactive in the config");
                        return false;
                    }
                    case "setpool": {
                        if (!(sender instanceof Player)) {
                            return false;
                        }
                        final Player player = (Player)sender;
                        if (args.length != 2) {
                            player.sendMessage("Usage: §a/towers §fsetpool §e<blue|red>");
                            return false;
                        }
                        if (!args[1].equals("blue") && !args[1].equals("red")) {
                            player.sendMessage("Usage: §a/towers §fsetpool §e<blue|red>");
                            return false;
                        }
                        if (!this.a.getWand().equalsPos1("") && !this.a.getWand().equalsPos2("")) {
                            if (this.a.getWand().getPos1() != null) {
                                this.a.getLocations().set("LOCATIONS.POOLS." + args[1].toUpperCase() + ".1", (Object)this.a.getWand().getPos1());
                                this.a.getLocations().saveConfig();
                            }
                            if (this.a.getWand().getPos2() != null) {
                                this.a.getLocations().set("LOCATIONS.POOLS." + args[1].toUpperCase() + ".2", (Object)this.a.getWand().getPos2());
                                this.a.getLocations().saveConfig();
                            }
                            this.a.getWand().clearStrings();
                            player.sendMessage("§7Defined corner §e1 §7and §a2 §7of §e" + args[1] + " §7pool");
                            return false;
                        }
                        return false;
                    }
                    default:
                        break Label_3759;
                }
                if (!(sender instanceof Player)) {
                    return false;
                }
                final Player player = (Player)sender;
                if (args.length != 1) {
                    player.sendMessage("§a/towers §fbackupWorld");
                    return false;
                }
                final File backup = new File(String.valueOf(this.a.getDataFolder().getAbsolutePath()) + "/backup", "TheTowers");
                if (backup.exists()) {
                    player.sendMessage("§fThe folder §a'TheTowers' §falready exists in backup folder!");
                    return false;
                }
                if (!this.senderPlayer.contains(player)) {
                    if (Bukkit.getWorld("TheTowers") != null) {
                        this.senderPlayer.add(player);
                        player.sendMessage("§fYou want to save the world §aTheTowers §fin the backup folder? ");
                        player.sendMessage("§fIf you want to save, Execute again the comand §a/towers backupWorld");
                        return false;
                    }
                    player.sendMessage("§fThe world §cTheTowers §fnot exist");
                    return false;
                }
                else {
                    final File world3 = new File(Bukkit.getWorldContainer().getAbsolutePath(), "TheTowers");
                    if (!world3.exists()) {
                        player.sendMessage("§fThe folder of the world §cTheTowers §fnot exist");
                        return false;
                    }
                    if (Bukkit.getWorld("TheTowers") == null) {
                        player.sendMessage("§fThe world §cTheTowers §fnot exist");
                        return false;
                    }
                    Bukkit.getWorld("TheTowers").save();
                    WorldReset.copyWorld(world3, backup);
                    final File[] ficheros = backup.listFiles();
                    for (int x = 0; x < ficheros.length; ++x) {
                        if (ficheros[x].getName().equals("session.lock") || ficheros[x].getName().equals("uid.dat")) {
                            ficheros[x].delete();
                        }
                    }
                    player.sendMessage("§fthe folder §aTheTowers §fcopied to the backup folder §asuccessfully");
                    this.senderPlayer.remove(player);
                    return false;
                }
            }
            sender.sendMessage("§eUsage: §f/towers help");
            return false;
        }
        if (label.equals("towers") || label.equals("tower") || label.equals("tt")) {
            sender.sendMessage("§eUsage: §f/towers help");
        }
        else if (args[0].equalsIgnoreCase("backupWorld")) {
            sender.sendMessage("§eUsage: §f/towers backupWorld");
        }
        else if (args[0].equalsIgnoreCase("tpWorld")) {
            sender.sendMessage("§eUsage: §f/towers tpWorld <nameWorld>");
        }
        else if (args[0].equalsIgnoreCase("createWorld")) {
            sender.sendMessage("§eUsage: §f/towers createWorld <nameWorld> <emptyWorld>");
        }
        else if (args[0].equalsIgnoreCase("loadWorld")) {
            sender.sendMessage("§eUsage: §f/towers loadWorld <nameWorld>");
        }
        else if (args[0].equalsIgnoreCase("setspawn")) {
            sender.sendMessage("§eUsage: §f/towers setspawn <blue|red|lobby|iron|xpbottles|lapislazuli>");
        }
        else if (args[0].equalsIgnoreCase("setpool")) {
            sender.sendMessage("§eUsage: §f/towers setpool <blue|red>");
        }
        return true;
    }
}




