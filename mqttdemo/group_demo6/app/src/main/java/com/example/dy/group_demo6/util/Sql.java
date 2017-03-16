package com.example.dy.group_demo6.util;

import com.example.dy.group_demo6.listview.device;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Sql {

    // JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://115.28.142.203:3306/openfire1";

    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "testuser2";
    static final String PASS = "testuser2";
    String homeid;
    public Sql(String homeid){
        this.homeid = homeid;
    }
    public List<device> getDevices(){
        Connection conn = null;
        Statement stmt = null;
        List<device> list = new ArrayList<device>();
        try{
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // 执行查询
            System.out.println("实例化Statement对...");
            stmt = conn.createStatement();
            String sql = "SELECT * FROM devicedata1 WHERE homeid = \""+homeid+"\"";
            ResultSet rs = stmt.executeQuery(sql);

            // 展开结果集数据库
            while(rs.next()){
                // 通过字段检索
                String name = rs.getString("devicename");
                String id = rs.getString("deviceid");
                String type = rs.getString("devicetype");
                // 输出数据
                System.out.print("名称: " + name);
                device de = new device(id,name,type);
                list.add(de);

                System.out.print("\n");
            }

            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
            return list;
        }catch(SQLException se){
            // 处理 JDBC 错误
            se.printStackTrace();
            return null;
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
            return null;
        }finally{
            // 关闭资源
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }// 什么都不做
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
            return list;
        }
        //System.out.println("Goodbye!");
    }
}

