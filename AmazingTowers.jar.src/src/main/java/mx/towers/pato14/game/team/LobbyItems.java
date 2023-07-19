package mx.towers.pato14.game.team;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.utils.Dar;
import mx.towers.pato14.utils.ItemAux;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.Rule;
import mx.towers.pato14.utils.enums.TeamColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

public class LobbyItems implements Listener {
    private final Map<TeamColor, ItemAux> teams;
    private final Game game;

    public LobbyItems(Game game) {
        this.game = game;
        this.at = game.getGameInstance().getPlugin();
        this.teams = new HashMap<>();
        for (TeamColor teamColor : TeamColor.getTeams(game.getGameInstance().getNumberOfTeams())) {
            teams.put(teamColor, (new ItemAux(Material.WOOL, teamColor.getWoolColor())).setName(AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("Items.item" + teamColor.firstCapitalized() + ".name"))));
        }
        if (this.game.getGameInstance().getConfig(ConfigType.CONFIG).getBoolean("Options.bungeecord-support.enabled"))
            this.quit = (new ItemAux(Material.BED)).setName(AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("Items.itemQuit.name")));
    }

    private ItemAux quit;
    private final AmazingTowers at;

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (game.getGameState().equals(GameState.FINISH)) {
            return;
        }
        Player player = e.getPlayer();
        if (e.getItem() != null && (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            for (TeamColor teamColorToJoin : TeamColor.values()) {
                if (e.getItem().getType().equals(this.teams.get(teamColorToJoin).getItem().getType()) && e.getItem().getItemMeta().getDisplayName().equals(this.teams.get(teamColorToJoin).getItem().getItemMeta().getDisplayName())) {
                    if (!getTeams().getTeam(teamColorToJoin).containsPlayer(player.getName())) { //Si no está ya en ese equipo
                        mx.towers.pato14.game.team.Team currentTeam = game.getTeams().getTeamByPlayer(player); //Equipo actual
                        if (!teamColorToJoin.isMatchTeam()
                                || !game.getGameInstance().getRules().get(Rule.BALANCED_TEAMS)
                                || getTeams().getTeam(teamColorToJoin).getSizePlayers() == game.getTeams().getLowestTeamPlayers()) {
                            if (currentTeam != null) currentTeam.removePlayer(player);
                            if (teamColorToJoin.isMatchTeam()) {
                                getTeams().getTeam(teamColorToJoin).addPlayer(player);
                                if (game.getGameState().equals(GameState.GAME))
                                    Dar.darItemsJoinTeam(player);
                            } else {
                                player.setGameMode(GameMode.SPECTATOR);
                            }
                            player.sendMessage(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.join")
                                    .replace("{Color}", teamColorToJoin.getColor())
                                    .replace("{Team}", teamColorToJoin.getName(game.getGameInstance()))));
                        } else {
                            player.sendMessage(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.unbalancedTeam")));
                        }
                    } else {
                        player.sendMessage(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.alreadyJoinedTeam")
                                .replace("{Color}", teamColorToJoin.getColor())
                                .replace("{Team}", teamColorToJoin.getName(game.getGameInstance()))));
                    }
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent e) {
        if (e.getInventory() == null || e.getClickedInventory() == null || e.getCurrentItem().getType() == null) {
            return;
        }
        if (this.quit != null && e.getCurrentItem().getType().equals(this.quit.getItem().getType()) && e.getCurrentItem().getItemMeta().getDisplayName().equals(this.quit.getItem().getItemMeta().getDisplayName())) {
            e.setCancelled(true);
        } else if (this.game.getItemBook() != null && e.getCurrentItem().getType().equals(this.game.getItemBook().getItem().getType()) && e.getCurrentItem().getItemMeta().getDisplayName().equals(this.game.getItemBook().getItem().getItemMeta().getDisplayName())) {
            e.setCancelled(true);
        }
    }

    public ItemAux getItemQuit() {
        return this.quit;
    }

    private GameTeams getTeams() {
        return this.game.getTeams();
    }

    private AmazingTowers getPlugin() {
        return this.at;
    }

    public ItemAux getItem(TeamColor teamColor) {
        return this.teams.get(teamColor);
    }
}


