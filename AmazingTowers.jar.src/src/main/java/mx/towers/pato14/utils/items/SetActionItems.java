package mx.towers.pato14.utils.items;

import me.katanya04.anotherguiplugin.actionItems.ActionItem;
import me.katanya04.anotherguiplugin.actionItems.ListItem;
import me.katanya04.anotherguiplugin.actionItems.MenuItem;
import me.katanya04.anotherguiplugin.menu.BookMenu;
import me.katanya04.anotherguiplugin.menu.ChestMenu;
import me.katanya04.anotherguiplugin.menu.InventoryMenu;
import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.game.kits.Kit;
import mx.towers.pato14.game.kits.Kits;
import mx.towers.pato14.game.team.TeamColor;
import mx.towers.pato14.utils.files.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.*;
import mx.towers.pato14.utils.rewards.SetupVault;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.function.Function;

public class SetActionItems {
    private static boolean registered = false;
    public static void registerItems() {
        if (registered)
            return;

        //Lobby items
        Function<TowersWorldInstance, Config> config = game -> game.getConfig(ConfigType.CONFIG);
        MenuItem<ChestMenu, Player> selectGameMenu = new MenuItem<>(pl ->
                Utils.setName(new ItemStack(Material.COMPASS), Utils.getColor(config.apply(AmazingTowers.getInstance(pl)).getString("lobbyItems.selectGame.name"))),
                new ChestMenu("selectGameMenu", new ItemStack[AmazingTowers.getGameInstances().length]),
                ItemsEnum.GAME_SELECT.name
        );
        generateJoinMenuContents();
        selectGameMenu.getMenu().setOnOpenBehaviour(event -> {
            sortContents(event);
            event.getInventory().setContents(InventoryMenu.parseActionItemsByPlayer(event.getInventory().getContents(), (Player) event.getPlayer()));
        });

        if (AmazingTowers.getGlobalConfig().getBoolean("options.bungeecord.enabled")) {
            new ActionItem<Player>(pl -> Utils.setName(new ItemStack(Material.BED),
                    Utils.getColor(config.apply(AmazingTowers.getInstance(pl)).getString("lobbyItems.quit.name"))), event -> Utils.bungeecordTeleport(event.getPlayer()),
                    ItemsEnum.QUIT_LOBBY.name
            );
        }

        //Game items
        TeamColor.createAllActionItems();
        ChestMenu selectTeam = new ChestMenu("selectTeam", TeamColor.getTeamItems());
        new MenuItem<ChestMenu, Player>(p -> Utils.setName(new ItemStack(Material.WOOL, 1, (short) 14),
                Utils.getColor(config.apply(AmazingTowers.getGameInstance(p)).getString("lobbyItems.hotbarItems.selectTeam.name"))),
                selectTeam, ItemsEnum.TEAM_SELECT.name);


        new ActionItem<Player>(pl ->
                Utils.setName(new ItemStack(Material.WOOL, 1, (short) 5), Utils.getColor(AmazingTowers.getGameInstance(pl).
                        getConfig(ConfigType.CONFIG).getString("lobbyItems.acceptBuy"))),
                event -> {
                    Player player = event.getPlayer();
                    GameInstance game = AmazingTowers.getGameInstance(player);
                    Kit kitToBuy = Kits.getByIcon(event.getInv().getItem(4));
                    if (kitToBuy == null)
                        return;
                    if (kitToBuy.isPermanent())
                        game.getGame().getKits().addKitToPlayer(player.getName(), kitToBuy);
                    else
                        game.getGame().getKits().addTemporalBoughtKitToPlayer(player.getName(), kitToBuy);
                    player.sendMessage(Utils.getColor(game.getConfig(ConfigType.MESSAGES).getString("buyKit").replace("%kitName%", kitToBuy.getName())));
                    SetupVault.getVaultEconomy().withdrawPlayer(player, kitToBuy.getPrice());
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 2.0f);
                }, ItemsEnum.ACCEPT_BUY.name);
        new ActionItem<Player>(pl ->
                Utils.setName(new ItemStack(Material.WOOL, 1, (short) 14), Utils.getColor(AmazingTowers.getGameInstance(pl)
                        .getConfig(ConfigType.CONFIG).getString("lobbyItems.denyBuy"))),
                event -> event.getPlayer().closeInventory(), ItemsEnum.DENY_BUY.name);

