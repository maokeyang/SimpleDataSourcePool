package com.makeyang.database;

import com.makeyang.database.service.SimplePoolService;
import com.makeyang.database.service.impl.SimplePoolServiceImpl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimplePoolManger {
    //私有化构造，禁止创建
    private SimplePoolManger() {
    }

    //在静态内部类中实例化对象，达到懒汉单例模式
    private static class ClassLoad {
        public static SimplePoolService getSimplePoolService() {
            return new SimplePoolServiceImpl();
        }
    }

    public static SimplePoolService getInstance(){
        return ClassLoad.getSimplePoolService();
    }

    public void handleIdleConnection() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(getInstance()::checkIdleConnections, 0, 1, TimeUnit.SECONDS);
    }
}
