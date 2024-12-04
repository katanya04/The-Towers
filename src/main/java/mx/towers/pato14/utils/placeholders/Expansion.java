package mx.towers.pato14.utils.placeholders;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.mysql.IConnexion;
import mx.towers.pato14.utils.stats.StatType;
import mx.towers.pato14.utils.stats.Stats;
import org.bukkit.OfflinePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;

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
        Stats playerStats;
        String tableName;
        if (params.contains("db=") && AmazingTowers.connexion.isAValidTable(tableName = params.split("db=")[1]))
            playerStats = AmazingTowers.connexion.getStats(player.getName(), tableName);
        else
            playerStats = AmazingTowers.connexion.getStats(player.getName(), IConnexion.ALL_TABLES);
        if (params.equalsIgnoreCase("kills")) {
            toret = Integer.toString(playerStats.getStat(StatType.KILLS));
        } else if (params.equalsIgnoreCase("points")) {
            toret = Integer.toString(playerStats.getStat(StatType.POINTS));
        } else if (params.equalsIgnoreCase("wins")) {
            toret = Integer.toString(playerStats.getStat(StatType.WINS));
        } else if (params.equalsIgnoreCase("games_played")) {
            toret = Integer.toString(playerStats.getStat(StatType.GAMES_PLAYED));
        } else if (params.equalsIgnoreCase("deaths")) {
            toret = Integer.toString(playerStats.getStat(StatType.DEATHS));
        } else if (params.equalsIgnoreCase("losses")) {
            toret= Integer.toString(playerStats.getStat(StatType.GAMES_PLAYED) - playerStats.getStat(StatType.WINS));
        } else {
            toret = null; // Placeholder is unknown by the Expansion
        }
        return toret;
    }
}
