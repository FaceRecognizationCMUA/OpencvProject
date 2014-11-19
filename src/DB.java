
import java.sql.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sky Xu <Sky Xu at Carnegie Mellon University>
 */
public class DB {
    static final String URL = "jdbc:mysql://opencvdb.cxsp5jskrofy.us-west-2.rds.amazonaws.com:3306/opencv";
    static String sql="";
    static Connection conn = null;
    static Statement stmt=null;
    static PreparedStatement pstmt = null;
    static ResultSet rs = null;
    static ResultSetMetaData rsmd=null;
    static String username = "admin";
    static String password = "cmua2014";
    
    static void connectDB(){
        try {
            conn = DriverManager.getConnection(DB.URL, DB.username, DB.password);
            stmt=conn.createStatement();
            System.out.println("Connected!");
        }
        catch(SQLException se){
            System.out.println(se);
        }
    }
    static String[] findStudentByLabel(int n){
        String[] result=null;
        try{
            sql="select * from student where stu_no='"+n+"';";
            rs=stmt.executeQuery(sql);
            rsmd=rs.getMetaData();
            String no=n+"";
            String andrew_id=rs.getString(2);
            String name=rs.getString(3);
            String program=rs.getString(4);
            String gender=rs.getString(5);
            result=new String[]{no,andrew_id,name,program,gender};
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return result;
    }
}
