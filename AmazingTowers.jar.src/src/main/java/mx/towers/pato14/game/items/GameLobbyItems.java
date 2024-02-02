package mx.towers.pato14.game.items;

import mx.towers.pato14.GameInstance;
import mx.towers.pato14.game.items.actions.QuitItem;
import mx.towers.pato14.game.items.menus.*;
import mx.towers.pato14.utils.Config;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class GameLobbyItems extends HotbarItems implements Listener {
    private final SelectTeam selectTeam;
    private final SelectKit selectKit;
    private final ModifyGameSettings modifyGameSettings;

    public GameLobbyItems(GameInstance gameInstance) {
        super();
        Config config = gameInstance.getConfig(ConfigType.CONFIG);
        this.hotbarItems.put(config.getInt("lobbyItems.hotbarItems.selectTeam.position"), this.selectTeam = new SelectTeam(gameInstance));
        this.hotbarItems.put(config.getInt("lobbyItems.hotbarItems.selectKit.position"), this.selectKit = new SelectKit(gameInstance));
        this.hotbarItems.put(config.getInt("lobbyItems.hotbarItems.quit.position"), new QuitItem(gameInstance));
        this.hotbarItems.put(config.getInt("lobbyItems.hotbarItems.modifyGameSettings.position"), this.modifyGameSettings = new ModifyGameSettings(gameInstance));
    }

    @Override
    public void giveHotbarItems(HumanEntity player) {
        for (Map.Entry<Integer, ItemStack> item : this.hotbarItems.entrySet()) {
            if (item.getValue().equals(this.modifyGameSettings) && !player.hasPermission("towers.organizer"))
                continue;
            player.getInventory().setItem(item.getKey(), item.getValue());
        }
    }

    public SelectTeam getSelectTeam() {
        return selectTeam;
    }

    public SelectKit getSelectKit() {
        return selectKit;
    }

    public ModifyGameSettings getModifyGameSettings() {
        return modifyGameSettings;
    }

    public void reset(GameInstance gameInstance) {
        Utils.removeGlint(this.getModifyGameSettings().getSaveSettings());
        this.getModifyGameSettings().getSetRules().updateMenu(gameInstance);
        this.getModifyGameSettings().getSelectDatabase().reset(gameInstance);
        this.getModifyGameSettings().updateMenu();
    }
}