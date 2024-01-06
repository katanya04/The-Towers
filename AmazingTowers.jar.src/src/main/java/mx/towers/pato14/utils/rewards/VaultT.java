package mx.towers.pato14.utils.rewards;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.entity.Player;

public class VaultT {
    private final String gameInstanceName;
    public VaultT(GameInstance gameInstance) {
        this.gameInstanceName = gameInstance.getInternalName();
    }
    public void giveReward(Player player, RewardsEnum reward) {
        GameInstance gameInstance = AmazingTowers.getGameInstance(this.gameInstanceName);
        if (gameInstance.getConfig(ConfigType.CONFIG).getBoolean("options.rewards.vault") &&
                SetupVault.getVaultEconomy() != null) {
            int points = gameInstance.getConfig(ConfigType.CONFIG).getInt("options.rewards.reward." + reward.getName());
            if (player.hasPermission("towers.coinsx6")) {
                points *= 6;
            } else if (player.hasPermission("towers.coinsx5")) {
                points *= 5;
            } else if (player.hasPermission("towers.coinsx4")) {
                points *= 4;
            } else if (player.hasPermission("towers.coinsx3")) {
                points *= 3;
            } else if (player.hasPermission("towers.coinsx2")) {
                points *= 2;
            }
            SetupVault.getVaultEconomy().depositPlayer(player, points);
            player.sendMessage(Utils.getColor(gameInstance.getConfig(ConfigType.CONFIG).getString("options.rewards.messages." + reward.getName()).replaceAll("%coins%", String.valueOf(points))));
        }
    }
}