        ChestMenu selectKit = new ChestMenu("selectKit", new ItemStack[9]);
        selectKit.setOnOpenBehaviour(event -> {
            GameInstance game = AmazingTowers.getGameInstance(event.getPlayer());
            ItemStack[] newContents = Arrays.stream(game.getGame().getKits().getKitIcons())
                    .map(o -> ActionItem.getByName(ItemsEnum.KIT.name).convertToActionItem(o)).toArray(ItemStack[]::new);
            event.getInventory().setContents(newContents);
        });
        new MenuItem<ChestMenu, Player>(p -> Utils.setName(new ItemStack(Material.IRON_SWORD),
                Utils.getColor(config.apply(AmazingTowers.getGameInstance(p)).getString("lobbyItems.hotbarItems.selectKit.name"))),
                selectKit, ItemsEnum.KIT_SELECT.name);

        new ActionItem<Player>(
                p -> Utils.setName(new ItemStack(Material.BED), Utils.getColor(config.apply(AmazingTowers.getGameInstance(p)).getString("lobbyItems.hotbarItems.quit.name"))),
                event -> Utils.tpToWorld(AmazingTowers.getLobby().getWorld(), event.getPlayer()), ItemsEnum.QUIT_GAME.name);

        generateSettingsMenuContents();
        ChestMenu gameSettingsMenu = new ChestMenu("gameSettingsMenu", gameSettingsContents());
        new MenuItem<ChestMenu, Player>(p -> Utils.setName(new ItemStack(Material.PAPER),
                Utils.getColor(config.apply(AmazingTowers.getInstance(p)).getString("lobbyItems.hotbarItems.modifyGameSettings.name"))),
                gameSettingsMenu, ItemsEnum.GAME_SETTINGS.name);

        ActionItem<String> selectPlayer = new ActionItem<>(Skulls::getPlayerHead, event -> {
            GameInstance game = AmazingTowers.getGameInstance(event.getPlayer());
            if (game == null || game.getGame() == null)
                return;
            game.getGame().getCaptainsPhase().choosePlayer(event.getPlayer(), Skulls.getPlayerByHead(event.getItem()));
        }, ItemsEnum.SELECT_PLAYER.name);

        ChestMenu selectPlayers = new ChestMenu("selectPlayers", pl -> {
            GameInstance game = AmazingTowers.getGameInstance(pl);
            if (game == null || game.getGame() == null || game.getGame().getGameState() != GameState.CAPTAINS_CHOOSE)
                return new ItemStack[0];
            return game.getGame().getCaptainsPhase().getPlayersToChoose().stream().map(selectPlayer::toItemStack).toArray(ItemStack[]::new);
        });
        selectPlayers.setOnOpenBehaviour(event -> {
            GameInstance game = AmazingTowers.getGameInstance(event.getPlayer());
            if (game == null || game.getGame() == null)
                return;
            game.getGame().getCaptainsPhase().setHeads(event.getInventory());
        });
        selectPlayers.setOnChangePage(event -> {
            GameInstance game = AmazingTowers.getGameInstance(event.getPlayer());
            if (game == null || game.getGame() == null)
                return;
            game.getGame().getCaptainsPhase().setHeads(event.getInv());
        });
        selectPlayers.setAfterUpdateContents(event -> {
            GameInstance game = AmazingTowers.getGameInstance(event.getInventory().getViewers().get(0));
            if (game == null || game.getGame() == null)
                return;
            game.getGame().getCaptainsPhase().setHeads(event.getInventory());
        });
        selectPlayers.parseActionItems = false;
        new MenuItem<ChestMenu, Player>(p -> Utils.setName(new ItemStack(Material.WOOL, 1,
                AmazingTowers.getGameInstance(p).getGame().getTeams().getTeamColorByPlayer(p.getName()).getWoolColor()),
                Utils.getColor(AmazingTowers.getGameInstance(p).getConfig(ConfigType.CONFIG).getString("lobbyItems.hotbarItems.selectPlayers.name"))),
                selectPlayers, ItemsEnum.SELECT_PLAYERS.name);

