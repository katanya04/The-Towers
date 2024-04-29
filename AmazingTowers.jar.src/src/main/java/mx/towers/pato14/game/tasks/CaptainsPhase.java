package mx.towers.pato14.game.tasks;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.team.GameTeams;
import mx.towers.pato14.game.team.TeamColor;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.MessageType;
import mx.towers.pato14.utils.items.Items;
import mx.towers.pato14.utils.items.ItemsEnum;
import mx.towers.pato14.utils.items.Skulls;
import mx.towers.pato14.utils.mysql.Callback;
import mx.towers.pato14.utils.mysql.IConnexion;
import mx.towers.pato14.utils.stats.Rank;
import mx.towers.pato14.utils.stats.StatType;
import mx.towers.pato14.utils.stats.Stats;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CaptainsPhase {
    private boolean concluded;
    private final Map<TeamColor, String> captains;
    private final Map<TeamColor, Boolean> ready;
    private Predicate<Player> captainCondition;
    private final String worldName;
    private TeamColor currentTurn;
    private final Set<String> playersToChoose;

    public CaptainsPhase(GameInstance gameInstance) {
        this.concluded = false;
        this.captains = new HashMap<>();
        this.ready = new HashMap<>();
        this.worldName = gameInstance.getInternalName();
        this.playersToChoose = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        this.captainCondition = pl -> !Boolean.parseBoolean(gameInstance.getConfig(ConfigType.GAME_SETTINGS).getString(
                "possibleCaptains.activated")) || Utils.getConfSafeList(gameInstance.getConfig(ConfigType.GAME_SETTINGS),
                "possibleCaptains.players").contains(pl.getName());
    }

    private Player getRandomCaptain(GameInstance game) {
        List<Player> players = game.getWorld().getPlayers().stream()
                .filter(o -> !this.captains.containsValue(o.getName())).collect(Collectors.toList());
        return players.get(new Random().nextInt(players.size()));
    }

    public void initialize() {
        GameInstance game = AmazingTowers.getGameInstance(this.worldName);
        this.concluded = false;
        captains.clear();
        ready.clear();
        List<TeamColor> teams = TeamColor.getMatchTeams(game.getNumberOfTeams());
        this.playersToChoose.addAll(game.getWorld().getPlayers().stream().filter(o -> o.getGameMode() != GameMode.SPECTATOR).map(HumanEntity::getName).collect(Collectors.toSet()));
        if (this.playersToChoose.size() - teams.size() <= 0 || game.getWorld().getPlayers().stream().filter(o -> o.getGameMode() != GameMode.SPECTATOR).count() < teams.size()) {
            game.broadcastMessage(game.getConfig(ConfigType.MESSAGES).getString("notEnoughPlayersCaptains"), true);
            conclude(true);
            return;
        }
        List<Player> validPlayers = game.getWorld().getPlayers().stream().filter(o -> o.getGameMode() != GameMode.SPECTATOR
                && (this.captainCondition == null || this.captainCondition.test(o))).collect(Collectors.toList());
        Collections.shuffle(validPlayers);
        Iterator<Player> playersItr = validPlayers.iterator();
        for (TeamColor team : teams) {
            Player captain = playersItr.hasNext() ? playersItr.next() : getRandomCaptain(game);
            this.captains.put(team, captain.getName());
            game.broadcastMessage(game.getConfig(ConfigType.MESSAGES).getString("captainSelected")
                    .replace("{Player}", captain.getName())
                    .replace("{Color}", team.getColor())
                    .replace("{Team}", team.getName(game)),
                    true);
            this.ready.put(team, false);
            game.getGame().getTeams().getTeam(team).addPlayer(captain.getName());
            Bukkit.getPlayer(captain.getName()).getInventory().setItem(game.getConfig(ConfigType.CONFIG).getInt(
                    "lobbyItems.hotbarItems.selectPlayers.position"), Items.getAndParse(ItemsEnum.SELECT_PLAYERS, captain));
            removePlayer(captain.getName());
        }
        game.getWorld().getPlayers().stream().filter(Player::isOp).forEach(o -> o.getInventory().setItem(game.getConfig(ConfigType.CONFIG).getInt(
                "lobbyItems.hotbarItems.selectPlayers.position"), Items.getAndParse(ItemsEnum.SELECT_PLAYERS, o)));
        this.currentTurn = Utils.getRandomSetElement(captains.keySet());
        sendMsgTurn();
    }

    public void setPlayerList(boolean clearPrevious) {
        GameInstance game = AmazingTowers.getGameInstance(this.worldName);
        if (clearPrevious)
            playersToChoose.clear();
        playersToChoose.addAll(game.getWorld().getPlayers().stream().filter(p -> p.getGameMode() != GameMode.SPECTATOR
                && !this.captains.containsValue(p.getName())).map(HumanEntity::getName).collect(Collectors.toSet()));
        playersToChoose.forEach(Skulls::cachePlayerHead);
        Callback.findPlayerAsync(playersToChoose, Collections.singletonList(IConnexion.ALL_TABLES), cache::putAll);
    }

    public TeamColor getCurrentTurn() {
        return currentTurn;
    }

    public void choosePlayer(Player captain, String choosenName) {
        GameInstance game = AmazingTowers.getGameInstance(this.worldName);
        if (game.getGame().getGameState() != GameState.CAPTAINS_CHOOSE)
            return;
        GameTeams teams = game.getGame().getTeams();
        TeamColor captainColor = teams.getTeamColorByPlayer(captain.getName());
        if (!Objects.equals(this.captains.get(captainColor), captain.getName())) { //this should not happen
            Utils.sendMessage("You are not a captain", MessageType.ERROR, captain);
            return;
        }
        if (captainColor != this.currentTurn) {
            Utils.sendMessage(game.getConfig(ConfigType.MESSAGES).getString("notYourTurn"), MessageType.ERROR, captain);
            return;
        }
        if (teams.getTeamColorByPlayer(choosenName) != null) { //this should not happen
            Utils.sendMessage("That player already has a team", MessageType.ERROR, captain);
            return;
        }
        removePlayer(choosenName);
        teams.getTeam(captainColor).addPlayer(choosenName);
        Items.updateMenu(ItemsEnum.SELECT_PLAYERS);
        game.broadcastMessage(game.getConfig(ConfigType.MESSAGES).getString("choosePlayer")
                        .replace("{Color}", captainColor.getColor())
                        .replace("{Captain}", captain.getName())
                        .replace("{Player}", choosenName),
                true);
        if (!playersToChoose.isEmpty()) {
            this.currentTurn = getNext();
            sendMsgTurn();
        }
    }

    public boolean hasConcluded() {
        return concluded;
    }

    public void conclude(boolean startImmediately) {
        this.currentTurn = null;
        this.concluded = true;
        GameInstance game = AmazingTowers.getGameInstance(this.worldName);
        game.getGame().setGameState(GameState.PREGAME);
        game.getGame().getStart().afterCaptainsChoose(startImmediately);
    }

    public TeamColor getNext() {
        GameInstance game = AmazingTowers.getGameInstance(this.worldName);
        List<TeamColor> teams = TeamColor.getMatchTeams(game.getNumberOfTeams());
        return teams.get((teams.indexOf(this.currentTurn) + 1) % game.getNumberOfTeams());
    }

    public Set<String> getPlayersToChoose() {
        return this.playersToChoose;
    }

    public void setCaptainCondition(Predicate<Player> captainCondition) {
        this.captainCondition = captainCondition;
    }

    public void reset() {
        this.concluded = false;
        captains.clear();
        ready.clear();
        playersToChoose.clear();
        cache.clear();
    }

    public boolean isCaptain(String player) {
        GameInstance game = AmazingTowers.getGameInstance(this.worldName);
        TeamColor team = game.getGame().getTeams().getTeamColorByPlayer(player);
        if (team == null)
            return false;
        return Objects.equals(this.captains.get(team), player);
    }

    public void setReady(TeamColor team) {
        GameInstance game = AmazingTowers.getGameInstance(this.worldName);
        this.ready.put(team, true);
        if (this.ready.values().stream().reduce((n, m) -> n && m).get())
            game.getGame().getStart().setCountDown(5);
    }

    public boolean isReady(TeamColor team) {
        return this.ready.get(team);
    }

    private void sendMsgTurn() {
        GameInstance game = AmazingTowers.getGameInstance(this.worldName);
        game.broadcastMessage(game.getConfig(ConfigType.MESSAGES).getString("currentTurn")
                        .replace("{Color}", this.currentTurn.getColor())
                        .replace("{Captain}", this.captains.get(this.currentTurn)),
                true);
    }

    public void addPlayer(String... players) {
        for (String player : players) {
            this.playersToChoose.add(player);
            Skulls.cachePlayerHead(player);
        }
        Callback.findPlayerAsync(Arrays.asList(players), Collections.singletonList(IConnexion.ALL_TABLES), cache::putAll);
    }

    public void removePlayer(String... players) {
        for (String player : players) {
            this.playersToChoose.remove(player);
            this.cache.remove(player);
        }
        if (playersToChoose.isEmpty())
            conclude(false);
    }

    private String getCorrectCase(String incorrectCase) {
        for (String string : playersToChoose)
            if (string.equalsIgnoreCase(incorrectCase))
                return string;
        return incorrectCase;
    }

    private final int CACHE_SIZE = 100;
    private final LinkedHashMap<String, Stats> cache = new LinkedHashMap<String, Stats>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Stats> eldest) {
            return size() > CACHE_SIZE;
        }
    };

    public void setHeads(Inventory inv) {
        Set<String> playersNotInCache = playersToChoose.stream().filter(o -> !cache.containsKey(o)).collect(Collectors.toSet());
        setHead(inv, cache);
        if (playersNotInCache.isEmpty())
            return;
        Callback.findPlayerAsync(playersNotInCache, Collections.singletonList(IConnexion.ALL_TABLES), result -> {
            this.cache.putAll(result);
            setHead(inv, result);
        });
    }
    private void setHead(Inventory inv, Map<String, Stats> stats) {
        for (int slot = 0; slot < inv.getSize(); slot++) {
            ItemStack playerHead = inv.getItem(slot);
            if (playerHead == null || playerHead.getType() != Material.SKULL_ITEM)
                continue;
            String playerName = getCorrectCase(Skulls.getPlayerByHead(playerHead));
            if (playerName == null || !stats.containsKey(playerName))
                continue;
            List<String> lore = new ArrayList<>();
            Stats stat = stats.get(playerName);
            lore.add("§r" + StatType.KILLS.getText() + ": " + StatType.KILLS.getColor() + stat.getStat(StatType.KILLS));
            lore.add("§r" + StatType.DEATHS.getText() + ": " + StatType.DEATHS.getColor() + stat.getStat(StatType.DEATHS));
            lore.add("§r" + StatType.POINTS.getText() + ": " + StatType.POINTS.getColor() + stat.getStat(StatType.POINTS));
            lore.add("§r" + StatType.GAMES_PLAYED.getText() + ": " + StatType.GAMES_PLAYED.getColor() + stat.getStat(StatType.GAMES_PLAYED));
            lore.add("§r" + StatType.WINS.getText() + ": " + StatType.WINS.getColor() + stat.getStat(StatType.WINS));
            Utils.setLore(playerHead, lore);
            Rank rank = Rank.getTotalRank(stat);
            Utils.setName(playerHead, "§r§l" + playerName + "§r: " + rank.getColor() + "§l" + rank.name());
            inv.setItem(slot, playerHead);
        }
    }
}
