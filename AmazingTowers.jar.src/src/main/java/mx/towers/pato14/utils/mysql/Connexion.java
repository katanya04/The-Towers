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
import mx.towers.pato14.utils.stats.StatType;
import mx.towers.pato14.utils.stats.Stats;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

public class Connexion implements IConnexion {
    private final AmazingTowers plugin = AmazingTowers.getPlugin();
    public Connection connection;
    private final List<String> tables;
    private final String hostname;
    //private final String port;
    private final String database;
    private final String user;
    private final String password;
    private boolean isConnected;

    public Connexion(ConfigurationSection databaseInfo) {
        this.tables = databaseInfo.getStringList("tableNames");
        this.tables.remove(ALL_TABLES);
        this.hostname = databaseInfo.getString("hostname");
        this.database = databaseInfo.getString("database");
        this.user = databaseInfo.getString("user");
        this.password = databaseInfo.getString("password");
    }

    @Override
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
        } catch (ClassNotFoundException ignored) {}
        return false;
    }

    @Override
    public boolean close() {
        try {
            if (connection != null && !connection.isClosed())
                connection.close();
            return true;
        } catch (Exception e) {
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
                    case UPDATE_DATA:
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

    @Override
    public boolean createTables() {
        return (boolean) Utils.getValueOrDefault(operation(Operation.CREATE_TABLE, null, null, null), false);
    }

    @Override
    public void createAccount(String player, String tableName) {
        operation(Operation.CREATE_ACC, player, tableName, null);
    }

    @Override
    public void updateData(String player, Stats stats, String tableName) {
        operation(Operation.UPDATE_DATA, player, tableName, stats);
    }

    @Override
    public int[] getStats(String player, String tableName) {
        return (int[]) Utils.getValueOrDefault(operation(Operation.GET_DATA, player, tableName, null), new int[7]);
    }

    @Override
    public boolean hasAccount(String player, String tableName) {
        return (boolean) Utils.getValueOrDefault(operation(Operation.HAS_ACC, player, tableName, null), false);
    }

    private boolean _createTables() throws SQLException {
        for (String table : tables) {
            PreparedStatement ps = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + table + "(UUID VARCHAR(37), PlayerName VARCHAR(20), Kills INT(9)NOT NULL, Deaths INT(9)NOT NULL, Anoted_Points INT(9) NOT NULL,Games_Played INT(9) NOT NULL,Wins INT(9)NOT NULL,Blocks_Broken INT(9)NOT NULL,Blocks_Placed INT(9)NOT NULL,PRIMARY KEY(UUID))");
            ps.execute();
            ps.close();
        }
        return true;
    }

    private boolean _createAccount(String player, String tableName) throws SQLException {
        if (ALL_TABLES.equals(tableName)) {
            for (String table : tables)
                _createAccount(player, table);
        } else {
            PreparedStatement ps = this.connection.prepareStatement("INSERT INTO " + tableName +
                    "(UUID,PlayerName,Kills,Deaths,Anoted_Points,Games_Played,Wins,Blocks_Broken,Blocks_Placed) VALUES (?,?,0,0,0,0,0,0,0) ON DUPLICATE KEY UPDATE UUID = UUID");
            ps.setString(1, UUID.nameUUIDFromBytes(("OfflinePlayer:" + player).getBytes(StandardCharsets.UTF_8)).toString());
            ps.setString(2, player);
            ps.execute();
            ps.close();
        }
        return true;
    }

    private boolean _updateData(String player, Stats stats, String tableName) throws SQLException {
        StringBuilder sb = new StringBuilder();
        for (StatType st : StatType.values()) {
            sb.append(st.getFieldName()).append("=").append(st.getFieldName()).append("+").append(stats.getStat(st)).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        PreparedStatement ps = this.connection.prepareStatement("INSERT INTO " + tableName +
                "(UUID,PlayerName,Kills,Deaths,Anoted_Points,Games_Played,Wins,Blocks_Broken,Blocks_Placed) VALUES (?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " + sb);
        ps.setString(1, UUID.nameUUIDFromBytes(("OfflinePlayer:" + player).getBytes(StandardCharsets.UTF_8)).toString());
        ps.setString(2, player);
        ps.setInt(3, stats.getStat(StatType.KILLS));
        ps.setInt(4, stats.getStat(StatType.DEATHS));
        ps.setInt(5, stats.getStat(StatType.POINTS));
        ps.setInt(6, stats.getStat(StatType.GAMES_PLAYED));
        ps.setInt(7, stats.getStat(StatType.WINS));
        ps.setInt(8, stats.getStat(StatType.BLOCKS_BROKEN));
        ps.setInt(9, stats.getStat(StatType.BLOCKS_PLACED));
        ps.executeUpdate();
        ps.close();
        return true;
    }

    private int[] _getStats(String player, String tableName) throws SQLException {
        int[] data = new int[7];
        StringBuilder query = new StringBuilder();
        if (!ALL_TABLES.equals(tableName)) {
            query.append("SELECT Kills, Deaths, Anoted_Points, Games_Played, Wins, Blocks_Broken, Blocks_Placed FROM ").append(tableName);
        } else {
            query.append("SELECT sum(Kills) Kills,sum(Deaths) Deaths,sum(Anoted_Points) Anoted_Points,sum(Games_Played) Games_Played,sum(Wins) Wins,sum(Blocks_Broken) Blocks_Broken,sum(Blocks_Placed) Blocks_Placed FROM (");
            for (int i = 0; i < tables.size(); i++) {
                query.append("SELECT * FROM ").append(tables.get(i));
                if (i != tables.size() - 1) query.append(" UNION ALL ");
            }
            query.append(") t");
        }
        query.append(" WHERE UUID ='").append(getUUID(player)).append("'");
        PreparedStatement ps = this.connection.prepareStatement(query.toString());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            for (int j = 0; j < data.length; j++) {
                data[j] = rs.getInt(j + 1);
            }
        }
        ps.close();
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
        query.append("WHERE ").append(ALL_TABLES.equals(tableName) ? tables.get(0) : tableName).append(".UUID ='").append(getUUID(player)).append("'");
        PreparedStatement ps = this.connection.prepareStatement(query.toString());
        ResultSet rs = ps.executeQuery();
        boolean toret = rs.next();
        ps.close();
        return toret;
    }

    public List<String> getTables() {
        return tables;
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    private String getUUID(String player) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + player)
                .getBytes(StandardCharsets.UTF_8)).toString();
    }
}