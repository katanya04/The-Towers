package mx.towers.pato14.utils.placeholders;

import mx.towers.pato14.AmazingTowers;
import org.bukkit.OfflinePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

public class Expansion extends PlaceholderExpansion {
    private final AmazingTowers plugin;

    public Expansion(AmazingTowers plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getAuthor() {
        return "Marco2124";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "towers";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        String toret;
        if (plugin.connexion == null)
            return "0";
        int[] result = plugin.connexion.getStats(player.getName());
        if (params.equalsIgnoreCase("kills")) {
            toret = Integer.toString(result[0]);
        } else if (params.equalsIgnoreCase("points")) {
            toret = Integer.toString(result[2]);
        } else if (params.equalsIgnoreCase("wins")) {
            toret = Integer.toString(result[4]);
        } else if (params.equalsIgnoreCase("games_played")) {
            toret = Integer.toString(result[3]);
        } else if (params.equalsIgnoreCase("deaths")) {
            toret = Integer.toString(result[1]);
        } else {
            toret = null; // Placeholder is unknown by the Expansion
        }
        return toret;
    }
}
