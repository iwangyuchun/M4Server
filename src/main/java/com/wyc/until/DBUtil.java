package com.wyc.until;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

public class DBUtil {
    public static final String DRIVER="com.mysql.jdbc.Driver";
    public static final String USERNAME="root";
    public static final String PASSWORD="123";
    public static final String URL="jdbc:mysql://localhost:3306/linedata?useUnicode=true&characterEncoding=utf-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    public static DataSource dataSource=null;

    static {
        ComboPooledDataSource pool=new ComboPooledDataSource();
        try {
            pool.setDriverClass(DRIVER);
            pool.setUser(USERNAME);
            pool.setPassword(PASSWORD);
            pool.setJdbcUrl(URL);
            pool.setMaxPoolSize(30);
            pool.setMinPoolSize(5);
            dataSource=pool;
        } catch (PropertyVetoException e) {
            e.printStackTrace();
            System.out.println("数据连接池加载失败");
        }


    }
    //获得Connection对象
    public static Connection getConnection()throws SQLException {
        return dataSource.getConnection();
    }
}
