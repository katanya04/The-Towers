package mx.towers.pato14.utils.logger;

import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.TowersWorldInstance;
import mx.towers.pato14.commands.Subcommand;
import mx.towers.pato14.game.events.player.TeamChatListener;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.PermissionLevel;
import mx.towers.pato14.utils.mysql.IConnexion;
import mx.towers.pato14.utils.stats.Stats;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Logger implements ILogger {
    private enum LogType{TOWERS_COMMAND, SQL_CALL, CHAT}
    private boolean activated;
    private boolean logTowersCommand;
    private PermissionLevel permLevelToLog; //will log only commands that require this permission or higher
    private boolean logSQLCalls;
    public enum SQLCallType{
        READ, WRITE, ALL;
        public boolean shouldLog(SQLCallType sqlCallType) {
            return this == ALL || this == sqlCallType;
        }
        public static SQLCallType getOrDefault(String sqlCallType, SQLCallType def) {
            for (SQLCallType call : SQLCallType.values()) {
                if (call.name().equalsIgnoreCase(sqlCallType))
                    return call;
            }
            return def;
        }
    }
    private SQLCallType sqlCallType;
    private boolean logChat;
    private Set<TeamChatListener.ChatScope> chatScopeToLog;
    private int hoursPerNewLog;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> createNewLogFile;
    private ScheduledFuture<?> flushStream;
    private File log;
    private FileWriter fileWriter;
    private BufferedWriter bufferedWriter;
    private PrintWriter printWriter;
    public Logger(boolean activated, boolean logSQLCalls, SQLCallType sqlCallType, boolean logTowersCommand,
                  PermissionLevel permLevelToLog, boolean logChat, Set<TeamChatListener.ChatScope> chatScopeToLog, int hoursPerNewLog) {
        setActivated(activated);
        setLogSQLCalls(logSQLCalls, sqlCallType);
        setLogTowersCommand(logTowersCommand, permLevelToLog);
        setLogChat(logChat, chatScopeToLog);
        this.scheduler = Executors.newScheduledThreadPool(1);
        setHoursPerNewLog(hoursPerNewLog);
    }

    public void setActivated(boolean activated) {
        if (activated && !this.activated)
            newLogFile();
        this.activated = activated;
    }

    public void setLogSQLCalls(boolean logSQLCalls, SQLCallType sqlCallType) {
        this.logSQLCalls = logSQLCalls;
        this.sqlCallType = sqlCallType;
    }

    public void setLogTowersCommand(boolean logTowersCommand, PermissionLevel permLevelToLog) {
        this.logTowersCommand = logTowersCommand;
        this.permLevelToLog = permLevelToLog;
    }

    public void setLogChat(boolean logChat, Set<TeamChatListener.ChatScope> chatScopeToLog) {
        this.logChat = logChat;
        this.chatScopeToLog = chatScopeToLog;
    }

    public void setHoursPerNewLog(int hoursPerNewLog) {
        this.hoursPerNewLog = hoursPerNewLog;
        if (this.createNewLogFile != null)
            this.createNewLogFile.cancel(false);
        this.createNewLogFile = scheduler.scheduleAtFixedRate(this::newLogFile, this.hoursPerNewLog, this.hoursPerNewLog, TimeUnit.HOURS);
    }

    @Override
    public void logSQLCall(IConnexion.Operation operationType, @Nullable String player, @Nullable String tableName,
                           @Nullable Stats stats) {
        if (!this.logSQLCalls || !this.sqlCallType.shouldLog(operationType.getSqlCallType()))
            return;
        StringBuilder logText = new StringBuilder();
        logText.append("Operation ").append(Utils.macroCaseToItemName(operationType.name()));
        if (player != null)
            logText.append(" on player ").append(player);
        if (tableName != null)
            logText.append(" on table ").append(tableName);
        if (stats != null)
            logText.append(" with stats ").append(stats);
        log(LogType.SQL_CALL, logText.toString());
    }

    @Override
    public void logTowersCommand(CommandSender sender, Subcommand subcommand, String[] args) {
        if (!this.logTowersCommand || !PermissionLevel.hasPermission(this.permLevelToLog, subcommand.getPermissionLevel()))
            return;
        String logText = sender.getName() + " executed " + Subcommand.argsBuilder(args, ' ');
        log(LogType.TOWERS_COMMAND, logText);
    }

    @Override
    public void logChat(String msg, String senderName, TeamChatListener.ChatScope chatScope, TowersWorldInstance instance) {
        if (!this.logChat || !this.chatScopeToLog.contains(chatScope))
            return;
        String logText = "(" + instance.getName() + ")" + senderName + ": " + msg;
        log(LogType.CHAT, logText);
    }

    private void log(@NotNull LogType logType, String logText) {
        String logLine = "[" + LocalTime.now().toString().split("\\.")[0] + "] [" + Utils.macroCaseToItemName(logType.name()) +
                "]: " + logText;
        if (this.printWriter == null)
            openNewStream();
        this.printWriter.println(logLine);
        if (this.flushStream != null)
            this.flushStream.cancel(true);
        this.flushStream = this.scheduler.schedule(() -> this.printWriter.flush(), 5, TimeUnit.SECONDS);
    }

    public void newLogFile() {
        closeStream();
        log = getNewFilePath();
        openNewStream();
    }

    @Override
    public void closeStream() {
        try {
            if (this.printWriter != null)
                this.printWriter.close();
            if (this.bufferedWriter != null)
                this.bufferedWriter.close();
            if (this.fileWriter != null)
                this.fileWriter.close();
        } catch (Exception ex) {
            throw new RuntimeException("Unable to close write stream");
        }
    }

    private void openNewStream() {
        try {
            this.fileWriter = new FileWriter(this.log.getPath(), true);
            this.bufferedWriter = new BufferedWriter(this.fileWriter);
            this.printWriter = new PrintWriter(this.bufferedWriter);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to open write stream");
        }
    }

    private static File getNewFilePath() {
        try {
            Files.createDirectories(Paths.get(AmazingTowers.getPlugin().getDataFolder().getPath() + "/logs/"));
            String pathString = AmazingTowers.getPlugin().getDataFolder().getPath() + "/logs/latest.log";
            File latest = new File(pathString);
            if (latest.exists() && latest.isFile()) {
                String date = Utils.fileTimeToDate(Files.getLastModifiedTime(latest.toPath()));
                int i = 1;
                File latestCompressed = new File(AmazingTowers.getPlugin().getDataFolder().getPath() + "/logs/" +
                        date + "-" + i + ".log.gz");
                while (latestCompressed.exists() && latestCompressed.isFile()) {
                    i++;
                    latestCompressed = new File(AmazingTowers.getPlugin().getDataFolder().getPath() + "/logs/" +
                            date + "-" + i + ".log.gz");
                }
                Utils.compressGzip(latest.toPath(), latestCompressed.toPath());
                latest.delete();
            }
            if (!latest.createNewFile())
                throw new IOException();
            return latest;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}