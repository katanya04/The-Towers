package mx.towers.pato14.utils.mysql;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import com.mysql.jdbc.CommunicationsException;
import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.Utils;
import mx.towers.pato14.utils.enums.MessageType;
import mx.towers.pato14.utils.files.Logger;
import mx.towers.pato14.utils.stats.StatType;
import mx.towers.pato14.utils.stats.Stats;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

public class Connexion {
    private final AmazingTowers plugin = AmazingTowers.getPlugin();
    public static String ALL_TABLES = "ALL_TABLES";
    public Connection connection;
    private final List<String> tables;
    private final String hostname;
    //private final String port = this.t.getGlobalConfig().getString("options.database.port");
    private final String database;
    private final String user;
    private final String password;
    private boolean isConnected;

    public enum Operation {
        CREATE_TABLE(Logger.SQLCallType.WRITE),
        CREATE_ACC(Logger.SQLCallType.WRITE),
        SET_DATA(Logger.SQLCallType.WRITE),
        GET_DATA(Logger.SQLCallType.READ),
        HAS_ACC(Logger.SQLCallType.READ);
        private final Logger.SQLCallType sqlCallType;
        Operation(Logger.SQLCallType sqlCallType) {
            this.sqlCallType = sqlCallType;
        }
        public Logger.SQLCallType getSqlCallType() {
            return sqlCallType;
        }
    }

    public Connexion(ConfigurationSection databaseInfo) {
        this.tables = databaseInfo.getStringList("tableNames");
        this.tables.remove(ALL_TABLES);
        this.hostname = databaseInfo.getString("hostname");
        this.database = databaseInfo.getString("database");
        this.user = databaseInfo.getString("user");
        this.password = databaseInfo.getString("password");
    }

    public boolean initialize() {
        return (isConnected = connect()) && createTables();
    }

