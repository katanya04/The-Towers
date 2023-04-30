package mx.towers.pato14.utils.rewards;

import mx.towers.pato14.AmazingTowers;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class VaultT {
    private AmazingTowers plugin;

    public VaultT(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    public boolean setReward(Player player, RewardsEnum reward) {
        if (this.plugin.getConfig().getBoolean("Options.Rewards.vault") &&
                SetupVault.getVaultEconomy() != null) {
            int points = this.plugin.getConfig().getInt("Options.Rewards.reward." + reward.getName());
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
            SetupVault.getVaultEconomy().depositPlayer((OfflinePlayer) player, points);
            player.sendMessage(this.plugin.getColor(this.plugin.getConfig().getString("Options.Rewards.messages." + reward.getName()).replaceAll("%coins%", String.valueOf(points))));
            return true;
        }
        return false;
    }

    public double getCoins(Player player) {
        return (SetupVault.getVaultEconomy() != null) ? SetupVault.getVaultEconomy().getBalance((OfflinePlayer) player) : 0.0D;
    }

    public String getPrefixRank(Player player) {
        return (SetupVault.getVaultChat() != null) ? SetupVault.getVaultChat().getPlayerPrefix(player) : "";
    }
}


