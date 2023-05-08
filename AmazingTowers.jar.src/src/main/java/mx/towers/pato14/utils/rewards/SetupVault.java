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
            Bukkit.getConsoleSender().sendMessage("");
            Bukkit.getConsoleSender().sendMessage("[AmazingTowers] Detecting if you have the vault plugin...");
            if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
                Bukkit.getConsoleSender().sendMessage("[AmazingTowers] It has been detected that you have the Vault plugin, loading Chat and Economy...");
            } else {
                Bukkit.getConsoleSender().sendMessage("[AmazingTowers] Apparently you do not have the Vault plugin, so the compatibility of this will be disabled");
            }
            if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
                String format = "[AmazingTowers] %s: [%s]";
                Bukkit.getConsoleSender().sendMessage(setupEconomy() ? String.format(format, "Economy", economy.getName()) : String.format(format, "Economy", "NONE"));
                Bukkit.getConsoleSender().sendMessage(setupChat() ? String.format(format, "Chat", chat.getName()) : String.format(format, "Chat", "NONE"));
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


