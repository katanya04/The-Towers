package mx.towers.pato14.utils.mysql;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import com.mysql.jdbc.CommunicationsException;
import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.Utils;
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
        } catch (ClassNotFoundException ignored) {
        }
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

    private Object operation(Operation operation, @Nullable Collection<String> players, @Nullable Collection<String> tables, @Nullable Map<String, Stats> stats) {
        if (tables != null) {
            if (tables.contains(ALL_TABLES))
                tables = getTables();
            else {
                tables = tables.stream().filter(this::isAValidTable).collect(Collectors.toSet());
                if (tables.isEmpty())
                    return null;
            }
        }
        AmazingTowers.logger.logSQLCall(operation, players, tables, stats);
        boolean repeat = false;
        do {
            try {
                switch (operation) {
                    case CREATE_TABLE:
                        return _createTables();
                    case CREATE_ACC:
                        return _createAccount(players, tables);
                    case UPDATE_DATA:
                        return _updateData(stats, tables);
                    case GET_DATA:
                        return _getStats(players, tables);
                    case HAS_ACC:
                        return _hasAccount(players, tables);
                }
            } catch (CommunicationsException | com.mysql.jdbc.exceptions.jdbc4.CommunicationsException ex) {
                if (!repeat && this.connect())
                    repeat = true;
                else {
                    Utils.reportException("Can't stablish a connection with the database", ex);
                    repeat = false;
                }
            } catch (SQLException ex) {
                Utils.reportException("Error while performing operation " + operation + " on tables " + tables, ex);
                repeat = false;
            }
        } while (repeat);
        return null;
    }

    @Override
    public boolean createTables() {
        return (boolean) Utils.getValueOrDefault(operation(Operation.CREATE_TABLE, null, null, null), false);
    }
    @Override
    public void createAccount(Collection<String> players, Collection<String> tables) {
        operation(Operation.CREATE_ACC, players, tables, null);
    }
    @Override
    public void updateData(Map<String, Stats> stats, Collection<String> tables) {
        operation(Operation.UPDATE_DATA, null, tables, stats);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Stats> getStats(Collection<String> players, Collection<String> tables) {
        return (Map<String, Stats>) Utils.getValueOrDefault(operation(Operation.GET_DATA, players, tables, null), new HashMap<String, Stats>());
    }

    @Override
    public boolean hasAccount(Collection<String> players, Collection<String> tables) {
        return (boolean) Utils.getValueOrDefault(operation(Operation.HAS_ACC, players, tables, null), false);
    }

    private boolean _createTables() throws SQLException {
        for (String table : tables) {
            PreparedStatement ps = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + table + "(UUID VARCHAR(37), username VARCHAR(20), kills INT(9)NOT NULL, deaths INT(9)NOT NULL, points INT(9) NOT NULL,games INT(9) NOT NULL,wins INT(9)NOT NULL,Blocks_Broken INT(9)NOT NULL,Blocks_Placed INT(9)NOT NULL,PRIMARY KEY(UUID))");
            ps.execute();
            ps.close();
        }
        return true;
    }

    private boolean _createAccount(Collection<String> players, Collection<String> tables) throws SQLException {
        for (String tableName : tables) {
            StringBuilder sb = new StringBuilder();
            int numPlayers = players.size();
            sb.append("INSERT INTO ").append(tableName);
            for (int i = 0; i < numPlayers; i++) {
                sb.append(" (UUID,username,kills,deaths,points,games,wins,Blocks_Broken,Blocks_Placed) VALUES (?,?,0,0,0,0,0,0,0)");
                if (i != numPlayers - 1)
                    sb.append(",");
            }
            sb.append(" ON DUPLICATE KEY UPDATE UUID = UUID");
            PreparedStatement ps = this.connection.prepareStatement(sb.toString());
            int i = 1;
            for (String player : players) {
                ps.setString(i++, getUUID(player));
                ps.setString(i++, player);
            }
            ps.execute();
            ps.close();
        }
        return true;
    }

    private boolean _updateData(Map<String, Stats> statsMap, Collection<String> tables) throws SQLException {
        for (String tableName : tables) {
            if (statsMap.isEmpty())
                return false;
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO ").append(tableName).append("(UUID,username,kills,deaths,points,games,wins,Blocks_Broken,Blocks_Placed) VALUES");
            int numPlayers = statsMap.size();
            for (int i = 0; i < numPlayers; i++) {
                sb.append(" (?,?,?,?,?,?,?,?,?)");
                if (i != numPlayers - 1)
                    sb.append(",");
            }
            sb.append(" ON DUPLICATE KEY UPDATE ");
            for (StatType st : StatType.values()) {
                sb.append(st.getColumnName()).append(" = ").append(st.getColumnName()).append(" + ")
                        .append("VALUES(").append(st.getColumnName()).append(")").append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            PreparedStatement ps = this.connection.prepareStatement(sb.toString());
            int i = 1;
            for (Map.Entry<String, Stats> entry : statsMap.entrySet()) {
                ps.setString(i++, getUUID(entry.getKey()));
                ps.setString(i++, entry.getKey());
                ps.setInt(i++, entry.getValue().getStat(StatType.KILLS));
                ps.setInt(i++, entry.getValue().getStat(StatType.DEATHS));
                ps.setInt(i++, entry.getValue().getStat(StatType.POINTS));
                ps.setInt(i++, entry.getValue().getStat(StatType.GAMES));
                ps.setInt(i++, entry.getValue().getStat(StatType.WINS));
                ps.setInt(i++, entry.getValue().getStat(StatType.BLOCKS_BROKEN));
                ps.setInt(i++, entry.getValue().getStat(StatType.BLOCKS_PLACED));
            }
            ps.executeUpdate();
            ps.close();
        }
        return true;
    }

    private Map<String, Stats> _getStats(Collection<String> players, Collection<String> tables) throws SQLException {
        Map<String, Stats> statsMap = new HashMap<>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT username, sum(kills) kills,sum(deaths) deaths,sum(points) points,sum(games) games,sum(wins) wins,sum(Blocks_Broken) Blocks_Broken,sum(Blocks_Placed) Blocks_Placed FROM (");
        int i = 0;
        for (String tableName : tables) {
            query.append("SELECT * FROM ").append(tableName);
            if (i != tables.size() - 1) query.append(" UNION ALL ");
            i++;
        }
        query.append(") t");
        query.append(" GROUP BY UUID HAVING UUID IN (");
        for (String player : players)
            query.append("'").append(getUUID(player)).append("',");
        query.deleteCharAt(query.length() - 1);
        query.append(")");
        PreparedStatement ps = this.connection.prepareStatement(query.toString());
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int[] statsValues = new int[StatType.values().length];
            for (int j = 1; j <= statsValues.length; j++)
                statsValues[j - 1] = rs.getInt(j + 1);
            statsMap.put(rs.getString(1), new Stats(statsValues));
        }
        ps.close();
        Set<String> currentPlayers = new HashSet<>(statsMap.keySet());
        Set<String> playersCopy = new HashSet<>(players);
        playersCopy.removeAll(currentPlayers);
        playersCopy.forEach(o -> statsMap.put(o, new Stats()));
        return statsMap;
    }

    private boolean _hasAccount(Collection<String> players, Collection<String> tables) throws SQLException {
        StringBuilder query = new StringBuilder();
        String firstTable = tables.iterator().next();
        query.append("SELECT ").append(firstTable).append(".UUID FROM ").append(firstTable).append(" ");
        for (String table : tables) {
            if (table.equals(firstTable))
                continue;
            query.append("INNER JOIN ").append(table).append(" ON ").append(firstTable).append(".UUID = ").append(table).append(".UUID").append(" ");
        }
        query.append("WHERE ").append(firstTable).append(".UUID IN(");
        for (String player : players)
            query.append("'").append(getUUID(player)).append("',");
        query.deleteCharAt(query.length() - 1);
        query.append(")");
        PreparedStatement ps = this.connection.prepareStatement(query.toString());
        ResultSet rs = ps.executeQuery();
        int numPlayers = players.size();
        while (rs.next())
            numPlayers--;
        ps.close();
        return numPlayers == 0;
    }

    public List<String> getTables() {
        return tables;
    }

    @Override
    public boolean isAValidTable(String tableName) {
        return tableName != null && (getTables().contains(tableName) || IConnexion.ALL_TABLES.equals(tableName));
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