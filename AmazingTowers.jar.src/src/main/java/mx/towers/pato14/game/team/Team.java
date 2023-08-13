package mx.towers.pato14.game.team;

import com.nametagedit.plugin.NametagEdit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.enums.PlayerState;
import mx.towers.pato14.utils.enums.Rule;
import mx.towers.pato14.utils.enums.TeamColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Team {
    private final TeamColor teamColor;
    private String prefix;
    private final HashMap<String, PlayerState> players;
    private int points;
    private final ItemStack lobbyItem;
    private final GameInstance gameInstance;

    public Team(TeamColor teamColor, GameInstance gameInstance) {
        points = 0;
        this.players = new HashMap<>();
        this.teamColor = teamColor;
        this.lobbyItem = teamColor.getTeamItem(gameInstance);
        this.gameInstance = gameInstance;
    }

    public void removePlayer(HumanEntity player) {
        this.players.remove(player.getName());
        removePlayerNameToTeamItem(player.getName());
    }

    public void setNameTagPlayer(Player player) {
        NametagEdit.getApi().setPrefix(player, ChatColor.translateAlternateColorCodes('&', this.prefix));
    }

    public void ClearNameTagPlayer(Player player) {
        NametagEdit.getApi().clearNametag(player);
    }

    public int getSizePlayers() {
        return this.players.size();
    }
    public int getSizeOnlinePlayers() {
        int i = 0;
        for (Map.Entry<String, PlayerState> player : players.entrySet()) {
            if (player.getValue() == PlayerState.ONLINE)
                i++;
        }
        return i;
    }

    public boolean containsPlayer(String name) {
        return this.players.get(name) != null;
    }

    public boolean containsPlayerOnline(String name) {
        return this.players.get(name) == PlayerState.ONLINE;
    }
    public TeamColor getTeamColor() {
        return this.teamColor;
    }

    public String getPrefixTeam() {
        return this.prefix;
    }

    protected void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void scorePoint(boolean bedwarsStyle) {
        if (bedwarsStyle)
            this.points--;
        else
            this.points++;
    }

    public ItemStack getLobbyItem() {
        return lobbyItem;
    }

    public void addPlayerNameToTeamItem(String playerName) {
        ItemMeta itemMeta = lobbyItem.getItemMeta();
        List<String> lore = itemMeta.getLore() == null ? new ArrayList<>() : itemMeta.getLore();
        lore.add("§r§7- " + playerName);
        itemMeta.setLore(lore);
        lobbyItem.setItemMeta(itemMeta);
        this.gameInstance.getGame().getLobbyItems().updateTeamsMenu();
    }

    public void removePlayerNameToTeamItem(String playerName) {
        ItemMeta itemMeta = lobbyItem.getItemMeta();
        List<String> lore = itemMeta.getLore();
        if (lore == null)
            return;
        lore.remove("§r§7- " + playerName);
        itemMeta.setLore(lore);
        lobbyItem.setItemMeta(itemMeta);
        this.gameInstance.getGame().getLobbyItems().updateTeamsMenu();
    }

    public boolean respawnPlayers() {
        return !gameInstance.getRules().get(Rule.BEDWARS_STYLE) || this.getPoints() != 0;
    }

    public void setPlayerState(String playerName, PlayerState playerState) {
        this.players.put(playerName, playerState);
    }

    public PlayerState getPlayerState(String playerName) {
        return this.players.get(playerName);
    }

    public List<Player> getListOnlinePlayers() {
        List<Player> toret = new ArrayList<>();
        for (String playerName : players.keySet()) {
            toret.add(Bukkit.getPlayer(playerName));
        }
        return toret;
    }
}