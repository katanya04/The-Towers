package mx.towers.pato14.utils.items;

import me.katanya04.anotherguiplugin.actionItems.ActionItem;
import mx.towers.pato14.GameInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public enum ItemsEnum {
    GAME_SELECT("GameSelect", false),
    QUIT_LOBBY("QuitLobby", false),
    TEAM_SELECT("TeamSelect", true),
    KIT_SELECT("KitSelect", true),
    QUIT_GAME("QuitGame", true),
    GAME_SETTINGS("GameSettings", true),
    SAVE_SETTINGS("SaveSettings", true),
    SELECT_DATABASE("SelectDatabase", true),
    SET_RULES("SetRules", true),
    WHITELIST("Whitelist", true),
    KICK_PLAYERS("KickPlayers", true),
    BLACKLIST("Blacklist", true),
    STOP_COUNT("StopCount", true),
    CONTINUE_COUNT("ContinueCount", true),
    START_IMMEDIATELY("StartImmediately", true),
    SET_TIMER("SetTimer", true),
    MODIFY_KITS("ModifyKits", true),
    SELECT_DB("SelectDatabase", true),
    END_MATCH("EndMatch", true),
    KIT("Kit", true),
    ACCEPT_BUY("AcceptBuy", true),
    DENY_BUY("DenyBuy", true),
    SELECT_PLAYER("SelectPlayer", false),
    SELECT_PLAYERS("SelectPlayers", true),
    POSSIBLE_CAPTAINS("PossibleCaptains", true);
    public final String name;
    public final boolean needsPlayer;
    ItemsEnum(String name, boolean needsPlayer) {
        this.name = name;
        this.needsPlayer = needsPlayer;
    }
}
