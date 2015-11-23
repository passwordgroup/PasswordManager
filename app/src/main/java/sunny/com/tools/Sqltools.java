package sunny.com.tools;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import sunny.com.data.Const;
import sunny.com.passwordmanager.Register;

/**
 * Created by sunnys on 2015/11/13.
 */
public class Sqltools {
    private Connection conn;
    private String url = Const.Aliurl;
    private String name = Const.AliUsername;
    private String password = Const.AliPassword;
    private PreparedStatement insertimage;//插入用户图片的statement
    private Statement createtable;//初始化时，创建新表的statement
    private ResultSet set;
    private String username = Register.nickname;//用户自己设定的用户名，这里用作晕数据库中的表名
    private String imagebase64;//图像的base64编码
    private String insertsql = "INSERT INTO userimage(useranme,base64) values(?,?);";
    private String createsql = "CREATE TABLE ?(username VARCHAR(20) PRIMARY KEY ,keki TEXT ,service VARCHAR(20));";
    public Sqltools(String imagebase64){
        this.imagebase64 = imagebase64;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection(url, name, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean insert(int Sno,String Sname,String Sex){
        return true;
    }
    public ResultSet InitSql(){
        try {
            insertimage = conn.prepareStatement(insertsql);
            createtable = conn.createStatement();
            insertimage.setString(1, username);
            insertimage.setCharacterStream(2, new StringReader(imagebase64), imagebase64.length());
//            insertimage.setString(2, imagebase64);
            insertimage.execute();
            createtable.execute("CREATE TABLE "+username+"(username VARCHAR(20) PRIMARY KEY ,keki TEXT ,service VARCHAR(20));");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void clear(){
        if(set!=null){
            try {
                set.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(insertimage !=null){
            try {
                insertimage.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(createtable!=null){
            try {
                createtable.close();
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
}
