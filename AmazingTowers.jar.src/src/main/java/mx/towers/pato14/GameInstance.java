package mx.towers.pato14;

import com.nametagedit.plugin.NametagEdit;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.scoreboard.ScoreHelper;
import mx.towers.pato14.game.scoreboard.ScoreUpdate;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.game.tasks.Dar;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.locations.Detectoreishon;
import mx.towers.pato14.utils.locations.Locations;
import mx.towers.pato14.utils.rewards.SetupVault;
import mx.towers.pato14.utils.rewards.VaultT;
import mx.towers.pato14.utils.world.WorldLoad;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class GameInstance {
    private final AmazingTowers plugin;
    private World world;
    private Game game;
    private final Map<ConfigType, Config> configs;
    private ScoreUpdate scoreUpdate;
    private VaultT vault;
    private final Map<Rule, Boolean> rules;
    private int numPlayers;
    private final int numberOfTeams;
    private final Detectoreishon detectoreishon;
    private final String name;
    private boolean hasWorldAssociated;
    private final HashMap<String, PermissionAttachment> perms = new HashMap<>();
    private boolean isReadyToJoin;
    private Map.Entry<Boolean, List<String>> whitelist;
    private Map.Entry<Boolean, List<String>> blacklist;

    public GameInstance(AmazingTowers towers, String name) {
        isReadyToJoin = false;
        this.hasWorldAssociated = false;
        this.plugin = towers;
        this.name = name;
        this.configs = new HashMap<>();
        this.rules = new HashMap<>();
        this.numPlayers = 0;
        registerConfigs(name);
        this.detectoreishon = new Detectoreishon(this);
        this.numberOfTeams = this.getConfig(ConfigType.CONFIG).getInt("teams.numberOfTeams");
        if (numberOfTeams < 2 || numberOfTeams > 8) {
            plugin.sendConsoleMessage("Error while creating " + name +
                    ", number of teams needs to be greater than 1 and less than 9 (currently " + numberOfTeams + ")", MessageType.ERROR);
            return;
        }
        this.detectoreishon.checkNeededLocationsExistence(numberOfTeams);
        setRules();

        if (this.detectoreishon.neededLocationsExist()) {
            if (plugin.getGlobalConfig().getBoolean("options.bungeecord.enabled")) {
                plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
            }
            if (!overwriteWithBackup(name)) {
                this.world = loadWorld();
            } else {
                this.hasWorldAssociated = true;
                SetupVault.setupVault();
                this.vault = new VaultT(this);
                this.game = new Game(this);
                this.scoreUpdate = new ScoreUpdate(this);
            }
        } else {
            this.hasWorldAssociated = checkWorld();
            if (this.hasWorldAssociated)
                this.world = loadWorld();
            plugin.sendConsoleMessage("Not all the locations have been set in " + name + ". Please set them first.", MessageType.WARNING);
        }
        isReadyToJoin = true;
    }

    private boolean checkWorld() {
        File potentialWorld = new File(Bukkit.getServer().getWorldContainer(), name);
        return potentialWorld.exists();
    }

    private World loadWorld() {
        return Utils.createEmptyWorld(name);
    }

    private void registerConfigs(String worldName) {
        for (ConfigType config : ConfigType.values())
            this.configs.put(config, new Config(plugin,
                    Utils.macroCaseToCamelCase(config.name()) + ".yml", true, worldName));
    }

    public boolean overwriteWithBackup(String worldName) {   //Borra mundo de partida anterior y lo sobreescribe con el de backup
        WorldLoad towers = new WorldLoad(worldName, plugin.getDataFolder().getAbsolutePath() + "/backup/" + worldName, Bukkit.getWorldContainer().getAbsolutePath() + "/" + worldName);
        if (towers.getFileSource().exists()) {
            setWorld(towers.loadWorld());
            return true;
        } else {
            plugin.sendConsoleMessage("There is no backup for " + worldName, MessageType.ERROR);
            return false;
        }
    }

    private void setRules() {
        for (Rule rule : Rule.values())
            this.rules.put(rule, Boolean.parseBoolean(getConfig(ConfigType.GAME_SETTINGS).getString("rules." + Utils.macroCaseToCamelCase(rule.name()))));
    }
    public VaultT getVault() {
        return this.vault;
    }

    public Game getGame() {
        return this.game;
    }

    public Config getConfig(ConfigType config) {
        return this.configs.get(config);
    }
    public ScoreUpdate getScoreUpdates() {
        return this.scoreUpdate;
    }

    public Map<Rule, Boolean> getRules() {
        return rules;
    }

    public AmazingTowers getPlugin() {
        return plugin;
    }
    public int getNumPlayers() {
        return numPlayers;
    }
    public int getMaxPlayers() {
        return Bukkit.getMaxPlayers() - Bukkit.getOnlinePlayers().size() + numPlayers;
    }
    public void addPlayer() {
        ++numPlayers;
    }
    public void removePlayer() {
        --numPlayers;
    }
    public void setWorld(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }
    public int getNumberOfTeams() {
        return numberOfTeams;
    }
    public Detectoreishon getDetectoreishon() {
        return detectoreishon;
    }

    public String getName() {
        return name;
    }
    public void linkWorld(World worldToLink) {
        this.world = worldToLink;
    }

    public void broadcastMessage(String msg, boolean colorMessage) {
        if (colorMessage)
            msg = AmazingTowers.getColor(msg);
        for (Player player : world.getPlayers()) {
            player.sendMessage(msg);
        }
    }

    public boolean hasWorldAssociated() {
        return hasWorldAssociated;
    }

    public HashMap<String, PermissionAttachment> getPermissions() {
        return this.perms;
    }

    public void playerJoinGame(Player player) {
        Team team = this.getGame().getTeams().getTeamByPlayer(player.getName());
        this.addPlayer();
        this.getScoreUpdates().createScoreboard(player);
        this.getScoreUpdates().updateScoreboardAll();
        switch (this.getGame().getGameState()) {
            case LOBBY:
                if (this.getNumPlayers() >= this.getConfig(ConfigType.CONFIG).getInt("options.gameStart.minPlayers")) {
                    this.getGame().setGameState(GameState.PREGAME);
                    this.getGame().getStart().gameStart();
                }
            case PREGAME:
                Dar.joinLobby(player);
                break;
            case GAME:
                if (team != null) {
                    if (team.respawnPlayers()) {
                        team.setPlayerState(player.getName(), PlayerState.ONLINE);
                        Dar.joinTeam(player);
                    } else {
                        team.setPlayerState(player.getName(), PlayerState.NO_RESPAWN);
                        player.setGameMode(GameMode.SPECTATOR);
                        player.teleport(Locations.getLocationFromString(this.getConfig(ConfigType.LOCATIONS)
                                .getString(Location.LOBBY.getPath())), PlayerTeleportEvent.TeleportCause.COMMAND);
                    }
                    break;
                }
                Dar.joinLobby(player);
                break;
            default:
                if (team == null || team.isEliminated())
                    player.setGameMode(GameMode.SPECTATOR);
                break;
        }
        if (this.getConfig(ConfigType.CONFIG).getBoolean("options.mysql.active"))
            this.plugin.con.CreateAccount(player.getName());
    }

    public void playerLeaveGame(Player player) {
        final Team playerTeam = this.getGame().getTeams().getTeamByPlayer(player.getName());
        if (ScoreHelper.hasScore(player)) {
            ScoreHelper.removeScore(player);
        }
        this.getPermissions().remove(player.getName());
        this.removePlayer();

        (new BukkitRunnable() {
            public void run() {
                GameInstance.this.getScoreUpdates().updateScoreboardAll();
                switch (GameInstance.this.getGame().getGameState()) {
                    case LOBBY:
                    case PREGAME:
                        if (playerTeam != null) {
                            playerTeam.removePlayer(player);
                            NametagEdit.getApi().clearNametag(player);
                        }
                        break;
                    case GAME:
                    case GOLDEN_GOAL:
                        if (playerTeam == null)
                            break;
                        playerTeam.setPlayerState(player.getName(), playerTeam.respawnPlayers() ? PlayerState.OFFLINE : PlayerState.NO_RESPAWN);
                        if (playerTeam.getSizeOnlinePlayers() <= 0)
                            Utils.checkForTeamWin(GameInstance.this);
                        break;
                }
            }
        }).runTaskLaterAsynchronously(this.plugin, 5L);
    }

    public boolean canJoin(Player player) {
        return isReadyToJoin && (player.isOp() || (!whitelist.getKey() || whitelist.getValue().contains(player.getName()))
                && (!blacklist.getKey() || !blacklist.getValue().contains(player.getName())));
    }

    public void setReadyToJoin(boolean readyToJoin) {
        isReadyToJoin = readyToJoin;
    }

    public void updateWhiteList() {
        this.whitelist = new AbstractMap.SimpleEntry<>(Boolean.parseBoolean(getConfig(ConfigType.GAME_SETTINGS).getString("whitelist.activated")), getConfig(ConfigType.GAME_SETTINGS).getStringList("whitelist.players") == null ? new ArrayList<>() : getConfig(ConfigType.GAME_SETTINGS).getStringList("whitelist.players"));
    }
    public void updateBlackList() {
        this.blacklist = new AbstractMap.SimpleEntry<>(Boolean.parseBoolean(getConfig(ConfigType.GAME_SETTINGS).getString("blacklist.activated")), getConfig(ConfigType.GAME_SETTINGS).getStringList("blacklist.players") == null ? new ArrayList<>() : getConfig(ConfigType.GAME_SETTINGS).getStringList("blacklist.players"));
    }
    public void updateLists() {
        updateWhiteList();
        updateBlackList();
    }
}