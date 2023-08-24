package mx.towers.pato14.game.items.menus;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.items.ActionItem;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.game.tasks.Dar;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.*;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class JoinTeamItem extends ActionItem {
    private final TeamColor teamColor;
    private final GameInstance gameInstance;
    public JoinTeamItem(GameInstance gameInstance, TeamColor teamColor) {
        super(
                Utils.setName(new ItemStack(Material.WOOL, 1, teamColor.getWoolColor()),
                        teamColor.isMatchTeam() ? AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("lobbyItems.menuItems.joinTeam").replace("%team_color%", teamColor.getColor()).replace("%team_name%", teamColor.getName(gameInstance))) :
                                AmazingTowers.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("lobbyItems.menuItems.spectator")))
        );
        this.gameInstance = gameInstance;
        this.teamColor = teamColor;
    }
    @Override
    public void interact(HumanEntity player, TowersWorldInstance instance) {
        if (!(instance instanceof GameInstance))
            return;
        GameInstance gameInstance = (GameInstance) instance;
        Config messages = gameInstance.getConfig(ConfigType.MESSAGES);
        Game game = gameInstance.getGame();
        Team currentTeam = game.getTeams().getTeamByPlayer(player.getName()); //Equipo actual
        Team teamToJoin = game.getTeams().getTeam(teamColor);
        if (teamToJoin == null) {
            if (teamColor == TeamColor.SPECTATOR) {
                player.setGameMode(GameMode.SPECTATOR);
                player.sendMessage(AmazingTowers.getColor(messages.getString("enterSpectatorMode").replace("%newLine%", "\n")));
                if (currentTeam != null)
                    currentTeam.removePlayer(player);
                player.closeInventory();
            }
        } else if (!teamToJoin.containsPlayer(player.getName())) { //Si no está ya en ese equipo
            if (!gameInstance.getRules().get(Rule.BALANCED_TEAMS)
                    || teamToJoin.getSizePlayers() == game.getTeams().getLowestTeamPlayers()) {
                teamToJoin.addPlayer(player);
                if (game.getGameState().equals(GameState.GAME))
                    Dar.joinTeam((Player) player);
                player.sendMessage(AmazingTowers.getColor(messages.getString("selectTeam")
                        .replace("{Color}", teamToJoin.getTeamColor().getColor())
                        .replace("{Team}", teamToJoin.getTeamColor().getName(game.getGameInstance()))));
                ((Player) player).playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                player.closeInventory();
            } else {
                player.sendMessage(AmazingTowers.getColor(messages.getString("unbalancedTeam")));
                ((Player) player).playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0f, 1.0f);
            }
        } else {
            player.sendMessage(AmazingTowers.getColor(messages.getString("alreadyJoinedTeam")
                    .replace("{Color}", teamToJoin.getTeamColor().getColor())
                    .replace("{Team}", teamToJoin.getTeamColor().getName(game.getGameInstance()))));
        }
    }

    public static ItemStack[] createAllTeams(GameInstance gameInstance) {
        List<ItemStack> toret = new ArrayList<>();
        for (TeamColor team : TeamColor.getMatchTeams(gameInstance.getNumberOfTeams())) {
            toret.add(new JoinTeamItem(gameInstance, team));
        }
        toret.add(new JoinTeamItem(gameInstance, TeamColor.SPECTATOR));
        return toret.toArray(new ItemStack[0]);
    }

    public void addPlayerNameToTeamItem(String playerName) {
        ItemMeta itemMeta = this.getItemMeta();
        List<String> lore = itemMeta.getLore() == null ? new ArrayList<>() : itemMeta.getLore();
        lore.add("§r§7- " + playerName);
        itemMeta.setLore(lore);
        this.setItemMeta(itemMeta);
        this.gameInstance.getHotbarItems().getSelectTeam().updateMenu();
    }

    public void removePlayerNameToTeamItem(String playerName) {
        ItemMeta itemMeta = this.getItemMeta();
        List<String> lore = itemMeta.getLore();
        if (lore == null)
            return;
        lore.remove("§r§7- " + playerName);
        itemMeta.setLore(lore);
        this.setItemMeta(itemMeta);
        this.gameInstance.getHotbarItems().getSelectTeam().updateMenu();
    }

    public TeamColor getTeamColor() {
        return teamColor;
    }
}