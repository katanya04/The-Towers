package mx.towers.pato14.game.events.player;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.Game;
import mx.towers.pato14.game.items.menus.settings.*;
import mx.towers.pato14.game.kits.Kit;
import mx.towers.pato14.game.items.MenuItem;
import mx.towers.pato14.game.tasks.Start;
import mx.towers.pato14.game.team.Team;
import mx.towers.pato14.game.utils.Dar;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.rewards.SetupVault;
import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class UseLobbyItems implements Listener {
    AmazingTowers plugin = AmazingTowers.getPlugin();
    private boolean canUseLobbyItem(GameInstance gameInstance) {
        return gameInstance != null && gameInstance.getGame() != null;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        GameInstance gameInstance = this.plugin.getGameInstance(player);
        if (!canUseLobbyItem(gameInstance))
            return;
        Game game = gameInstance.getGame();
        if (game.getGameState().equals(GameState.FINISH))
            return;
        if (e.getItem() == null || !(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
            return;
        for (ItemStack item : game.getLobbyItems().getHotbarItems().values()) {
            if (!item.equals(e.getItem()))
                continue;
            if (item instanceof MenuItem && game.getGameState() != GameState.FINISH) {
                if (item.equals(game.getLobbyItems().getSelectKit()) && !gameInstance.getRules().get(Rule.KITS))
                    player.sendMessage(AmazingTowers.getColor(gameInstance.getConfig(ConfigType.MESSAGES).getString("kitsDisabled")));
                else
                    player.openInventory(((MenuItem) item).getMenu());
                e.setCancelled(true);
            } else if (item.equals(game.getLobbyItems().getQuitItem())) {
                Dar.bungeecordTeleport(player);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent e) {
        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().equals(Material.AIR))
            return;
        HumanEntity player = e.getWhoClicked();
        GameInstance gameInstance = this.plugin.getGameInstance(player);
        if (!canUseLobbyItem(gameInstance))
            return;
        Game game = gameInstance.getGame();
        if (!game.getLobbyItems().isALobbyItem(clickedItem, e.getClickedInventory()))
            return;
        e.setCancelled(true);
        Config messages = game.getGameInstance().getConfig(ConfigType.MESSAGES);
        if (game.getLobbyItems().getSelectTeam().getMenu().equals(e.getClickedInventory()) && game.getGameState() != GameState.FINISH) {
            Team currentTeam = game.getTeams().getTeamByPlayer(player.getName()); //Equipo actual
            Team teamToJoin = game.getTeams().getTeamFromLobbyItem(clickedItem);
            if (teamToJoin == null) {
                if (TeamColor.isSpectatorItem(clickedItem, game.getGameInstance())) {
                    player.setGameMode(GameMode.SPECTATOR);
                    player.sendMessage(AmazingTowers.getColor(messages.getString("enterSpectatorMode").replace("%newLine%", "\n")));
                    if (currentTeam != null) {
                        currentTeam.removePlayer(player);
                    }
                    player.closeInventory();
                }
            } else if (!teamToJoin.containsPlayer(player.getName())) { //Si no está ya en ese equipo
                if (!game.getGameInstance().getRules().get(Rule.BALANCED_TEAMS)
                        || teamToJoin.getSizePlayers() == game.getTeams().getLowestTeamPlayers()) {
                    if (currentTeam != null)
                        currentTeam.removePlayer(player);
                    teamToJoin.setPlayerState(player.getName(), PlayerState.ONLINE);
                    teamToJoin.addPlayerNameToTeamItem(player.getName());
                    if (game.getGameState().equals(GameState.GAME))
                        Dar.darItemsJoinTeam((Player) player);
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

        } else if (game.getLobbyItems().getSelectKit().getMenu().equals(e.getClickedInventory())) {
            Kit selectedKit = game.getKits().get(clickedItem);
            if (selectedKit == null)
                return;
            if (!plugin.capitalismExists() || selectedKit.getPrice() == 0 || game.getKits().playerHasKit(player.getName(), selectedKit)) {
                game.getPlayersSelectedKit().put(player, selectedKit);
                player.sendMessage(AmazingTowers.getColor(messages.getString("selectKit")
                        .replace("%kitName%", selectedKit.getName())));
                ((Player) player).playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                player.closeInventory();
            } else if (game.getGameInstance().getVault().getCoins((Player) player) >= selectedKit.getPrice()) {
                player.openInventory(openMenuBuyKit(game, selectedKit, AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("lobbyItems.buyKitMenuName"))));
            } else {
                player.sendMessage(AmazingTowers.getColor(messages.getString("notEnoughMoney")));
                ((Player) player).playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0f, 1.0f);
            }
        } else if (e.getClickedInventory().getName().equals(AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("lobbyItems.buyKitMenuName")))) {
            if (clickedItem.getItemMeta().getDisplayName().equals(AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("lobbyItems.acceptBuy")))) {
                Kit kit = game.getKits().get(e.getClickedInventory().getItem(4));
                if (kit.isPermanent())
                    game.getKits().addKitToPlayer(player.getName(), kit);
                else
                    game.getKits().addTemporalBoughtKitToPlayer(player.getName(), kit);
                player.sendMessage(AmazingTowers.getColor(messages.getString("buyKit").replace("%kitName%", kit.getName())));
                SetupVault.getVaultEconomy().withdrawPlayer((OfflinePlayer) player, kit.getPrice());
                player.openInventory(game.getLobbyItems().getSelectKit().getMenu());
                ((Player) player).playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 2.0f);
            } else if (clickedItem.getItemMeta().getDisplayName().equals(AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("lobbyItems.denyBuy")))) {
                player.openInventory(game.getLobbyItems().getSelectKit().getMenu());
            }
        } else if (game.getLobbyItems().getModifyGameSettings().getMenu().equals(e.getClickedInventory())) {
            for (ItemStack item : game.getLobbyItems().getModifyGameSettings().getContents()) {
                if (!item.equals(clickedItem))
                    continue;
                if (item instanceof SetRules) {
                    player.openInventory(((SetRules) item).getMenu());
                } else if (item instanceof SetWhitelist) {
                    System.out.println("To do");
                } else if (item instanceof KickAll) {
                    for (Player p : game.getPlayers()) {
                        if (!p.isOp())
                            p.kickPlayer(gameInstance.getConfig(ConfigType.CONFIG).getString("settingsBook.kickAll.kickMessage"));
                    }
                } else if (item instanceof StopCount) {
                    gameInstance.getGame().getStart().stopCount();
                } else if (item instanceof ContinueCount || item instanceof StartImmediately) {
                    Start start = gameInstance.getGame().getStart();
                    if (!start.hasStarted()) {
                        gameInstance.getGame().setGameState(GameState.PREGAME);
                        start.setRunFromCommand(true);
                        start.setHasStarted(true);
                        start.gameStart();
                    }
                    start.continueCount();
                    if (item instanceof StartImmediately)
                        start.setSeconds(0);
                }
                ((Player) player).playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);
            }
        } else if (game.getLobbyItems().getModifyGameSettings().getSetRules().getMenu().equals(e.getClickedInventory())) {
            Rule rule = Rule.getRuleFromItem(clickedItem);
            if (rule == null)
                return;
            gameInstance.getRules().put(rule, !gameInstance.getRules().get(rule));
            game.getLobbyItems().getModifyGameSettings().getSetRules().getMenu().setContents(Rule.getIcons(game));
            ((Player) player).playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);
        }
    }

    private Inventory openMenuBuyKit(Game game, Kit kit, String name) {
        Inventory menu = Bukkit.createInventory(null, 27, name);
        menu.setItem(4, kit.getIconInMenu());
        menu.setItem(21, Utils.setName(new ItemStack(Material.WOOL, 1, (short) 5), AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("lobbyItems.acceptBuy"))));
        menu.setItem(23, Utils.setName(new ItemStack(Material.WOOL, 1, (short) 14), AmazingTowers.getColor(game.getGameInstance().getConfig(ConfigType.CONFIG).getString("lobbyItems.denyBuy"))));
        return menu;
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        GameInstance gameInstance = this.plugin.getGameInstance(e.getWhoClicked());
        if (!canUseLobbyItem(gameInstance))
            return;
        if (e.getWhoClicked().getGameMode().equals(GameMode.ADVENTURE)) {
            e.setCancelled(true);
            return;
        }
        Game game = gameInstance.getGame();
        for (ItemStack item : game.getLobbyItems().getHotbarItems().values()) {
            if ((item instanceof MenuItem && ((MenuItem) item).getMenu().equals(e.getInventory()))
                || item.equals(e.getCursor())) {
                e.setCancelled(true);
                return;
            }
        }
    }
}