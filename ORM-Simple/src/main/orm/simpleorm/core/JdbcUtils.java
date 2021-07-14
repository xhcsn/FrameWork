package simpleorm.core;

import java.sql.*;

/**
 * @Description: 公用常量
 * @Date: 2021/7/14 14:02
 * @Pacakge: simpleorm.core
 * @ClassName: JdbcUtils
 * @Version: v1.0.0
 * @Author: ccc
 */
public class JdbcUtils {

    //获取连接
    public static Connection getConn(){
        Connection conn =null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql:///dbpipe", "root", "admin");
        } catch (Exception e){
            System.out.println("获取连接对象失败");
            e.printStackTrace();
        }
        return conn;
    }

    //更新操作
    public static int excuteUpdate(String sql, Object[] params){
        Connection connection = null;
        PreparedStatement pstmt = null;
        int result = -1;
        try{
            connection = getConn();
            pstmt = connection.prepareStatement(sql);
            for(int i = 0; i < params.length; i++){
                pstmt.setObject(i + 1, params[i]);
            }
            result = pstmt.executeUpdate();
        }catch (SQLException e){
            System.out.println("更新数据出现异常");
            System.out.println(e.getMessage());
        }finally {
            //释放连接
            release(pstmt, connection);
        }
        return result;
    }

    public static void release(Statement stmt, Connection conn){
        if(conn != null){
            try {
                stmt.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            stmt = null;
        }
        if(conn != null){
            try{
                conn.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            conn = null;
        }
    }
}
