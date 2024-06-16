package com.makeyang.database.entity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * 数据库连接池对象
 */
public class SimplePoolEntity {
    //真正的数据库连接对象
    private Connection connection;
    //是否可用的标志，默认为true 可用
    private boolean isUsable = true;
    // 最后使用时间
    private long lastUsedTime;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean isUsable() {
        return isUsable;
    }

    public void setUsable(boolean usable) {
        isUsable = usable;
    }

    public long getLastUsedTime() {
        return lastUsedTime;
    }

    public void setLastUsedTime(long lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
    }

    /**
     * 数据库操作方法
     * @param sql  需要执行的sql语句
     * @return  ResultSet
     */
    public ResultSet execSql(String sql){
        this.lastUsedTime = System.currentTimeMillis() + 3600000;
        ResultSet rs = null;
        try {
            Statement statement = connection.createStatement();
            rs = statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }
}
