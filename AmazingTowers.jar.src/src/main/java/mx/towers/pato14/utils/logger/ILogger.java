package mx.towers.pato14.utils.logger;

import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.commands.Subcommand;
import mx.towers.pato14.game.events.player.TeamChatListener;
import mx.towers.pato14.utils.mysql.IConnexion;
import mx.towers.pato14.utils.stats.Stats;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public interface ILogger {
    void logSQLCall(IConnexion.Operation operationType, @Nullable Collection<String> player, @Nullable Collection<String> tableName, @Nullable Map<String, Stats> stats);
    void logTowersCommand(CommandSender sender, Subcommand subcommand, String[] args);
    void logChat(String msg, String senderName, TeamChatListener.ChatScope chatScope, TowersWorldInstance instance);
    void closeStream();
}
