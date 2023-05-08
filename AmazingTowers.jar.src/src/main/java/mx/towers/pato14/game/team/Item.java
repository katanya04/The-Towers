package mx.towers.pato14.game.team;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.utils.Dar;
import mx.towers.pato14.utils.ItemBuilder;
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

public class Item implements Listener {
    private final Map<TeamColor, ItemBuilder> teams;
    private final Game game;

    public Item(Game game) {
        this.game = game;
        this.at = game.getGameInstance().getPlugin();
        this.teams = new HashMap<>();
        for (TeamColor teamColor : TeamColor.getTeams(game.getNumberOfTeams())) {
            teams.put(teamColor, (new ItemBuilder(Material.WOOL, teamColor.getWoolColor())).setName(AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("Items.item" + teamColor.toString().toLowerCase().replace(teamColor.toString().toLowerCase().charAt(0), teamColor.toString().charAt(0)) + ".name"))));
        }
        if (this.game.getGameInstance().getConfig(ConfigType.CONFIG).getBoolean("Options.bungeecord-support.enabled"))
            this.quit = (new ItemBuilder(Material.BED)).setName(AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("Items.itemQuit.name")));
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
                            player.sendMessage(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.join" + teamColorToJoin.toString().toLowerCase())));
                        } else {
                            player.sendMessage(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.unbalancedTeam")));
                        }
                    } else {
                        player.sendMessage(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.alreadyJoined" + teamColorToJoin.toString().toLowerCase().replace(teamColorToJoin.toString().toLowerCase().charAt(0), teamColorToJoin.toString().charAt(0)) + "Team")));
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
        for (TeamColor teamColor : TeamColor.values()) {
            if (e.getCurrentItem().getType().equals(this.teams.get(teamColor).getItem().getType()) && e.getCurrentItem().getItemMeta().getDisplayName().equals(this.teams.get(teamColor).getItem().getItemMeta().getDisplayName()))
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

    private GameTeams getTeams() {
        return this.game.getTeams();
    }

    private AmazingTowers getPlugin() {
        return this.at;
    }

    public ItemBuilder getItem(TeamColor teamColor) {
        return this.teams.get(teamColor);
    }
}


