package com.makeyang.database.service.impl;

import com.makeyang.database.entity.SimplePoolEntity;
import com.makeyang.database.service.SimplePoolService;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class SimplePoolServiceImpl implements SimplePoolService {

    private String jdbcDriver; // 数据库驱动
    private String jdbcUrl;  //数据库url
    private String username; //用户名
    private String password; //密码
    private Integer initCount; //初始化数量
    private Integer stepCount;  //步进数量
    private Integer maxCount; //最大数量
    private Integer idleTimeout; // 空闲时间
    private Set<SimplePoolEntity> simplePoolEntitySet = new HashSet<>();

    /**
     * 构造方法初始化 数据库连接池
     */
    public SimplePoolServiceImpl(){
        init();
    }

    /**
     * 数据库连接池初始化
     */
    private void init(){
        //1.读取配置文件
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("dbconfig.properties");
        Properties properties = new Properties();
        try {
            properties.load(in);
            //设置初始化参数
            jdbcDriver = properties.getProperty("jdbc.driver");
            jdbcUrl = properties.getProperty("jdbc.url");
            username = properties.getProperty("jdbc.username");
            password = properties.getProperty("jdbc.password");
            initCount = Integer.parseInt(properties.getProperty("init.count"));
            stepCount = Integer.parseInt( properties.getProperty("step.count"));
            maxCount = Integer.parseInt( properties.getProperty("max.count"));
            idleTimeout = Integer.parseInt( properties.getProperty("idle.timeout"));
            //加载数据库驱动
            Driver driver = (Driver) Class.forName(jdbcDriver).newInstance();
            //获取数据库管理对象
            DriverManager.deregisterDriver(driver);
            //初始化一定数量的连接
            createConnection(initCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createConnection(int connCount) throws Exception {
        //判断数据库连接池是否以达到最大连接数
        if(simplePoolEntitySet.size() + connCount > maxCount){
            throw new RuntimeException("连接池数量已到上限");
        }
        for (int i = 0; i < connCount; i++){
            SimplePoolEntity simplePoolEntity = new SimplePoolEntity();
            simplePoolEntity.setConnection(DriverManager.getConnection(jdbcUrl, username, password));
            simplePoolEntity.setUsable(true);
            simplePoolEntity.setLastUsedTime(System.currentTimeMillis());
            simplePoolEntitySet.add(simplePoolEntity);
        }
    }

    @Override
    public SimplePoolEntity getConnection() {
        SimplePoolEntity simplePoolEntity = null;
        try {
            simplePoolEntity = getRealConnection();
            //为空时创建新的连接
            while (simplePoolEntity == null){
                createConnection(stepCount);
                //创建连接比较耗时，建议在此处让线程延迟一定时间
                Thread.sleep(6000);
                simplePoolEntity = getRealConnection();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return simplePoolEntity;
    }

    private SimplePoolEntity getRealConnection() throws Exception{
        //循环连接池，获取连接
        for(SimplePoolEntity simplePoolEntity : simplePoolEntitySet){
            //判断是否为空闲
            if(simplePoolEntity.isUsable()){
                //判断连接是否有效
                if(!simplePoolEntity.getConnection().isValid(3000)){
                    //无效连接，重新创建连接替换该连接
                    Connection realConnect = DriverManager.getConnection(jdbcUrl,username,password);
                    simplePoolEntity.setConnection(realConnect);
                }
                //设置状态为不可用
                simplePoolEntity.setUsable(false);
                return simplePoolEntity;
            }
        }
        return null;
    }

    /**
     * 检测空闲连接
     */
    public void checkIdleConnections() {
        System.out.println("checkIdleConnections....");
        for (SimplePoolEntity simplePoolEntity : simplePoolEntitySet) {
            if (isConnectionIdle(simplePoolEntity)) {
                // 可以选择关闭连接或者标记为可回收
                closeConnection(simplePoolEntity.getConnection());
            }
        }
    }

    private boolean isConnectionIdle(SimplePoolEntity simplePoolEntity) {
        // 这里需要根据实际情况检查conn的lastUsedTime属性
        // 假设conn有一个getLastUsedTime()方法
        long currentTime = System.currentTimeMillis();
        long lastUsedTime = simplePoolEntity.getLastUsedTime();
        long times = (currentTime - lastUsedTime);
        System.out.println("相差多少秒" + times);
        return times > idleTimeout;
    }

    private void closeConnection(Connection conn) {
        try {
            System.out.println("触发了close...");
            conn.close();
        } catch (Exception e) {
            // 处理异常
            e.printStackTrace();
        }
    }
}
