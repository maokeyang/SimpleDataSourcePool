package com.makeyang.database;

import com.makeyang.database.entity.SimplePoolEntity;
import com.makeyang.database.service.SimplePoolService;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws SQLException, IOException {
        SimplePoolService instance = SimplePoolManger.getInstance();
        SimplePoolEntity connection = instance.getConnection();

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(instance::checkIdleConnections, 0, 1, TimeUnit.SECONDS); // 每分钟检查一次

        ResultSet rs = connection.execSql("select * from user");

        while (rs.next()) {
            System.out.println(String.format("%s | %s | %s |", rs.getString(1), rs.getString(2), rs.getString(3)));
        }

        System.in.read();
    }
}
