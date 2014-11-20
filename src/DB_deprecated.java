
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
public class DB_deprecated {
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
            conn = DriverManager.getConnection(DB_deprecated.URL, DB_deprecated.username, DB_deprecated.password);
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
//            rs.first();
            String andrew_id=rs.getString(2);
            
            String name=rs.getString(3);
            String program=rs.getString(4);
            String gender=rs.getString(5);
            result=new String[]{no,andrew_id,name,program,gender};
            rs.close();
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return result;
    }
    static String[] findEventByLabel(int n){
        String[] result=null;
        try{
            sql="select * from visit where stu_no='"+n+"';";
            rs=stmt.executeQuery(sql);
            rsmd=rs.getMetaData();
            String evtid=rs.getInt(1)+"";
            String evttime=rs.getDate(2)+"";
            String stu_no=n+"";
            String reason=rs.getString(4);
            String remark=rs.getString(5);
            result=new String[]{evtid,evttime,stu_no,reason,remark};
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return result;
    }
    static int calcVisitTimeByLabel(int n){
        int result=0;
        try{
            sql="select count(*) from visit where stu_no='"+n+"';";
            rs=stmt.executeQuery(sql);
            rsmd=rs.getMetaData();
            result=rs.getInt(1);
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return result;
    }
    static int findLastVisitTimeByLabel(int n){
        int result=0;
        try{
            sql="select max(event_time) from visit where stu_no='"+n+"';";
            rs=stmt.executeQuery(sql);
            rsmd=rs.getMetaData();
            result=rs.getInt(1);
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return result;
    }
}
