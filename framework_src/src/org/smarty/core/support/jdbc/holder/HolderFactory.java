package org.smarty.core.support.jdbc.holder;

import org.smarty.core.logger.RuntimeLogger;
import org.smarty.core.support.jdbc.support.DBType;

/**
 * 工具箱工厂
 * Created Date 2015/04/09
 *
 * @author quliang
 * @version 1.0
 */
public class HolderFactory {
    private static RuntimeLogger logger = new RuntimeLogger(HolderFactory.class);


    /**
     * 根据类型初始化SQL工具
     *
     * @param sql     SQL
     * @param sqlType 类型
     * @return SQL工具
     */
    public static SQLHolder getHolder(String sql, DBType sqlType) {
        SQLHolder holder = chooseHolder(sqlType);
        if (holder != null) {
            holder.setSql(sql);
        }
        return holder;
    }

    /**
     * 根据类型初始化SQL工具
     *
     * @param sqlType 类型
     * @return SQL工具
     */
    private static SQLHolder chooseHolder(DBType sqlType) {
        switch (sqlType) {
            case MySQL:
                return new MySQLHolder();
            case Oracle:
                return new OracleHolder();
            case DB2:
                return new DB2Holder();
            case Informix:
                return new InformixHolder();
            case Sybase:
                return new SybaseHolder();
            case MSSQL:
                return new MSSQLHolder();
            case PostgreSQL:
                return new PostgreHolder();
            case Access:
                return new AccessHolder();
            case SQLite:
                return new SQLiteHolder();
            default:
                logger.out("Not found the available SQL type");
                return null;
        }
    }
}
