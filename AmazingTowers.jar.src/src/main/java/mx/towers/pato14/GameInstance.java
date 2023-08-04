package mx.towers.pato14;

import com.nametagedit.plugin.NametagEdit;
import mx.towers.pato14.commands.TowerCommand;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.scoreboard.ScoreHelper;
import mx.towers.pato14.game.scoreboard.ScoreUpdate;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.game.utils.Dar;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.Rule;
import mx.towers.pato14.utils.enums.TeamColor;
import mx.towers.pato14.utils.locations.Detectoreishon;
import mx.towers.pato14.utils.rewards.SetupVault;
import mx.towers.pato14.utils.rewards.VaultT;
import mx.towers.pato14.utils.world.WorldLoad;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public GameInstance(AmazingTowers towers, String name) {
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
            plugin.sendConsoleError("Error while creating " + name +
                    ", number of teams needs to be greater than 1 and less than 9 (currently " + numberOfTeams + ")");
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
            plugin.sendConsoleWarning("Not all the locations have been set in " + name + ". Please set them first.");
        }
    }

    private boolean checkWorld() {
        File potentialWorld = new File(Bukkit.getServer().getWorldContainer(), name);
        return potentialWorld.exists();
    }

    private World loadWorld() {
        return TowerCommand.createWorld(name);
    }

    private void registerConfigs(String worldName) {
        for (ConfigType config : ConfigType.values())
            this.configs.put(config, new Config(plugin,
                    config.toString().toLowerCase() + ".yml", true, worldName));
    }

    public boolean overwriteWithBackup(String worldName) {   //Borra mundo de partida anterior y lo sobreescribe con el de backup
        WorldLoad towers = new WorldLoad(worldName, plugin.getDataFolder().getAbsolutePath() + "/backup/" + worldName, Bukkit.getWorldContainer().getAbsolutePath() + "/" + worldName);
        if (towers.getFileSource().exists()) {
            setWorld(towers.loadWorld());
            return true;
        } else {
            plugin.sendConsoleError("There is no backup for " + worldName);
            return false;
        }
    }

    private void setRules() {   //Sets rules to default values
        for (Rule rule : Rule.values())
            this.rules.put(rule, rule.getCurrentState());
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
        Team team = this.getGame().getTeams().getTeamByPlayerIncludingOffline(player.getName());
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
                Dar.DarItemsJoin(player, GameMode.ADVENTURE);
                break;
            case GAME:
                if (team != null) {
                    team.removeOfflinePlayer(player.getName());
                    Dar.darItemsJoinTeam(player);
                    break;
                }
                Dar.DarItemsJoin(player, GameMode.ADVENTURE);
                break;
            case FINISH:
                break;
            default:
        }
        if (this.getConfig(ConfigType.CONFIG).getBoolean("options.mysql.active"))
            this.plugin.con.CreateAccount(player.getName());
    }

    public void playerLeaveGame(Player player) {
        final Team playerTeam = this.getGame().getTeams().getTeamByPlayer(player);
        if (ScoreHelper.hasScore(player)) {
            ScoreHelper.removeScore(player);
        }
        this.getPermissions().remove(player.getName());
        this.removePlayer();

        (new BukkitRunnable() {
            public void run() {
                GameInstance.this.getScoreUpdates().updateScoreboardAll();
                List<Team> teams = GameInstance.this.getGame().getTeams().getTeams();
                switch (GameInstance.this.getGame().getGameState()) {
                    case LOBBY:
                    case PREGAME:
                        if (playerTeam != null) {
                            playerTeam.removePlayer(player);
                            NametagEdit.getApi().clearNametag(player);
                        }
                        break;
                    case GAME:
                        if (playerTeam == null) {
                            break;
                        }
                        playerTeam.addOfflinePlayer(player.getName());
                        playerTeam.removePlayer(player);
                        boolean makeATeamWin = true;
                        Team temp = null;
                        for (Team team : teams) {
                            if (team.getSizePlayers() > 0) {
                                if (temp == null)
                                    temp = team;
                                else
                                    makeATeamWin = false;
                            }
                        }
                        if (makeATeamWin) {
                            if (temp != null)
                                GameInstance.this.getGame().getFinish().Fatality(temp.getTeamColor());
                            else {
                                int numberOfTeams = teams.size();
                                int numero = (int) Math.floor(Math.random() * numberOfTeams);
                                GameInstance.this.getGame().getFinish().Fatality(TeamColor.values()[numero]);
                            }
                        }
                        break;
                }
            }
        }).runTaskLaterAsynchronously(this.plugin, 5L);
    }
}