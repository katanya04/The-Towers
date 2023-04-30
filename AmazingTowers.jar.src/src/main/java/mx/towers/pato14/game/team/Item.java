package mx.towers.pato14.game.team;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.utils.Dar;
import mx.towers.pato14.utils.ItemBuilder;
import mx.towers.pato14.utils.enums.GameState;
import mx.towers.pato14.utils.enums.Rule;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Item implements Listener {
    private ItemBuilder teamBlue;
    private ItemBuilder teamRed;
    private ItemBuilder spectator;

    public Item(AmazingTowers plugin) {
        this.at = plugin;
        this.teamRed = (new ItemBuilder(Material.WOOL, Short.valueOf((short) 14))).setName(getPlugin().getColor(getPlugin().getConfig().getString("Items.itemRed.name")));
        this.teamBlue = (new ItemBuilder(Material.WOOL, Short.valueOf((short) 11))).setName(getPlugin().getColor(getPlugin().getConfig().getString("Items.itemBlue.name")));
        this.spectator = (new ItemBuilder(Material.WOOL, Short.valueOf((short) 8))).setName(getPlugin().getColor(getPlugin().getConfig().getString("Items.itemSpectator.name")));
        if (this.at.getConfig().getBoolean("Options.bungeecord-support.enabled"))
            this.quit = (new ItemBuilder(Material.BED)).setName(getPlugin().getColor(getPlugin().getConfig().getString("Items.itemQuit.name")));
    }

    private ItemBuilder quit;
    private AmazingTowers at;

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (GameState.isState(GameState.FINISH)) {
            return;
        }
        Player player = e.getPlayer();
        if (e.getItem() != null && e.getItem().getType().equals(this.teamRed.getItem().getType()) && e.getItem().getItemMeta().getDisplayName().equals(this.teamRed.getItem().getItemMeta().getDisplayName()) && (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            if (GameState.isState(GameState.FINISH)) {
                return;
            }
            if (!getTeams().getRed().containsPlayer(player.getName())) {
                if (getTeams().getBlue().containsPlayer(player.getName())) {
                    if (!Rule.BALANCED_TEAMS.getCurrentState() || getTeams().getRed().getSizePlayers() < getTeams().getBlue().getSizePlayers()) {
                        getTeams().getBlue().removePlayer(player);
                        getTeams().getRed().addPlayer((OfflinePlayer) player);
                        if (GameState.isState(GameState.GAME)) {
                            Dar.darItemsJoinTeam(player);
                        }
                        player.sendMessage(this.at.getColor(this.at.getMessages().getString("messages.joinred")));
                    } else {
                        player.sendMessage(this.at.getColor(this.at.getMessages().getString("messages.unbalancedTeam")));
                    }
                } else if (!Rule.BALANCED_TEAMS.getCurrentState() || getTeams().getRed().getSizePlayers() <= getTeams().getBlue().getSizePlayers()) {
                    getTeams().getBlue().removePlayer(player);
                    getTeams().getRed().addPlayer((OfflinePlayer) player);
                    if (GameState.isState(GameState.GAME)) {
                        Dar.darItemsJoinTeam(player);
                    }
                    player.sendMessage(this.at.getColor(this.at.getMessages().getString("messages.joinred")));
                } else {
                    player.sendMessage(this.at.getColor(this.at.getMessages().getString("messages.unbalancedTeam")));
                }
            } else {
                player.sendMessage(this.at.getColor(this.at.getMessages().getString("messages.alreadyJoinedRedTeam")));
            }
            e.setCancelled(true);
        } else if (e.getItem() != null && e.getItem().getType().equals(this.teamBlue.getItem().getType()) && e.getItem().getItemMeta().getDisplayName().equals(this.teamBlue.getItem().getItemMeta().getDisplayName()) && (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            if (GameState.isState(GameState.FINISH)) {
                return;
            }
            if (!getTeams().getBlue().containsPlayer(player.getName())) {
                if (getTeams().getRed().containsPlayer(player.getName())) {
                    if (!Rule.BALANCED_TEAMS.getCurrentState() || getTeams().getRed().getSizePlayers() > getTeams().getBlue().getSizePlayers()) {
                        getTeams().getRed().removePlayer(player);
                        getTeams().getBlue().addPlayer((OfflinePlayer) player);
                        if (GameState.isState(GameState.GAME)) {
                            Dar.darItemsJoinTeam(player);
                        }
                        player.sendMessage(this.at.getColor(this.at.getMessages().getString("messages.joinblue")));
                    } else {
                        player.sendMessage(this.at.getColor(this.at.getMessages().getString("messages.unbalancedTeam")));
                    }
                } else if (!Rule.BALANCED_TEAMS.getCurrentState() || getTeams().getRed().getSizePlayers() >= getTeams().getBlue().getSizePlayers()) {
                    getTeams().getRed().removePlayer(player);
                    getTeams().getBlue().addPlayer((OfflinePlayer) player);
                    if (GameState.isState(GameState.GAME)) {
                        Dar.darItemsJoinTeam(player);
                    }
                    player.sendMessage(this.at.getColor(this.at.getMessages().getString("messages.joinblue")));
                } else {
                    player.sendMessage(this.at.getColor(this.at.getMessages().getString("messages.unbalancedTeam")));
                }
            } else {
                player.sendMessage(this.at.getColor(this.at.getMessages().getString("messages.alreadyJoinedBlueTeam")));
            }
            e.setCancelled(true);
        } else if (e.getItem() != null && e.getItem().getType() == this.spectator.getItem().getType() && e.getItem().getItemMeta().getDisplayName() == this.spectator.getItem().getItemMeta().getDisplayName() && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (GameState.isState(GameState.FINISH)) {
                return;
            }
            if (getTeams().getBlue().containsPlayer(player.getName())) {
                getTeams().getBlue().removePlayer(player);
            } else if (getTeams().getRed().containsPlayer(player.getName())) {
                getTeams().getRed().removePlayer(player);
            }
            player.sendMessage(this.at.getColor(this.at.getMessages().getString("messages.joinspectator")).replace("%newLine%", "\n"));
            player.setGameMode(GameMode.SPECTATOR);
            e.setCancelled(true);
        } else if (e.getItem() != null && this.quit != null && e.getItem().getType() == this.quit.getItem().getType() && e.getItem().getItemMeta().getDisplayName() == this.quit.getItem().getItemMeta().getDisplayName() && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            Dar.bungeecordTeleport(player);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent e) {
        if (e.getInventory() == null || e.getClickedInventory() == null || e.getCurrentItem().getType() == null) {
            return;
        }
        if (e.getCurrentItem().getType().equals(this.teamRed.getItem().getType()) && e.getCurrentItem().getItemMeta().getDisplayName().equals(this.teamRed.getItem().getItemMeta().getDisplayName())) {
            e.setCancelled(true);
        } else if (e.getCurrentItem().getType().equals(this.teamBlue.getItem().getType()) && e.getCurrentItem().getItemMeta().getDisplayName().equals(this.teamBlue.getItem().getItemMeta().getDisplayName())) {
            e.setCancelled(true);
        } else if (this.quit != null && e.getCurrentItem().getType().equals(this.quit.getItem().getType()) && e.getCurrentItem().getItemMeta().getDisplayName().equals(this.quit.getItem().getItemMeta().getDisplayName())) {
            e.setCancelled(true);
        } else if (this.at.getGame().getItemBook() != null && e.getCurrentItem().getType().equals(this.at.getGame().getItemBook().getItem().getType()) && e.getCurrentItem().getItemMeta().getDisplayName().equals(this.at.getGame().getItemBook().getItem().getItemMeta().getDisplayName())) {
            e.setCancelled(true);
        } else if (e.getCurrentItem().getType().equals(this.spectator.getItem().getType()) && e.getCurrentItem().getItemMeta().getDisplayName().equals(this.spectator.getItem().getItemMeta().getDisplayName())) {
            e.setCancelled(true);
        }
    }

    public ItemBuilder getItemBlueTeam() {
        return this.teamBlue;
    }

    public ItemBuilder getItemRedTeam() {
        return this.teamRed;
    }
    public ItemBuilder getItemSpectator() {
        return this.spectator;
    }

    public ItemBuilder getItemQuit() {
        return this.quit;
    }

    private TeamGame getTeams() {
        return this.at.getGame().getTeams();
    }

    private AmazingTowers getPlugin() {
        return this.at;
    }
}


