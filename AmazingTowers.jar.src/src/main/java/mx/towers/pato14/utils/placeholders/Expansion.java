package mx.towers.pato14.utils.placeholders;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.mysql.Connexion;
import org.bukkit.OfflinePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class Expansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getAuthor() {
        return "katanya04";
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
        if (!AmazingTowers.isConnectedToDatabase() || AmazingTowers.connexion == null)
            return "0";
        int[] result;
        String tableName;
        if (params.contains("db=") && Utils.isAValidTable(tableName = params.split("db=")[1]))
            result = AmazingTowers.connexion.getStats(player.getName(), tableName);
        else
            result = AmazingTowers.connexion.getStats(player.getName(), Connexion.ALL_TABLES);
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
        } else if (params.equalsIgnoreCase("losses")) {
            toret= Integer.toString(result[3] - result[4]);
        } else {
            toret = null; // Placeholder is unknown by the Expansion
        }
        return toret;
    }
}
