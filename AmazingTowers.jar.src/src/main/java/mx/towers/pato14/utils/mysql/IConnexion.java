package mx.towers.pato14.utils.mysql;

import mx.towers.pato14.utils.logger.Logger;
import mx.towers.pato14.utils.stats.Stats;

import java.util.List;

public interface IConnexion {
    String ALL_TABLES = "ALL_TABLES";
    boolean initialize();
    boolean close();
    boolean isConnected();
    boolean createTables();
    void createAccount(String player, String tableName);
    void updateData(String player, Stats stats, String tableName);
    int[] getStats(String player, String tableName);
    boolean hasAccount(String player, String tableName);
    List<String> getTables();

    enum Operation {
        CREATE_TABLE(Logger.SQLCallType.WRITE),
        CREATE_ACC(Logger.SQLCallType.WRITE),
        UPDATE_DATA(Logger.SQLCallType.WRITE),
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
}
