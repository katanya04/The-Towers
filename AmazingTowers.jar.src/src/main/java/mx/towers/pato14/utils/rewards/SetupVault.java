package mx.towers.pato14.utils.rewards;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.plugin.PluginA;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
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
        if (plugin.getGlobalConfig().getBoolean("Options.Rewards.vault")) {
            plugin.sendConsoleMessage("");
            plugin.sendConsoleMessage("Detecting if you have the vault plugin...");
            if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
                plugin.sendConsoleMessage("It has been detected that you have the Vault plugin, loading Chat and Economy...");
            } else {
                plugin.sendConsoleMessage("Apparently you do not have the Vault plugin, so the compatibility of this will be disabled");
            }
            if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
                plugin.sendConsoleMessage(setupEconomy() ? "Economy" + economy.getName() : "Economy NONE");
                plugin.sendConsoleMessage(setupChat() ? "Chat" + chat.getName() : "Chat NONE");
            }
        }
    }

    public static Chat getVaultChat() {
        return chat;
    }

    public static Economy getVaultEconomy() {
        return economy;
    }
}


