package com.makeyang.database.service;

import com.makeyang.database.entity.SimplePoolEntity;


/**
 * 数据库连接池操作接口
 */
public interface SimplePoolService {
    /**
     * 连接池创建连接接口
     * @param connCount  需要创建的连接数量
     */
    void createConnection(int connCount) throws Exception;

    /**
     * 获取数据库连接
     * @return
     * @throws Exception
     */
    SimplePoolEntity getConnection();

    /**
     * 获取数据库连接
     * @return
     * @throws Exception
     */
    void checkIdleConnections();
}