    public boolean connect() {
        try {
            if (this.connection != null && !this.connection.isClosed())
                this.connection.close();
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + /*":" + this.port +*/
                    "/" + this.database + "?autoReconnect=true", this.user, this.password);
            return true;
        } catch (SQLException e) {
            AmazingTowers.getGlobalConfig().set("options.database.active", false);
            this.plugin.saveConfig();
        } catch (ClassNotFoundException ignored) {
        }
        return false;
    }

    public boolean close() {
        try {
            if (connection != null && !connection.isClosed())
                connection.close(); // closing the connection field variable.
            return true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Object operation(Operation operation, @Nullable String player, @Nullable String tableName, @Nullable Stats stats) {
        if (tableName != null && !Utils.isAValidTable(tableName))
            return null;
        AmazingTowers.logger.logSQLCall(operation, player, tableName, stats);
        boolean repeat = false;
        do {
            try {
                switch (operation) {
                    case CREATE_TABLE:
                        return _createTables();
                    case CREATE_ACC:
                        return _createAccount(player, tableName);
                    case SET_DATA:
                        return _updateData(player, stats, tableName);
                    case GET_DATA:
                        return _getStats(player, tableName);
                    case HAS_ACC:
                        return _hasAccount(player, tableName);
                }
            } catch (CommunicationsException | com.mysql.jdbc.exceptions.jdbc4.CommunicationsException ex) {
                if (!repeat && this.connect())
                    repeat = true;
                else {
                    Utils.sendConsoleMessage("Can't stablish a connection with the database", MessageType.ERROR);
                    ex.printStackTrace();
                    repeat = false;
                }
            } catch (SQLException ex) {
                Utils.sendConsoleMessage("Error while performing operation " + operation + " on table " + tableName, MessageType.ERROR);
                ex.printStackTrace();
                repeat = false;
            }
        } while (repeat);
        return false;
    }

    public boolean createTables() {
        return (boolean) Utils.getValueOrDefault(operation(Operation.CREATE_TABLE, null, null, null), false);
    }

    public void createAccount(String player, String tableName) {
        operation(Operation.CREATE_ACC, player, tableName, null);
    }

    public void updateData(String player, Stats stats, String tableName) {
        operation(Operation.SET_DATA, player, tableName, stats);
    }

    public int[] getStats(String player, String tableName) {
        return (int[]) Utils.getValueOrDefault(operation(Operation.GET_DATA, player, tableName, null), new int[7]);
    }

    public boolean hasAccount(String player, String tableName) {
        return (boolean) Utils.getValueOrDefault(operation(Operation.HAS_ACC, player, tableName, null), false);
    }

    private boolean _createTables() throws SQLException {
        for (String table : tables) {
            PreparedStatement ps = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + table + "(UUID VARCHAR(37), PlayerName VARCHAR(20), Kills INT(9)NOT NULL, Deaths INT(9)NOT NULL, Anoted_Points INT(9) NOT NULL,Games_Played INT(9) NOT NULL,Wins INT(9)NOT NULL,Blocks_Broken INT(9)NOT NULL,Blocks_Placed INT(9)NOT NULL,PRIMARY KEY(UUID))");
            ps.execute();
            ps.close();
            return true;
        }
        return false;
    }

    private boolean _createAccount(String player, String tableName) throws SQLException {
        if (ALL_TABLES.equals(tableName)) {
            for (String table : tables)
                _createAccount(player, table);
        } else if (!hasAccount(player, tableName)) {
            PreparedStatement ps = this.connection.prepareStatement("INSERT INTO " + tableName + "(UUID,PlayerName,Kills,Deaths,Anoted_Points,Games_Played,Wins,Blocks_Broken,Blocks_Placed) VALUES (?,?,0,0,0,0,0,0,0)");
            ps.setString(1, UUID.nameUUIDFromBytes(("OfflinePlayer:" + player).getBytes(StandardCharsets.UTF_8)).toString());
            ps.setString(2, player);
            ps.execute();
            ps.close();
        }
        return true;
    }

    private boolean _updateData(String player, Stats stats, String tableName) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ").append(tableName).append(" SET ");
        for (StatType st : StatType.values()) {
            sb.append(st.getFieldName()).append("=").append(st.getFieldName()).append(" + ")
                    .append(stats.getStat(st)).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" WHERE UUID='").append(UUID.nameUUIDFromBytes(("OfflinePlayer:" + player)
                .getBytes(StandardCharsets.UTF_8))).append("'");
        PreparedStatement ps = this.connection.prepareStatement(sb.toString());
        ps.executeUpdate();
        ps.close();
        return true;
    }

    private int[] _getStats(String player, String tableName) throws SQLException {
        int[] data = new int[7];
        StringBuilder query = new StringBuilder();
        if (!ALL_TABLES.equals(tableName)) {
            query.append("SELECT * FROM ").append(tableName);
        } else {
            query.append("SELECT UUID, PlayerName,sum(Kills) Kills,sum(Deaths) Deaths,sum(Anoted_Points) Anoted_Points,sum(Games_Played) Games_Played,sum(Wins) Wins,sum(Blocks_Broken) Blocks_Broken,sum(Blocks_Placed) Blocks_Placed FROM (");
            for (int i = 0; i < tables.size(); i++) {
                query.append("SELECT * FROM ").append(tables.get(i));
                if (i != tables.size() - 1) query.append(" UNION ALL ");
            }
            query.append(") t");
        }
        query.append(" WHERE UUID ='").append(UUID.nameUUIDFromBytes(("OfflinePlayer:" + player).getBytes(StandardCharsets.UTF_8))).append("'");
        if (hasAccount(player, tableName)) {
            PreparedStatement ps = this.connection.prepareStatement(query.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                for (int j = 0; j < data.length; j++) {
                    data[j] = rs.getInt(3 + j);
                }
            }
        }
        return data;
    }

    private boolean _hasAccount(String player, String tableName) throws SQLException {
        StringBuilder query = new StringBuilder();
        if (!ALL_TABLES.equals(tableName)) {
            query.append("SELECT UUID FROM ").append(tableName).append(" ");
        } else {
            query.append("SELECT ").append(tables.get(0)).append(".UUID FROM ").append(tables.get(0)).append(" ");
            for (int i = 1; i < tables.size(); i++) {
                query.append("INNER JOIN ").append(tables.get(i)).append(" ON ").append(tables.get(0)).append(".UUID = ").append(tables.get(i)).append(".UUID").append(" ");
            }
        }
        query.append("WHERE ").append(ALL_TABLES.equals(tableName) ? tables.get(0) : tableName).append(".UUID ='").append(UUID.nameUUIDFromBytes(("OfflinePlayer:" + player).getBytes(StandardCharsets.UTF_8))).append("'");
        PreparedStatement ps = this.connection.prepareStatement(query.toString());
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }

    public List<String> getTables() {
        return tables;
    }

    public boolean isConnected() {
        return isConnected;
    }
}