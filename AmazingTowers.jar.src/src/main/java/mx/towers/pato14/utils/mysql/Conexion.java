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
import mx.towers.pato14.utils.enums.StatType;
import mx.towers.pato14.utils.stats.Stats;
import org.bukkit.Bukkit;

public class Conexion {
    AmazingTowers t = AmazingTowers.getPlugin();

    public Connection c;

    String hostname = this.t.getConfig().getString("Options.mysql.hostname");

    //String port = this.t.getConfig().getString("Options.mysql.port");

    String database = this.t.getConfig().getString("Options.mysql.database");

    String user = this.t.getConfig().getString("Options.mysql.user");

    String password = this.t.getConfig().getString("Options.mysql.password");

    public void Conectar() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.c = DriverManager.getConnection("jdbc:mysql://" + this.hostname + /*":" + this.port +*/
                    "/" + this.database + "?autoReconnect=true", this.user, this.password);
            if (this.c != null)
                this.t.message("Connection of MYSQL set successfully");
        } catch (SQLException e) {
            this.t.message("Error MySql: Error connecting of MYSQL (Disabling in config); " + e);
            this.t.getConfig().set("Options.mysql.active", Boolean.valueOf(false));
            this.t.saveConfig();
        } catch (ClassNotFoundException classNotFoundException) {}
    }

    public void CreateTable() {
        boolean repeat = false;
        do {
            try {
                PreparedStatement ps = this.c.prepareStatement("CREATE TABLE IF NOT EXISTS towers(UUID VARCHAR(37), PlayerName VARCHAR(20), Kills INT(9)NOT NULL, Deaths INT(9)NOT NULL, Anoted_Points INT(9) NOT NULL,Games_Played INT(9) NOT NULL,Wins INT(9)NOT NULL,Blocks_Broken INT(9)NOT NULL,Blocks_Placed INT(9)NOT NULL,PRIMARY KEY(UUID))");
                ps.execute();
                ps.close();
                repeat = false;
            } catch (CommunicationsException e) {
                this.Conectar();
                repeat = true;
            } catch (SQLException e) {
                this.t.message("Error MySql: " + e);
            }
        } while (repeat);
    }

    public void CreateAcount(String playername) {
        boolean repeat = false;
        do {
            try {
                if (!hasAccount(playername)) {
                    PreparedStatement ps = this.c.prepareStatement("INSERT INTO towers(UUID,PlayerName,Kills,Deaths,Anoted_Points,Games_Played,Wins,Blocks_Broken,Blocks_Placed) VALUES (?,?,?,?,?,?,?,?,?)");
                    ps.setString(1, UUID.nameUUIDFromBytes(("OfflinePlayer:" + playername).getBytes(StandardCharsets.UTF_8)).toString());
                    ps.setString(2, playername);
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
                this.Conectar();
                repeat = true;
            } catch (SQLException e) {
                System.out.println("Error mysql: " + e);
            }
        } while (repeat);
    }
    public void UpdateData(String playername, Stats stats) {
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
                sb.append(" WHERE UUID='").append(UUID.nameUUIDFromBytes(("OfflinePlayer:" + playername)
                        .getBytes(StandardCharsets.UTF_8)).toString()).append("'");
                PreparedStatement ps = this.c.prepareStatement(sb.toString());
                ps.executeUpdate();
                ps.close();
                repeat = false;
            } catch (CommunicationsException e) {
                this.Conectar();
                repeat = true;
            } catch (SQLException e) {
                this.t.message("Error MySql: " + e);
            }
        } while (repeat);
    }

    public int[] getData(String playername) {
        boolean repeat = false;
        int i[];
        do {
            i = new int[7];
            try {
                if (hasAccount(playername)) {
                    PreparedStatement ps = this.c.prepareStatement("SELECT * FROM towers WHERE UUID ='" + UUID.nameUUIDFromBytes(("OfflinePlayer:" + playername).getBytes(StandardCharsets.UTF_8)).toString() + "'");
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        for (int j = 0; j < 7; j++) {
                            i[j] = rs.getInt(3 + j);
                        }
                    }
                } else {
                    i = null;
                }
                repeat = false;
            } catch (CommunicationsException e) {
                this.Conectar();
                repeat = true;
            } catch (SQLException e) {
                this.t.message("Error MySql: " + e);
            }
        } while (repeat);
        return i;
    }

    public boolean hasAccount(String playername) {
        boolean repeat = false;
        do {
            try {
                PreparedStatement ps = this.c.prepareStatement("Select PlayerName From towers WHERE PlayerName ='" + playername + "'");
                ResultSet rs = ps.executeQuery();
                if (rs.next())
                    return true;
                repeat = false;
            } catch (CommunicationsException e) {
                this.Conectar();
                repeat = true;
            } catch (SQLException e) {
                this.t.message("Error MySql: " + e);
            }
        } while (repeat);
        return false;
    }
}

