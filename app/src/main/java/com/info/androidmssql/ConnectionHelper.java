package com.info.androidmssql;

import android.annotation.SuppressLint;
import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHelper {

    String IP,DB,DBUsername,DBPassword;

    @SuppressLint("NewApi")
    public Connection connection(){
        IP = "192.168.10.150:1450";
        DB = "SJ";
        DBUsername = "fernando";
        DBPassword = "ThisIsNice01!";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        java.sql.Connection connection = null;
        String ConnectionURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://" + IP + ";databaseName=" + DB + ";user=" + DBUsername+ ";password=" + DBPassword ;
                    connection = DriverManager.getConnection(ConnectionURL);
        } catch (SQLException e){
            e.getMessage();
        } catch (ClassNotFoundException e){
            e.getMessage();
        } catch (Exception ex){
            ex.getMessage();
        } return connection;
    }
}
