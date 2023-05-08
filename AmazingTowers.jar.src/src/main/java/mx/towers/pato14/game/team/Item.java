package mx.towers.pato14.game.team;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.utils.Dar;
import mx.towers.pato14.utils.ItemBuilder;
import mx.towers.pato14.utils.enums.ConfigType;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.Rule;
import mx.towers.pato14.utils.enums.Team;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Item implements Listener {
    private final Map<Team, ItemBuilder> teams;
    private final Game game;

    public Item(Game game) {
        this.game = game;
        this.at = game.getGameInstance().getPlugin();
        this.teams = new HashMap<>();
        for (mx.towers.pato14.utils.enums.Team team : mx.towers.pato14.utils.enums.Team.values()) {
            teams.put(team, (new ItemBuilder(Material.WOOL, team.getWoolColor())).setName(AmazingTowers.getColor(getPlugin().getConfig().getString("Items.item" + team.toString().toLowerCase().replace(team.toString().toLowerCase().charAt(0), team.toString().charAt(0)) + ".name"))));
        }
        if (this.game.getGameInstance().getConfig(ConfigType.CONFIG).getBoolean("Options.bungeecord-support.enabled"))
            this.quit = (new ItemBuilder(Material.BED)).setName(AmazingTowers.getColor(getPlugin().getConfig().getString("Items.itemQuit.name")));
    }

    private ItemBuilder quit;
    private final AmazingTowers at;

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (game.getGameState().equals(GameState.FINISH)) {
            return;
        }
        Player player = e.getPlayer();
        if (e.getItem() != null && (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            for (Team teamToJoin : Team.values()) {
                if (e.getItem().getType().equals(this.teams.get(teamToJoin).getItem().getType()) && e.getItem().getItemMeta().getDisplayName().equals(this.teams.get(teamToJoin).getItem().getItemMeta().getDisplayName())) {
                    if (!getTeams().getTeam(teamToJoin).containsPlayer(player.getName())) { //Si no está ya en ese equipo
                        mx.towers.pato14.game.team.Team currentTeam = game.getTeams().getTeamByPlayer(player); //Equipo actual
                        if (!teamToJoin.isMatchTeam()
                                || !game.getGameInstance().getRules().get(Rule.BALANCED_TEAMS)
                                || getTeams().getTeam(teamToJoin).getSizePlayers() == game.getTeams().getLowestTeamPlayers()) {
                            if (currentTeam != null) currentTeam.removePlayer(player);
                            if (teamToJoin.isMatchTeam()) {
                                getTeams().getTeam(teamToJoin).addPlayer(player);
                                if (game.getGameState().equals(GameState.GAME))
                                    Dar.darItemsJoinTeam(player);
                            } else {
                                player.setGameMode(GameMode.SPECTATOR);
                            }
                            player.sendMessage(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.join" + teamToJoin.toString().toLowerCase())));
                        } else {
                            player.sendMessage(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.unbalancedTeam")));
                        }
                    } else {
                        player.sendMessage(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.alreadyJoined" + teamToJoin.toString().toLowerCase().replace(teamToJoin.toString().toLowerCase().charAt(0), teamToJoin.toString().charAt(0)) + "Team")));
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
        for (Team team : Team.values()) {
            if (e.getCurrentItem().getType().equals(this.teams.get(team).getItem().getType()) && e.getCurrentItem().getItemMeta().getDisplayName().equals(this.teams.get(team).getItem().getItemMeta().getDisplayName()))
                e.setCancelled(true);
        }
        if (this.quit != null && e.getCurrentItem().getType().equals(this.quit.getItem().getType()) && e.getCurrentItem().getItemMeta().getDisplayName().equals(this.quit.getItem().getItemMeta().getDisplayName())) {
            e.setCancelled(true);
        } else if (this.game.getItemBook() != null && e.getCurrentItem().getType().equals(this.game.getItemBook().getItem().getType()) && e.getCurrentItem().getItemMeta().getDisplayName().equals(this.game.getItemBook().getItem().getItemMeta().getDisplayName())) {
            e.setCancelled(true);
        }
    }

    public ItemBuilder getItemQuit() {
        return this.quit;
    }

    private TeamGame getTeams() {
        return this.game.getTeams();
    }

    private AmazingTowers getPlugin() {
        return this.at;
    }

    public ItemBuilder getItem(Team team) {
        return this.teams.get(team);
    }
}


