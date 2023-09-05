package mx.towers.pato14.utils.rewards;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.enums.MessageType;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class SetupVault {
    private static final AmazingTowers plugin = AmazingTowers.getPlugin();
    private static Chat chat;
    private static Economy economy;

    private static boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = plugin.getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }
        return (chat != null);
    }

    private static boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        return (economy != null);
    }

    public static void setupVault() {
        if (plugin.getGlobalConfig().getBoolean("options.rewards.vault")) {
            plugin.sendConsoleMessage("Looking for the vault plugin...", MessageType.INFO);
            if (plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
                plugin.sendConsoleMessage("It has been detected that you have the Vault plugin, loading Chat and Economy...", MessageType.INFO);
                plugin.sendConsoleMessage(setupEconomy() ? "§aEconomy§f: " + economy.getName() : "§aEconomy§f: [NONE]", MessageType.INFO);
                plugin.sendConsoleMessage(setupChat() ? "§aChat§f: " + chat.getName() : "§aChat§f: [NONE]", MessageType.INFO);
            } else {
                plugin.sendConsoleMessage("Apparently you do not have the Vault plugin, so the compatibility of this will be disabled", MessageType.WARNING);
                plugin.getGlobalConfig().set("options.rewards.vault", false);
            }
        }
    }

    public static Chat getVaultChat() {
        return chat;
    }

    public static Economy getVaultEconomy() {
        return economy;
    }

    public static double getCoins(Player player) {
        return (getVaultEconomy() != null) ? getVaultEconomy().getBalance(player) : 0.0D;
    }

    public static String getPrefixRank(Player player) {
        return (SetupVault.getVaultChat() != null) ? SetupVault.getVaultChat().getPlayerPrefix(player) : "";
    }
}


