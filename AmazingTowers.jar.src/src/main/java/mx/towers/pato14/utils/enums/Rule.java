package mx.towers.pato14.utils.enums;

import me.katanya04.anotherguiplugin.actionItems.ListItem;
import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.team.GameTeams;
import mx.towers.pato14.game.team.ITeam;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.items.Items;
import mx.towers.pato14.utils.items.Skulls;
import mx.towers.pato14.utils.nms.ReflectionMethods;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public enum Rule {
    GRIEF(new ItemStack(Material.DIAMOND_PICKAXE)),
    PROTECT_POINT(new ItemStack(Material.BARRIER)),
    EMERALD(new ItemStack(Material.EMERALD)),
    REDSTONE(new ItemStack(Material.REDSTONE)),
    COAL(new ItemStack(Material.COAL)),
    LAPISLAZULI(new ItemStack(Material.INK_SACK, 1, (short) 4)),
    BALANCED_TEAMS(new ItemStack(Material.WOOL, 1, (short) 14)),
    BOW(new ItemStack(Material.BOW)),
    IRON_ARMOR(new ItemStack(Material.IRON_CHESTPLATE)),
    WATER(new ItemStack(Material.WATER_BUCKET)),
    POTS_AND_APPLE(new ItemStack(Material.POTION)),
    ENDER_PEARL(new ItemStack(Material.ENDER_PEARL)),
    TNT(new ItemStack(Material.TNT)),
    ENCHANTS(new ItemStack(Material.ENCHANTED_BOOK)),
    KITS(new ItemStack(Material.IRON_SWORD)),
    BEDWARS_STYLE(new ItemStack(Material.BED)),
    EXPLOSIVE_CHICKEN(new ItemStack(Material.EGG)),
    HUNGER(new ItemStack(Material.PORK)),
    CAPTAINS(Skulls.getSkullFromURL("http://textures.minecraft.net/texture/45587da7fe7336e8ab9f791ea5e2cfc8a827ca959567eb9d53a647babf948d5"));
    private final ItemStack icon;
    Rule(ItemStack icon) {
        this.icon = icon;
    }
    public ItemStack getIcon() {
        return icon;
    }
    public static void createAllActionItems() {
        for (Rule rule : Rule.values())
            createActionItem(rule);
    }
    public static void createActionItem(Rule rule) {
        List<String> list = Collections.unmodifiableList(Arrays.asList("§cFalse", "§aTrue"));
        ListItem<Player> ruleItem = new ListItem<>(
                Utils.setName(rule.getIcon(), "§r" + Utils.macroCaseToItemName(rule.name())), list,
                player -> AmazingTowers.getGameInstance(player).getRules().get(rule) ? 1 : 0, false, "RuleItem." + rule
        );
        ruleItem.setOnInteract(event -> changeRuleValue(AmazingTowers.getGameInstance(event.getPlayer()), rule));
    }
    public static void changeRuleValue(GameInstance gameInstance, Rule rule) {
        setRuleValue(gameInstance, rule, !gameInstance.getRules().get(rule));
    }
    public static void setRuleValue(GameInstance gameInstance, Rule rule, boolean value) {
        gameInstance.getRules().put(rule, value);
        gameInstance.getConfig(ConfigType.GAME_SETTINGS).set("rules." + Utils.macroCaseToCamelCase(rule.name()),
                String.valueOf(value).toLowerCase());
        gameInstance.setFlagChanges(true);
        changeValueBhv(gameInstance, rule, value);
    }
    private static void changeValueBhv(GameInstance gameInstance, Rule rule, boolean value) {
        switch (rule) {
            case KITS:
                if (!value)
                    gameInstance.getGame().getPlayersSelectedKit().clear();
                break;
            case BEDWARS_STYLE:
                if (gameInstance.getGame().getGameState().matchIsBeingPlayed) {
                    GameTeams gameTeams = gameInstance.getGame().getTeams();
                    if (value)
                        gameTeams.getTeams().stream().filter(o -> gameTeams.checkNoLives(o.getTeamColor()))
                            .forEach(o -> gameTeams.loseRespawn(o.getTeamColor()));
                    Set<ITeam> winners = gameTeams.checkWin();
                    if (!winners.isEmpty())
                        gameTeams.win(winners.iterator().next().getTeamColor());
                }
                break;
            case HUNGER:
                if (!value)
                    gameInstance.getWorld().getPlayers().forEach(o -> o.setFoodLevel(20));
                break;
            case CAPTAINS:
                if (!value) {
                    gameInstance.getGame().getCaptainsPhase().reset();
                    if (gameInstance.getGame().getGameState() == GameState.CAPTAINS_CHOOSE) {
                        gameInstance.getGame().getStart().gameStart();
                    }
                } else {
                    gameInstance.getGame().getCaptainsPhase().setPlayerList(false);
                    if (!gameInstance.getGame().getGameState().matchIsBeingPlayed && gameInstance.getGame().getGameState() != GameState.FINISH)
                        gameInstance.getGame().getTeams().getTeams().forEach(ITeam::clear);
                }
            default:
                break;
        }
    }
    public static ItemStack[] getRuleItems() {
        ItemStack[] toret = new ItemStack[Rule.values().length];
        int i = 0;
        for (Rule rule : Rule.values())
            toret[i++] = Items.getByName("RuleItem." + rule);
        return toret;
    }
}