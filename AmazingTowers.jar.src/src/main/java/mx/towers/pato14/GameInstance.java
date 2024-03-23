package mx.towers.pato14;

import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.items.GameLobbyItems;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.rewards.VaultT;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.io.IOException;
import java.util.*;

public class GameInstance extends TowersWorldInstance {
    private Game game;
    private VaultT vault;
    private final Map<Rule, Boolean> rules;
    private final int numberOfTeams;
    private final HashMap<String, PermissionAttachment> perms = new HashMap<>();
    private Map.Entry<Boolean, List<String>> whitelist;
    private Map.Entry<Boolean, List<String>> blacklist;
    private final List<String> nonExistentLocations;
    private String dbTableName;

    public GameInstance(String name) {
        super(name, GameInstance.class);
        this.rules = new HashMap<>();
        this.nonExistentLocations = new ArrayList<>();
        updateLists();
        this.numberOfTeams = this.getConfig(ConfigType.CONFIG).getInt("teams.numberOfTeams");
        if (numberOfTeams < 2 || numberOfTeams > 8) {
            Utils.sendConsoleMessage("Error while creating " + name +
                    ", number of teams needs to be greater than 1 and less than 9 (currently " + numberOfTeams + ")", MessageType.ERROR);
            return;
        }
        setRules();
        checkNeededLocationsExistence(this.numberOfTeams);
        if (AmazingTowers.isConnectedToDatabase()) {
            dbTableName = getConfig(ConfigType.GAME_SETTINGS).getString("database.database");
            if (!Utils.isAValidTable(dbTableName))
                dbTableName = null;
        }
        if (this.nonExistentLocations.isEmpty()) {
            if (AmazingTowers.getGlobalConfig().getBoolean("options.bungeecord.enabled"))
                plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
            if (overwriteWithBackup(name)) {
                this.vault = new VaultT(this);
                this.game = new Game(this);
                this.hotbarItems = new GameLobbyItems(this);
                getPlugin().getServer().getPluginManager().registerEvents(getHotbarItems(), getPlugin());
            }
        } else {
            Utils.sendConsoleMessage("Not all the locations have been set in " + name + ". Please set them first.", MessageType.WARNING);
        }
        state = State.READY;
    }

    @Override
    public GameLobbyItems getHotbarItems() {
        return (GameLobbyItems) hotbarItems;
    }

    public boolean overwriteWithBackup(String worldName) {   //Borra mundo de partida anterior y lo sobreescribe con el de backup
        try {
            if (!Utils.replaceWithBackup(plugin.getDataFolder().getAbsolutePath() + "/backup/" + worldName,
                    Bukkit.getWorldContainer().getAbsolutePath() + "/" + worldName)) {
                Utils.sendConsoleMessage("There is no backup for " + worldName, MessageType.ERROR);
                return false;
            }
        } catch (IOException ex) {
            Utils.sendConsoleMessage("I/O error when overwritting " + worldName + " with its backup", MessageType.ERROR);
            return false;
        }
        this.setWorldProperties(getWorld());
        return true;
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

    public HashMap<String, PermissionAttachment> getPermissions() {
        return this.perms;
    }

    @Override
    public void joinInstance(Player player) {
        super.joinInstance(player);
        if (game == null)
            return;
        AmazingTowers.getLobby().getHotbarItems().getSelectGameMenu().updateMenu(this);
        if (this.game.getGameState() == GameState.LOBBY && this.getNumPlayers() >=
                this.getConfig(ConfigType.CONFIG).getInt("options.gameStart.minPlayers")) {
            this.game.start();
        }
        game.joinGame(player);
        Arrays.sort(AmazingTowers.getGameInstances(), Collections.reverseOrder());
    }

    @Override
    public void leaveInstance(Player player) {
        super.leaveInstance(player);
        if (game == null)
            return;
        this.getPermissions().remove(player.getName());
        AmazingTowers.getLobby().getHotbarItems().getSelectGameMenu().updateMenu(this);
        game.leave(player);
        Arrays.sort(AmazingTowers.getGameInstances(), Collections.reverseOrder());
    }

    public boolean canJoin(HumanEntity player) {
        return isReady() && (player.isOp() || (!whitelist.getKey() || whitelist.getValue().contains(player.getName()))
                && (!blacklist.getKey() || !blacklist.getValue().contains(player.getName())));
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
        try {
            reloadAllConfigs();
            updateLists();
            setRules();
            this.dbTableName = getConfig(ConfigType.GAME_SETTINGS).getString("database.database");
            if (!Utils.isAValidTable(this.dbTableName))
                this.dbTableName = null;
            this.game.reset();
            overwriteWithBackup(internalName);
            this.getGame().getRefill().resetTime();
            this.getHotbarItems().reset(this);
        } finally {
            state = State.READY;
        }
    }

    private void checkNeededLocationsExistence(int numberOfTeams) {
        for (Location loc : Location.getObligatoryLocations()) {
            if (!loc.needsTeamColor()) {
                if (!loc.exists(this, null))
                    this.nonExistentLocations.add(loc.name().toLowerCase().replace('_', ' '));
            } else {
                for (TeamColor teamColor : TeamColor.getMatchTeams(numberOfTeams))
                    if (!loc.exists(this, teamColor))
                        this.nonExistentLocations.add(teamColor.name().toLowerCase() + " " + loc.name().toLowerCase().replace('_', ' '));
            }
        }
    }

    public boolean hasUnsetRegions() {
        return !nonExistentLocations.isEmpty();
    }

    public String getUnsetRegionsString() {
        return Utils.listToCommaSeparatedString(nonExistentLocations);
    }

    public String getTableName() {
        return dbTableName;
    }

    public void setTableName(String dbTableName) {
        this.dbTableName = dbTableName;
    }

    @Override
    public void setWorldProperties(World world) {
        super.setWorldProperties(world);
        world.setAutoSave(false);
    }
}