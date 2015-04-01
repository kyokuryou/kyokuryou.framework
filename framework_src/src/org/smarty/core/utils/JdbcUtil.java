package org.smarty.core.utils;

import org.smarty.core.support.jdbc.mapper.RowMapperHandler;
import org.smarty.core.logger.RuntimeLogger;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.CallableStatementCreatorFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC工具
 */
public abstract class JdbcUtil {
    private static RuntimeLogger logger = new RuntimeLogger(JdbcUtil.class);

    /**
     * 创建preparedStatement
     *
     * @param sql             sql
     * @param parameterSource parameterSource
     *                        MapSqlParameterSource (org.springframework.jdbc.core.namedparam)
     *                        BeanPropertySqlParameterSource (org.springframework.jdbc.core.namedparam)
     * @return PreparedStatement
     * @throws java.sql.SQLException SQLException
     */
    public static PreparedStatementCreator getStatementCreator(String sql, SqlParameterSource parameterSource) throws SQLException {
        ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);
        List<SqlParameter> declaredParams = NamedParameterUtils.buildSqlParameterList(parsedSql, parameterSource);

        String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, parameterSource);
        Object[] params = NamedParameterUtils.buildValueArray(parsedSql, parameterSource, declaredParams);

        PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(sqlToUse, declaredParams);
        return pscf.newPreparedStatementCreator(params);
    }

    /**
     * 创建callableStatement
     *
     * @param sql             sql
     * @param parameterSource parameterSource
     *                        MapSqlParameterSource (org.springframework.jdbc.core.namedparam)
     *                        BeanPropertySqlParameterSource (org.springframework.jdbc.core.namedparam)
     * @return callableStatement
     * @throws java.sql.SQLException SQLException
     */
    public static CallableStatementCreator getCallableStatementCreator(String sql, SqlParameterSource parameterSource) throws SQLException {
        ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);
        String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, parameterSource);

        CallableStatementCreatorFactory cscf = new CallableStatementCreatorFactory(sqlToUse);
        Map<String, Object> params = buildValueMap(parameterSource);
        return cscf.newCallableStatementCreator(params);
    }

    public static Map<String, Object> buildValueMap(SqlParameterSource parameterSource) {
        Map<String, Object> params = new HashMap<String, Object>(0);
        if (parameterSource instanceof MapSqlParameterSource) {
            params = ((MapSqlParameterSource) parameterSource).getValues();
        } else if (parameterSource instanceof BeanPropertySqlParameterSource) {
            BeanPropertySqlParameterSource bpss = (BeanPropertySqlParameterSource) parameterSource;
            String[] propertyNames = bpss.getReadablePropertyNames();
            for (String pn : propertyNames) {
                params.put(pn, bpss.getValue(pn));
            }
        }
        return params;
    }

    /**
     * 解析多行ResultSet
     *
     * @param rs  ResultSet
     * @param rmh 映射工具
     * @param <T> 类型
     * @return T 列表
     * @throws java.sql.SQLException
     */
    public static <T> List<T> processMultipleRow(ResultSet rs, RowMapperHandler<T> rmh) throws SQLException {
        List<T> list = new ArrayList<T>(0);
        while (rs.next()) {
            list.add(rmh.rowMapper(rs));
        }
        return list;
    }

    /**
     * 解析单行ResultSet
     *
     * @param rs  ResultSet
     * @param rmh 映射工具
     * @param <T> 类型
     * @return T 列表
     * @throws java.sql.SQLException
     */
    public static <T> T processSingleRow(ResultSet rs, RowMapperHandler<T> rmh) throws SQLException {

        if (rs.next()) {
            return rmh.rowMapper(rs);
        }
        return null;
    }

    /**
     * 关闭Connection
     *
     * @param con Connection
     */
    public static void closeConnection(Connection con) {
        if (con == null) {
            return;
        }

        try {
            con.close();
        } catch (SQLException ex) {
            logger.out(ex);
        }
    }

    /**
     * 关闭Statement
     *
     * @param stmt Statement
     */
    public static void closeStatement(Statement stmt) {
        if (stmt == null) {
            return;
        }
        try {
            stmt.clearBatch();
            stmt.clearWarnings();
            stmt.close();
        } catch (SQLException ex) {
            logger.out(ex);
        }
    }

    /**
     * 关闭ResultSet
     *
     * @param rs ResultSet
     */
    public static void closeResultSet(ResultSet rs) {
        if (rs == null) {
            return;
        }

        try {
            rs.clearWarnings();
            rs.close();
        } catch (SQLException ex) {
            logger.out(ex);
        }
    }

    public static String getResultSetColumnName(ResultSetMetaData rsmd, int index) throws SQLException {
        String lab = rsmd.getColumnLabel(index);
        return LogicUtil.isNotEmpty(lab) ? lab : rsmd.getColumnName(index);
    }

    /**
     * 从ResultSet获取适合requiredType的值
     *
     * @param rs           当前游标的ResultSet
     * @param index        列索引
     * @param requiredType 类型
     * @return 值
     * @throws java.sql.SQLException
     */
    public static Object getResultSetValue(ResultSet rs, int index, Class requiredType) throws SQLException {
        if (requiredType == null) {
            return getResultSetValue(rs, index);
        }

        Object value = null;
        boolean wasNullCheck = false;

        if (String.class.equals(requiredType)) {
            value = rs.getString(index);
        } else if (boolean.class.equals(requiredType) || Boolean.class.equals(requiredType)) {
            value = rs.getBoolean(index);
            wasNullCheck = true;
        } else if (byte.class.equals(requiredType) || Byte.class.equals(requiredType)) {
            value = rs.getByte(index);
            wasNullCheck = true;
        } else if (short.class.equals(requiredType) || Short.class.equals(requiredType)) {
            value = rs.getShort(index);
            wasNullCheck = true;
        } else if (int.class.equals(requiredType) || Integer.class.equals(requiredType)) {
            value = rs.getInt(index);
            wasNullCheck = true;
        } else if (long.class.equals(requiredType) || Long.class.equals(requiredType)) {
            value = rs.getLong(index);
            wasNullCheck = true;
        } else if (float.class.equals(requiredType) || Float.class.equals(requiredType)) {
            value = rs.getFloat(index);
            wasNullCheck = true;
        } else if (double.class.equals(requiredType) || Double.class.equals(requiredType) ||
                Number.class.equals(requiredType)) {
            value = rs.getDouble(index);
            wasNullCheck = true;
        } else if (byte[].class.equals(requiredType)) {
            value = rs.getBytes(index);
        } else if (Date.class.equals(requiredType)) {
            value = rs.getDate(index);
        } else if (Time.class.equals(requiredType)) {
            value = rs.getTime(index);
        } else if (Timestamp.class.equals(requiredType) || java.util.Date.class.equals(requiredType)) {
            value = rs.getTimestamp(index);
        } else if (BigDecimal.class.equals(requiredType)) {
            value = rs.getBigDecimal(index);
        } else if (Blob.class.equals(requiredType)) {
            value = rs.getBlob(index);
        } else if (Clob.class.equals(requiredType)) {
            value = rs.getClob(index);
        } else {
            value = getResultSetValue(rs, index);
        }
        if (wasNullCheck && value != null && rs.wasNull()) {
            value = null;
        }
        return value;
    }

    /**
     * 获取ResultSet真实的类型
     * java.sql.Blob
     * java.sql.Clob
     * java.sql.Timestamp
     *
     * @param rs    ResultSet
     * @param index 当前ResultSet游标
     * @return 值
     * @throws java.sql.SQLException
     */
    public static Object getResultSetValue(ResultSet rs, int index) throws SQLException {
        Object obj = rs.getObject(index);
        String className = null;
        if (obj != null) {
            className = obj.getClass().getName();
        }
        if (obj instanceof Blob) {
            obj = rs.getBytes(index);
        } else if (obj instanceof Clob) {
            obj = rs.getString(index);
        } else if (className != null &&
                ("oracle.sql.TIMESTAMP".equals(className) ||
                        "oracle.sql.TIMESTAMPTZ".equals(className))) {
            obj = rs.getTimestamp(index);
        } else if (className != null && className.startsWith("oracle.sql.DATE")) {
            String metaDataClassName = rs.getMetaData().getColumnClassName(index);
            if ("java.sql.Timestamp".equals(metaDataClassName) ||
                    "oracle.sql.TIMESTAMP".equals(metaDataClassName)) {
                obj = rs.getTimestamp(index);
            } else {
                obj = rs.getDate(index);
            }
        } else if (obj != null && obj instanceof Date) {
            if ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(index))) {
                obj = rs.getTimestamp(index);
            }
        }
        return obj;
    }

    /**
     * 检查驱动是否支持批处理
     *
     * @param con Connection
     * @return boolean
     */
    public static boolean supportsBatchUpdates(Connection con) {
        try {
            DatabaseMetaData dbmd = con.getMetaData();
            if (dbmd != null) {
                if (dbmd.supportsBatchUpdates()) {
                    return true;
                }
            }
        } catch (SQLException ex) {
            logger.out(ex);
        } catch (AbstractMethodError err) {
            logger.out(err);
        }
        return false;
    }

    /**
     * 检查是否是数值类型
     *
     * @param sqlType sqlType
     * @return boolean
     */
    public static boolean isNumeric(int sqlType) {
        return Types.BIT == sqlType || Types.BIGINT == sqlType || Types.DECIMAL == sqlType ||
                Types.DOUBLE == sqlType || Types.FLOAT == sqlType || Types.INTEGER == sqlType ||
                Types.NUMERIC == sqlType || Types.REAL == sqlType || Types.SMALLINT == sqlType ||
                Types.TINYINT == sqlType;
    }
}