        registered = true;

    }

    private static void generateJoinMenuContents() {
        ActionItem<?>[] joinItems = new ActionItem[AmazingTowers.getGameInstances().length];
        for (int i = 0; i < joinItems.length; i++) {
            GameInstance instance = AmazingTowers.getGameInstances()[i];
            joinItems[i] = new ActionItem<>(ignored -> Utils.setLore(Utils.setName(new ItemStack(Material.EMPTY_MAP),
                            Utils.getColor(instance.getName())), "§f" + instance.getNumPlayersString()),
                    null, "JoinMatch." + instance.getInternalName());
            joinItems[i].setOnInteract(event -> {
                if (instance.canJoin(event.getPlayer()))
                    Utils.tpToWorld(Bukkit.getWorld(instance.getInternalName()), event.getPlayer());
                else
                    Utils.sendMessage(Utils.getColor(instance.getConfig(ConfigType.MESSAGES).getString("canNotJoinGame")), MessageType.ERROR, event.getPlayer());
            });
        }
    }

    private static void sortContents(InventoryOpenEvent event) {
        GameInstance[] instances = AmazingTowers.getGameInstances();
        for (int i = 0; i < instances.length; i++) {
            GameInstance current = instances[i];
            event.getInventory().setItem(i, Items.getByName("JoinMatch." + current.getInternalName()));
        }
    }

    private static void generateSettingsMenuContents() {
        Function<GameInstance, Config> config =  game -> game.getConfig(ConfigType.CONFIG);
        Function<GameInstance, Config> gameSettings =  game -> game.getConfig(ConfigType.GAME_SETTINGS);
        Function<GameInstance, Config> kits =  game -> game.getConfig(ConfigType.KITS);

        Rule.createAllActionItems();
        ChestMenu rulesMenu = new ChestMenu("SetRules", Rule.getRuleItems());
        /*10*/ new MenuItem<ChestMenu, Player>(p -> Utils.setName(new ItemStack(Material.PAPER),
                Utils.getColor(config.apply(AmazingTowers.getGameInstance(p)).getString("settingsBook.setRules"))),
                rulesMenu, ItemsEnum.SET_RULES.name);

        BookMenu<GameInstance> whitelistBook = new BookMenu<>(game -> {
            BookMenu.ConfigField root = BookMenu.ConfigField.fromConfig(gameSettings.apply(game).getConfigurationSection("whitelist"));
            BookMenu.Field players = root.getFirstChildGivenData("players");
            if (players == null) {
                gameSettings.apply(game).set("whitelist.players", "");
                root = BookMenu.ConfigField.fromConfig(gameSettings.apply(game).getConfigurationSection("whitelist"));
                players = root.getFirstChildGivenData("players");
            }
            players.applyToChildren(field -> field.setRemovableFromBook(true), false);
            players.setCanAddMoreFields(true);
            root.getFirstChildGivenData("activated").getChild(0).setValidCheckFunction(Utils::isBoolean);
            root.setOnModifyChildrenValue(field -> {
                game.updateWhiteList();
                game.setFlagChanges(true);
            });
            return root;
        });
        whitelistBook.setToCacheKey(AmazingTowers::getGameInstance);
        /*3*/ new MenuItem<BookMenu<GameInstance>, Player>(p -> Utils.setName(new ItemStack(Material.BOOK_AND_QUILL),
                Utils.getColor(config.apply(AmazingTowers.getGameInstance(p)).getString("settingsBook.setWhitelist"))),
                whitelistBook, ItemsEnum.WHITELIST.name);

        /*12*/ new ActionItem<Player>(player -> Utils.setName(new ItemStack(Material.REDSTONE_TORCH_ON), Utils.getColor(
                config.apply(AmazingTowers.getGameInstance(player)).getString("settingsBook.kickAll.name"))), event -> AmazingTowers.getGameInstance(event.getPlayer()).getGame().getPlayers().stream().filter(
                o -> !o.equals(event.getPlayer()) && !o.isOp()).forEach(o -> o.kickPlayer(config.apply(AmazingTowers.getGameInstance(event.getPlayer())).getString("settingsBook.kickAll.kickMessage"))),
                ItemsEnum.KICK_PLAYERS.name
        );

        BookMenu<GameInstance> blacklistBook = new BookMenu<>(game -> {
            BookMenu.ConfigField root = BookMenu.ConfigField.fromConfig(gameSettings.apply(game).getConfigurationSection("blacklist"));
            BookMenu.Field players = root.getFirstChildGivenData("players");
            if (players == null) {
                gameSettings.apply(game).set("blacklist.players", "");
                root = BookMenu.ConfigField.fromConfig(gameSettings.apply(game).getConfigurationSection("blacklist"));
                players = root.getFirstChildGivenData("players");
            }
            players.applyToChildren(field -> field.setRemovableFromBook(true), false);
            root.getFirstChildGivenData("activated").getChild(0).setValidCheckFunction(Utils::isBoolean);
            players.setCanAddMoreFields(true);
            root.setOnModifyChildrenValue(field -> {
                game.updateWhiteList();
                game.setFlagChanges(true);
            });
            return root;
        });
        blacklistBook.setToCacheKey(AmazingTowers::getGameInstance);
        /*21*/ new MenuItem<BookMenu<GameInstance>, Player>(p -> Utils.setName(new ItemStack(Material.BOOK_AND_QUILL),
                Utils.getColor(config.apply(AmazingTowers.getGameInstance(p)).getString("settingsBook.setBlacklist"))),
                blacklistBook, ItemsEnum.BLACKLIST.name);

        /*7*/ new ActionItem<Player>(p -> Utils.setName(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14),
                        Utils.getColor(config.apply(AmazingTowers.getGameInstance(p)).getString("settingsBook.stopCount"))),
                e -> AmazingTowers.getGameInstance(e.getPlayer()).getGame().getStart().stopCount(), ItemsEnum.STOP_COUNT.name
        );

        /*16*/ new ActionItem<Player>(p -> Utils.setName(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 4),
                        Utils.getColor(config.apply(AmazingTowers.getGameInstance(p)).getString("settingsBook.continueCount"))),
                e -> AmazingTowers.getGameInstance(e.getPlayer()).getGame().getStart().continueFromCommand(), ItemsEnum.CONTINUE_COUNT.name
        );

        /*25*/ new ActionItem<Player>(p -> Utils.setName(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5),
                        Utils.getColor(config.apply(AmazingTowers.getGameInstance(p)).getString("settingsBook.startImmediately"))),
                e -> AmazingTowers.getGameInstance(e.getPlayer()).getGame().getStart().startFromCommand(), ItemsEnum.START_IMMEDIATELY.name
        );

        BookMenu<GameInstance> timerBook = new BookMenu<>(game -> {
            BookMenu.ConfigField timerRoot = BookMenu.ConfigField.fromConfig(gameSettings.apply(game).getConfigurationSection("timer"));
            timerRoot.getFirstChildGivenData("activated").getChild(0).setValidCheckFunction(s -> "false".equalsIgnoreCase(s) || "true".equalsIgnoreCase(s));
            timerRoot.getFirstChildGivenData("time").getChild(0).setValidCheckFunction(s -> Utils.isStringTime(s.split(":")));
            timerRoot.setOnModifyChildrenValue(f -> {
                game.getGame().getTimer().update(game);
                game.setFlagChanges(true);
            });
            return timerRoot;
        });
        timerBook.setToCacheKey(AmazingTowers::getGameInstance);
        /*14*/ new MenuItem<BookMenu<GameInstance>, Player>(p -> Utils.setName(new ItemStack(Material.WATCH),
                Utils.getColor(config.apply(AmazingTowers.getGameInstance(p)).getString("settingsBook.setTimer"))),
                timerBook, ItemsEnum.SET_TIMER.name);

        BookMenu<GameInstance> kitsBook = new BookMenu<>(game -> {
            BookMenu.Field kitsRoot = new BookMenu.Field("Kits")
                    .addChild(BookMenu.ConfigField.fromConfig(AmazingTowers.getKitsDefine().getConfigurationSection("Kits")));
            BookMenu.Field kitsField = kitsRoot.getFirstChildGivenData("Kits");
            kitsField.setCanAddMoreFields(true);
            kitsField.getChildren().forEach(o -> o.setIsModifiable(BookMenu.Field.ModifiableOption.YES));
            kitsField.setOnModifyChildrenValue(field -> {
                Kits.updateGlobalKits();
                game.setFlagChanges(true);
            });
            kitsField.setChildTemplate(
                    new BookMenu.Field("", true, false)
                            .addChild(new BookMenu.Field("armor").addChild(new BookMenu.InventoryField(4, "armor")))
                            .addChild(new BookMenu.Field("hotbar").addChild(new BookMenu.InventoryField(9, "hotbar")))
                            .addChild(new BookMenu.Field("price").addChild(new BookMenu.Field("0", null, Utils::isInteger)))
                            .addChild(new BookMenu.Field("permanent").addChild(new BookMenu.Field("true", null, Utils::isBoolean)))
                            .addChild(new BookMenu.Field("iconInMenu").addChild(new BookMenu.InventoryField(1, "iconInMenu")))
            );
            kitsRoot.getChildrenGivenData("price", true).forEach(o -> o.setValidCheckFunction(Utils::isInteger));
            kitsRoot.getChildrenGivenData("permanent", true).forEach(o -> o.setValidCheckFunction(Utils::isBoolean));
            if (kits.apply(game).get("KitsInThisInstance") == null) {
                kits.apply(game).set("KitsInThisInstance", "");
            }
            kitsRoot.addChild(BookMenu.ConfigField.fromConfig(kits.apply(game)));
            BookMenu.Field kitsInThisInstance = kitsRoot.getChildByPredicate(f -> f.getData().equals("KitsInThisInstance"), true);
            kitsInThisInstance.setCanAddMoreFields(true);
            kitsInThisInstance.setChildTemplate(new BookMenu.Field("Default", null, str -> Kits.getKitsNames().contains(str)));
            kitsInThisInstance.getChildren().forEach(o -> o.setRemovableFromBook(true));
            kitsInThisInstance.setOnModifyChildrenValue(o -> {
                game.getGame().getKits().updateKits();
                game.setFlagChanges(true);
            });
            kitsRoot.getChildrenByPredicate(o -> o instanceof BookMenu.InventoryField &&
                    ((BookMenu.InventoryField) o).getInvMenu() instanceof ChestMenu, true)
                    .forEach(o -> ((ChestMenu) ((BookMenu.InventoryField) o).getInvMenu()).setFillWithBarriers(true));
            kitsRoot.setShouldUseCache(false);
            return kitsRoot;
        });
        kitsBook.setToCacheKey(AmazingTowers::getGameInstance);
        /*15*/ new MenuItem<BookMenu<GameInstance>, Player>(p -> Utils.setName(new ItemStack(Material.IRON_SWORD),
                Utils.getColor(config.apply(AmazingTowers.getGameInstance(p)).getString("settingsBook.modifyKits"))),
                kitsBook, ItemsEnum.MODIFY_KITS.name);

        /*26*/ new ActionItem<Player>(p -> {
                ItemStack item = Utils.setName(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 8),
                    Utils.getColor(config.apply(AmazingTowers.getGameInstance(p)).getString("settingsBook.saveSettings.name")));
                if (AmazingTowers.getGameInstance(p).needsToBeSaved())
                    Utils.addGlint(item);
                return item;
                },
                event -> {
                    GameInstance game = AmazingTowers.getGameInstance(event.getPlayer());
                    if (!game.needsToBeSaved()) {
                        Utils.sendMessage(config.apply(game).getString("settingsBook.saveSettings.noChangesMessage"), MessageType.ERROR, event.getPlayer());
                        return;
                    }
                    game.saveConfig();
                    AmazingTowers.getKitsDefine().saveConfig();
                    Utils.sendMessage(config.apply(game).getString("settingsBook.saveSettings.saveMessage"), MessageType.INFO, event.getPlayer());
                    game.setFlagChanges(false);
                    event.getActionItem().getParent().updateContents();
                }, ItemsEnum.SAVE_SETTINGS.name
        );

        /*17*/ ListItem<Player> selectDatabase = new ListItem<>(p ->
                Utils.setName(new ItemStack(Material.BEACON), Utils.getColor(config.apply(AmazingTowers.getGameInstance(p)).getString("settingsBook.selectStatsDB"))),
                AmazingTowers.connexion.getTables(), p -> AmazingTowers.connexion.getTables().indexOf(AmazingTowers.getGameInstance(p).getTableName()), true,
                ItemsEnum.SELECT_DB.name);
        selectDatabase.setOnInteract(e -> {
            GameInstance game = AmazingTowers.getGameInstance(e.getPlayer());
            game.setTableName(selectDatabase.getFromItem(e.getItem()));
            game.setFlagChanges(true);
            selectDatabase.getParent().updateContents();
        });

        /*8*/ new ActionItem<Player>(p ->
                Utils.setName(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15),
                        Utils.getColor(config.apply(AmazingTowers.getGameInstance(p)).getString("settingsBook.endMatch"))),
                event -> {
                    GameInstance game = AmazingTowers.getGameInstance(event.getPlayer());
                    game.getGame().endMatch();
                    if (game.getGame().getGameState() == GameState.EXTRA_TIME)
                        Utils.sendMessage("Redo this action to finish the match definitively", MessageType.INFO, event.getPlayer());
                    else if (game.getGame().getGameState() != GameState.FINISH)
                        Utils.sendMessage("This action can only be done while a match is taking place", MessageType.ERROR, event.getPlayer());
                },
                ItemsEnum.END_MATCH.name
        );
    }
    private static ItemStack[] gameSettingsContents() {
        ItemStack[] toret = new ItemStack[9 * 3];
        toret[10] = Items.get(ItemsEnum.SET_RULES);
        toret[3] = Items.get(ItemsEnum.WHITELIST);
        toret[12] = Items.get(ItemsEnum.KICK_PLAYERS);
        toret[21] = Items.get(ItemsEnum.BLACKLIST);
        toret[7] = Items.get(ItemsEnum.STOP_COUNT);
        toret[16] = Items.get(ItemsEnum.CONTINUE_COUNT);
        toret[25] = Items.get(ItemsEnum.START_IMMEDIATELY);
        toret[14] = Items.get(ItemsEnum.SET_TIMER);
        toret[15] = Items.get(ItemsEnum.MODIFY_KITS);
        toret[26] = Items.get(ItemsEnum.SAVE_SETTINGS);
        toret[17] = Items.get(ItemsEnum.SELECT_DB);
        toret[8] = Items.get(ItemsEnum.END_MATCH);
        return toret;
    }
    public static void setHotbarItemsInInstances() {
        AmazingTowers.getLobby().setHotbarItems();
        for (GameInstance game : AmazingTowers.getGameInstances())
            game.setHotbarItems();
    }
}