package mx.towers.pato14.game.team;

import com.nametagedit.plugin.NametagEdit;

import java.util.ArrayList;
import java.util.List;

import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.enums.TeamColor;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Team {
    private final TeamColor teamColor;
    private String prefix;
    private final String nameTeam;
    private final ArrayList<String> players;
    private final ArrayList<String> offlinePlayers;
    private int points;
    private final ItemStack lobbyItem;
    private final GameInstance gameInstance;

    public Team(String nameTeam, GameInstance gameInstance) {
        points = 0;
        this.nameTeam = nameTeam;
        this.players = new ArrayList<>();
        this.offlinePlayers = new ArrayList<>();
        this.teamColor = TeamColor.valueOf(nameTeam.toUpperCase());
        this.lobbyItem = teamColor.getTeamItem(gameInstance);
        this.gameInstance = gameInstance;
    }

    public void addPlayer(HumanEntity player) {
        this.players.add(player.getName());
        addPlayerNameToTeamItem(player.getName());
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

    public boolean containsPlayer(String name) {
        return this.players.contains(name);
    }
    public TeamColor getTeamColor() {
        return this.teamColor;
    }
    public String getNameTeam() {
        return this.nameTeam;
    }

    public String getPrefixTeam() {
        return this.prefix;
    }

    public ArrayList<String> getTeam() {
        return this.players;
    }

    protected void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public ArrayList<String> getPlayersOfflineTeam() {
        return this.offlinePlayers;
    }

    public boolean containsOffline(String namePlayer) {
        return this.offlinePlayers.contains(namePlayer);
    }

    public void addOfflinePlayer(String namePlayer) {
        if (!containsOffline(namePlayer)) {
            this.offlinePlayers.add(namePlayer);
        }
    }

    public void removeOfflinePlayer(String namePlayer) {
        if (containsOffline(namePlayer)) {
            this.offlinePlayers.remove(namePlayer);
            this.players.add(namePlayer);
        }
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void sumarPunto() {
        this.points++;
    }

    public ItemStack getLobbyItem() {
        return lobbyItem;
    }

    private void addPlayerNameToTeamItem(String playerName) {
        ItemMeta itemMeta = lobbyItem.getItemMeta();
        List<String> lore = itemMeta.getLore() == null ? new ArrayList<>() : itemMeta.getLore();
        lore.add("§r§7- " + playerName);
        itemMeta.setLore(lore);
        lobbyItem.setItemMeta(itemMeta);
        this.gameInstance.getGame().getLobbyItems().updateTeamsMenu();
    }

    private void removePlayerNameToTeamItem(String playerName) {
        ItemMeta itemMeta = lobbyItem.getItemMeta();
        List<String> lore = itemMeta.getLore();
        if (lore == null)
            return;
        lore.remove("§r§7- " + playerName);
        itemMeta.setLore(lore);
        lobbyItem.setItemMeta(itemMeta);
        this.gameInstance.getGame().getLobbyItems().updateTeamsMenu();
    }
}


