package mx.towers.pato14.game.team;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.utils.Dar;
import mx.towers.pato14.utils.ItemBuilder;
import mx.towers.pato14.utils.enums.ConfigType;
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
    private final ItemBuilder teamBlue;
    private final ItemBuilder teamRed;
    private final ItemBuilder spectator;
    private final Game game;

    public Item(Game game) {
        this.game = game;
        this.at = game.getGameInstance().getPlugin();
        this.teamRed = (new ItemBuilder(Material.WOOL, (short) 14)).setName(AmazingTowers.getColor(getPlugin().getConfig().getString("Items.itemRed.name")));
        this.teamBlue = (new ItemBuilder(Material.WOOL, (short) 11)).setName(AmazingTowers.getColor(getPlugin().getConfig().getString("Items.itemBlue.name")));
        this.spectator = (new ItemBuilder(Material.WOOL, (short) 8)).setName(AmazingTowers.getColor(getPlugin().getConfig().getString("Items.itemSpectator.name")));
        if (this.at.getConfig().getBoolean("Options.bungeecord-support.enabled"))
            this.quit = (new ItemBuilder(Material.BED)).setName(AmazingTowers.getColor(getPlugin().getConfig().getString("Items.itemQuit.name")));
    }

    private ItemBuilder quit;
    private final AmazingTowers at;

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
            if (!getTeams().getTeam(mx.towers.pato14.utils.enums.Team.RED).containsPlayer(player.getName())) {
                if (getTeams().getTeam(mx.towers.pato14.utils.enums.Team.BLUE).containsPlayer(player.getName())) {
                    if (!Rule.BALANCED_TEAMS.getCurrentState() || getTeams().getTeam(mx.towers.pato14.utils.enums.Team.RED).getSizePlayers() < getTeams().getTeam(mx.towers.pato14.utils.enums.Team.BLUE).getSizePlayers()) {
                        getTeams().getTeam(mx.towers.pato14.utils.enums.Team.BLUE).removePlayer(player);
                        getTeams().getTeam(mx.towers.pato14.utils.enums.Team.RED).addPlayer((OfflinePlayer) player);
                        if (GameState.isState(GameState.GAME)) {
                            Dar.darItemsJoinTeam(player);
                        }
                        player.sendMessage(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.joinred")));
                    } else {
                        player.sendMessage(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.unbalancedTeam")));
                    }
                } else if (!Rule.BALANCED_TEAMS.getCurrentState() || getTeams().getTeam(mx.towers.pato14.utils.enums.Team.RED).getSizePlayers() <= getTeams().getTeam(mx.towers.pato14.utils.enums.Team.BLUE).getSizePlayers()) {
                    getTeams().getTeam(mx.towers.pato14.utils.enums.Team.BLUE).removePlayer(player);
                    getTeams().getTeam(mx.towers.pato14.utils.enums.Team.RED).addPlayer((OfflinePlayer) player);
                    if (GameState.isState(GameState.GAME)) {
                        Dar.darItemsJoinTeam(player);
                    }
                    player.sendMessage(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.joinred")));
                } else {
                    player.sendMessage(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.unbalancedTeam")));
                }
            } else {
                player.sendMessage(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.alreadyJoinedRedTeam")));
            }
            e.setCancelled(true);
        } else if (e.getItem() != null && e.getItem().getType().equals(this.teamBlue.getItem().getType()) && e.getItem().getItemMeta().getDisplayName().equals(this.teamBlue.getItem().getItemMeta().getDisplayName()) && (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            if (GameState.isState(GameState.FINISH)) {
                return;
            }
            if (!getTeams().getTeam(mx.towers.pato14.utils.enums.Team.BLUE).containsPlayer(player.getName())) {
                if (getTeams().getTeam(mx.towers.pato14.utils.enums.Team.RED).containsPlayer(player.getName())) {
                    if (!Rule.BALANCED_TEAMS.getCurrentState() || getTeams().getTeam(mx.towers.pato14.utils.enums.Team.RED).getSizePlayers() > getTeams().getTeam(mx.towers.pato14.utils.enums.Team.BLUE).getSizePlayers()) {
                        getTeams().getTeam(mx.towers.pato14.utils.enums.Team.RED).removePlayer(player);
                        getTeams().getTeam(mx.towers.pato14.utils.enums.Team.BLUE).addPlayer((OfflinePlayer) player);
                        if (GameState.isState(GameState.GAME)) {
                            Dar.darItemsJoinTeam(player);
                        }
                        player.sendMessage(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.joinblue")));
                    } else {
                        player.sendMessage(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.unbalancedTeam")));
                    }
                } else if (!Rule.BALANCED_TEAMS.getCurrentState() || getTeams().getTeam(mx.towers.pato14.utils.enums.Team.RED).getSizePlayers() >= getTeams().getTeam(mx.towers.pato14.utils.enums.Team.BLUE).getSizePlayers()) {
                    getTeams().getTeam(mx.towers.pato14.utils.enums.Team.RED).removePlayer(player);
                    getTeams().getTeam(mx.towers.pato14.utils.enums.Team.BLUE).addPlayer((OfflinePlayer) player);
                    if (GameState.isState(GameState.GAME)) {
                        Dar.darItemsJoinTeam(player);
                    }
                    player.sendMessage(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.joinblue")));
                } else {
                    player.sendMessage(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.unbalancedTeam")));
                }
            } else {
                player.sendMessage(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.alreadyJoinedBlueTeam")));
            }
            e.setCancelled(true);
        } else if (e.getItem() != null && e.getItem().getType().equals(this.spectator.getItem().getType()) && e.getItem().getItemMeta().getDisplayName().equals(this.spectator.getItem().getItemMeta().getDisplayName()) && (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            if (GameState.isState(GameState.FINISH)) {
                return;
            }
            if (getTeams().getTeam(mx.towers.pato14.utils.enums.Team.BLUE).containsPlayer(player.getName())) {
                getTeams().getTeam(mx.towers.pato14.utils.enums.Team.BLUE).removePlayer(player);
            } else if (getTeams().getTeam(mx.towers.pato14.utils.enums.Team.RED).containsPlayer(player.getName())) {
                getTeams().getTeam(mx.towers.pato14.utils.enums.Team.RED).removePlayer(player);
            }
            player.sendMessage(AmazingTowers.getColor(this.game.getGameInstance().getConfig(ConfigType.MESSAGES).getString("messages.joinspectator")).replace("%newLine%", "\n"));
            player.setGameMode(GameMode.SPECTATOR);
            e.setCancelled(true);
        } else if (e.getItem() != null && this.quit != null && e.getItem().getType().equals(this.quit.getItem().getType()) && e.getItem().getItemMeta().getDisplayName().equals(this.quit.getItem().getItemMeta().getDisplayName()) && (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
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
        } else if (this.game.getItemBook() != null && e.getCurrentItem().getType().equals(this.game.getItemBook().getItem().getType()) && e.getCurrentItem().getItemMeta().getDisplayName().equals(this.game.getItemBook().getItem().getItemMeta().getDisplayName())) {
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
        return this.game.getTeams();
    }

    private AmazingTowers getPlugin() {
        return this.at;
    }
}


