package cn.com.vans.db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
/**
 * JdbcUtils class
 *
 * @author vans
 * @date 2019/09/23
 */
public class JdbcUtils {
    public static String driver;
    public static String url;
    public static String user;
    public static String password;
    static{

            Properties pro=AppConfig.getProperties("jdbc.properties");
            driver = pro.getProperty("driver");
            url = pro.getProperty("url");
            user = pro.getProperty("user");
            password = pro.getProperty("password");
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /***获取Connection对象*/
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url,user,password);
    }

    /**关闭资源*/
    public static void close(PreparedStatement pstat, Connection conn){
        if(pstat!=null){
            try {
                pstat.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(conn!=null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(ResultSet rs, PreparedStatement pstat, Connection conn){
        if(rs!=null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        close(pstat,conn);
    }

}