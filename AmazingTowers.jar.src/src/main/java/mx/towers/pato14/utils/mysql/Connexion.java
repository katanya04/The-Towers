package mx.towers.pato14.utils.mysql;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.mysql.jdbc.CommunicationsException;
import mx.towers.pato14.AmazingTowers;
import mx.towers.pato14.utils.enums.MessageType;
import mx.towers.pato14.utils.stats.StatType;
import mx.towers.pato14.utils.stats.Stats;

public class Connexion {
    AmazingTowers plugin = AmazingTowers.getPlugin();
    public Connection connection;
    String hostname = AmazingTowers.getGlobalConfig().getString("options.mysql.hostname");
    //String port = this.t.getGlobalConfig().getString("options.mysql.port");
    String database = AmazingTowers.getGlobalConfig().getString("options.mysql.database");
    String user = AmazingTowers.getGlobalConfig().getString("options.mysql.user");
    String password = AmazingTowers.getGlobalConfig().getString("options.mysql.password");
    public void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + /*":" + this.port +*/
                    "/" + this.database + "?autoReconnect=true", this.user, this.password);
        } catch (SQLException e) {
            AmazingTowers.getGlobalConfig().set("options.mysql.active", false);
            this.plugin.saveConfig();
        } catch (ClassNotFoundException ignored) {}
    }

    public void createTable() {
        boolean repeat = false;
        do {
            try {
                PreparedStatement ps = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS towers(UUID VARCHAR(37), PlayerName VARCHAR(20), Kills INT(9)NOT NULL, Deaths INT(9)NOT NULL, Anoted_Points INT(9) NOT NULL,Games_Played INT(9) NOT NULL,Wins INT(9)NOT NULL,Blocks_Broken INT(9)NOT NULL,Blocks_Placed INT(9)NOT NULL,PRIMARY KEY(UUID))");
                ps.execute();
                ps.close();
                repeat = false;
            } catch (CommunicationsException e) {
                this.connect();
                repeat = true;
            } catch (SQLException e) {
                plugin.sendConsoleMessage("§cError while creating the database table: " + e, MessageType.ERROR);
            }
        } while (repeat);
    }

    public void createAccount(String player) {
        boolean repeat = false;
        do {
            try {
                if (!hasAccount(player)) {
                    PreparedStatement ps = this.connection.prepareStatement("INSERT INTO towers(UUID,PlayerName,Kills,Deaths,Anoted_Points,Games_Played,Wins,Blocks_Broken,Blocks_Placed) VALUES (?,?,?,?,?,?,?,?,?)");
                    ps.setString(1, UUID.nameUUIDFromBytes(("OfflinePlayer:" + player).getBytes(StandardCharsets.UTF_8)).toString());
                    ps.setString(2, player);
                    ps.setInt(3, 0);
                    ps.setInt(4, 0);
                    ps.setInt(5, 0);
                    ps.setInt(6, 0);
                    ps.setInt(7, 0);
                    ps.setInt(8, 0);
                    ps.setInt(9, 0);
                    ps.execute();
                    ps.close();
                }
                repeat = false;
            } catch (CommunicationsException e) {
                this.connect();
                repeat = true;
            } catch (SQLException e) {
                plugin.sendConsoleMessage("§cError while adding an entry to the database: " + e, MessageType.ERROR);
            }
        } while (repeat);
    }
    public void updateData(String player, Stats stats) {
        boolean repeat = false;
        do {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("UPDATE towers SET ");
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
                repeat = false;
            } catch (CommunicationsException e) {
                this.connect();
                repeat = true;
            } catch (SQLException e) {
                plugin.sendConsoleMessage("§cError while updating the database: " + e, MessageType.ERROR);
            }
        } while (repeat);
    }

    public int[] getStats(String player) {
        boolean repeat = false;
        int[] data;
        do {
            data = new int[7];
            try {
                if (hasAccount(player)) {
                    PreparedStatement ps = this.connection.prepareStatement("SELECT * FROM towers WHERE UUID ='" + UUID.nameUUIDFromBytes(("OfflinePlayer:" + player).getBytes(StandardCharsets.UTF_8)) + "'");
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        for (int j = 0; j < 7; j++) {
                            data[j] = rs.getInt(3 + j);
                        }
                    }
                } else {
                    data = new int[]{0, 0, 0, 0, 0, 0, 0};
                }
                repeat = false;
            } catch (CommunicationsException e) {
                this.connect();
                repeat = true;
            } catch (SQLException e) {
                plugin.sendConsoleMessage("§cError while getting data of the database: " + e, MessageType.ERROR);
            }
        } while (repeat);
        return data;
    }

    public boolean hasAccount(String player) {
        boolean repeat = false;
        do {
            try {
                PreparedStatement ps = this.connection.prepareStatement("Select PlayerName From towers WHERE PlayerName ='" + player + "'");
                ResultSet rs = ps.executeQuery();
                if (rs.next())
                    return true;
                repeat = false;
            } catch (CommunicationsException e) {
                this.connect();
                repeat = true;
            } catch (SQLException e) {
                plugin.sendConsoleMessage("§cError while checking the database: " + e, MessageType.ERROR);
            }
        } while (repeat);
        return false;
    }
}