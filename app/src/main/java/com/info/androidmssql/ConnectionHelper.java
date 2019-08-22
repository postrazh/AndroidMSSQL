package com.info.androidmssql;

import android.annotation.SuppressLint;
import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHelper {

    // NOX
//    private final String IP = "192.168.1.237:1450";

    // wifi
//    private final String IP = "192.168.2.126:1450";

    // production IP
    private final String IP = "192.168.10.150:1450";

    private final String DB = "SJ";
    private final String DBUsername = "fernando";
    private final String DBPassword = "ThisIsNice01!";

    public Connection connection(){
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
