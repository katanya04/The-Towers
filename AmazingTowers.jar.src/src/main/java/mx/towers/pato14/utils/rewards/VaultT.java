package mx.towers.pato14.utils.rewards;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.GameInstance;
import mx.towers.pato14.utils.enums.ConfigType;
import org.bukkit.entity.Player;

public class VaultT {
    private final GameInstance gameInstance;

    public VaultT(GameInstance gameInstance) {
        this.gameInstance = gameInstance;
    }

    public void setReward(Player player, RewardsEnum reward) {
        if (this.gameInstance.getConfig(ConfigType.CONFIG).getBoolean("options.rewards.vault") &&
                SetupVault.getVaultEconomy() != null) {
            int points = this.gameInstance.getConfig(ConfigType.CONFIG).getInt("options.rewards.reward." + reward.getName());
            if (player.hasPermission("towers.vip.coinsx6")) {
                points *= 6;
            } else if (player.hasPermission("towers.vip.coinsx5")) {
                points *= 5;
            } else if (player.hasPermission("towers.vip.coinsx4")) {
                points *= 4;
            } else if (player.hasPermission("towers.vip.coinsx3")) {
                points *= 3;
            } else if (player.hasPermission("towers.vip.coinsx2")) {
                points *= 2;
            }
            SetupVault.getVaultEconomy().depositPlayer(player, points);
            player.sendMessage(AmazingTowers.getColor(this.gameInstance.getConfig(ConfigType.CONFIG).getString("options.rewards.messages." + reward.getName()).replaceAll("%coins%", String.valueOf(points))));
        }
    }

    public double getCoins(Player player) {
        return (SetupVault.getVaultEconomy() != null) ? SetupVault.getVaultEconomy().getBalance(player) : 0.0D;
    }

    public String getPrefixRank(Player player) {
        return (SetupVault.getVaultChat() != null) ? SetupVault.getVaultChat().getPlayerPrefix(player) : "";
    }
}


