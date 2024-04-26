package mx.towers.pato14.utils.mysql;

import mx.towers.pato14.utils.logger.Logger;
import mx.towers.pato14.utils.stats.Stats;

import java.util.*;

public interface IConnexion {
    String ALL_TABLES = "ALL_TABLES";
    boolean initialize();
    boolean close();
    boolean isConnected();
    boolean createTables();
    default void createAccount(String player, String tableName) {
        createAccount(Collections.singleton(player), Collections.singleton(tableName));
    }
    void createAccount(Collection<String> players, Collection<String> tables);
    default void updateData(String player, Stats stats, String tableName) {
        Map<String, Stats> map = new HashMap<>();
        map.put(player, stats);
        updateData(map, Collections.singleton(tableName));
    }
    void updateData(Map<String, Stats> stats, Collection<String> tables);
    default Stats getStats(String player, String tableName) {
        return getStats(Collections.singleton(player), Collections.singleton(tableName)).get(player);
    }
    Map<String, Stats> getStats(Collection<String> players, Collection<String> tableName);
    default boolean hasAccount(String player, String tableName) {
        return hasAccount(Collections.singleton(player), Collections.singleton(tableName));
    }
    boolean hasAccount(Collection<String> player, Collection<String> tableName);
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
