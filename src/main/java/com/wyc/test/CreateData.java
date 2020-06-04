package com.wyc.test;

import com.wyc.until.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class CreateData {
    public static void main(String[] args) {
        Connection connection=null;
        try {
             connection= DBUtil.getConnection();
            String sql="insert into data values(?,?)";
            for(int i=1;i<=1000000;i++){
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1,i);
                preparedStatement.setInt(2, new Random().nextInt(1000));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}
