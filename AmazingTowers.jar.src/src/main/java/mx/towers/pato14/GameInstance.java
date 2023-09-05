package mx.towers.pato14;

import com.nametagedit.plugin.NametagEdit;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.items.GameLobbyItems;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.game.tasks.Dar;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.locations.CheckLocations;
import mx.towers.pato14.utils.locations.Locations;
import mx.towers.pato14.utils.rewards.SetupVault;
import mx.towers.pato14.utils.rewards.VaultT;
import mx.towers.pato14.utils.world.WorldLoad;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GameInstance extends TowersWorldInstance {
    private Game game;
    private VaultT vault;
    private final Map<Rule, Boolean> rules;
    private final int numberOfTeams;
    private final CheckLocations checkLocations;
    private final HashMap<String, PermissionAttachment> perms = new HashMap<>();
    private boolean isReadyToJoin;
    private Map.Entry<Boolean, List<String>> whitelist;
    private Map.Entry<Boolean, List<String>> blacklist;

    public GameInstance(String name) {
        super(name, GameInstance.class);
        isReadyToJoin = false;
        this.rules = new HashMap<>();
        updateLists();
        this.checkLocations = new CheckLocations(this);
        this.numberOfTeams = this.getConfig(ConfigType.CONFIG).getInt("teams.numberOfTeams");
        if (numberOfTeams < 2 || numberOfTeams > 8) {
            plugin.sendConsoleMessage("Error while creating " + name +
                    ", number of teams needs to be greater than 1 and less than 9 (currently " + numberOfTeams + ")", MessageType.ERROR);
            return;
        }
        this.checkLocations.checkNeededLocationsExistence(numberOfTeams);
        setRules();

        if (this.checkLocations.neededLocationsExist()) {
            if (plugin.getGlobalConfig().getBoolean("options.bungeecord.enabled")) {
                plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
            }
            if (overwriteWithBackup(name)) {
                this.vault = new VaultT(this);
                this.game = new Game(this);
                this.hotbarItems = new GameLobbyItems(this);
                getPlugin().getServer().getPluginManager().registerEvents(getHotbarItems(), getPlugin());
            }
        } else {
            plugin.sendConsoleMessage("Not all the locations have been set in " + name + ". Please set them first.", MessageType.WARNING);
        }
        isReadyToJoin = true;
    }

    @Override
    public GameLobbyItems getHotbarItems() {
        return (GameLobbyItems) hotbarItems;
    }

    public boolean overwriteWithBackup(String worldName) {   //Borra mundo de partida anterior y lo sobreescribe con el de backup
        WorldLoad towers = new WorldLoad(worldName, plugin.getDataFolder().getAbsolutePath() + "/backup/" + worldName, Bukkit.getWorldContainer().getAbsolutePath() + "/" + worldName);
        if (towers.getFileSource().exists()) {
            towers.loadWorld();
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

    public Map<Rule, Boolean> getRules() {
        return rules;
    }
    public int getNumberOfTeams() {
        return numberOfTeams;
    }
    public CheckLocations getDetectoreishon() {
        return checkLocations;
    }

    public HashMap<String, PermissionAttachment> getPermissions() {
        return this.perms;
    }

    @Override
    public void playerJoinGame(Player player) {
        super.playerJoinGame(player);
        Team team = this.getGame().getTeams().getTeamByPlayer(player.getName());
        AmazingTowers.getLobby().getHotbarItems().getSelectGameMenu().updateMenu(this);
        switch (this.getGame().getGameState()) {
            case LOBBY:
                if (this.getNumPlayers() >= this.getConfig(ConfigType.CONFIG).getInt("options.gameStart.minPlayers")) {
                    this.getGame().setGameState(GameState.PREGAME);
                    this.getGame().getStart().gameStart();
                }
            case PREGAME:
                Dar.joinGameLobby(player);
                break;
            case GAME:
                game.getTimer().addPlayer(player);
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
                Dar.joinGameLobby(player);
                break;
            default:
                if (team == null || team.isEliminated())
                    player.setGameMode(GameMode.SPECTATOR);
                break;
        }
    }

    @Override
    public void playerLeaveGame(Player player) {
        super.playerLeaveGame(player);
        final Team playerTeam = this.getGame().getTeams().getTeamByPlayer(player.getName());
        this.getPermissions().remove(player.getName());
        AmazingTowers.getLobby().getHotbarItems().getSelectGameMenu().updateMenu(this);

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
                        game.getTimer().removeBossBar(player.getName());
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

    public boolean canJoin(HumanEntity player) {
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
    @Override
    public void reset() {
        super.reset();
        isReadyToJoin = false;
        try {
            updateLists();
            setRules();
            this.game.reset();
            overwriteWithBackup(name);
            Utils.removeGlint(this.getHotbarItems().getModifyGameSettings().getSaveSettings());
            this.getHotbarItems().getModifyGameSettings().updateMenu();
            this.getHotbarItems().getModifyGameSettings().getSetRules().updateMenu(this);
        } finally {
            isReadyToJoin = true;
        }
    }
}