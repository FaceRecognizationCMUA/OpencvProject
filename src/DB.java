import java.sql.Connection;
import java.sql.*;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Eason Lu
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

    public static Connection DBconnect() {

        try {
//            Class.forName("com.mysql.jdbc.Driver");//get the driver
//            conn = DriverManager.getConnection(URL, username, password);//connect
            conn = DriverManager.getConnection(URL, username, password);
            stmt=conn.createStatement();
            System.out.println("Connected!");
        } catch (Exception e) {
            System.out.printf(e.getMessage());
        } 
        return conn;//return the connection to use
    }

    public static Vector<Vector> selectStudentReason(String date1, String date2) {
        try {
            Statement stmt = DBconnect().createStatement();//connect database
            pstmt = conn.prepareStatement("SELECT andrew_id, student.stu_name,visit.reason,visit.remark,event_time FROM student,visit where student.stu_no=visit.stu_no and event_time between ? and ? order by reason");//execute the sql sentence
            pstmt.setString(1, date1);
            pstmt.setString(2, date2);
            rs = pstmt.executeQuery();
            rs.last();//to get the number of the row
            int rowCount = rs.getRow();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();//get the column number
            rs.first();//move back to the first row

            Vector<Vector> resultArray = new Vector<Vector>();//to storage the data
            do {//to get the student id, student name, sex and email
                String stid = rs.getString("andrew_id");
                String name = rs.getString("stu_name");
                String reason = rs.getString("reason");
                String remark = rs.getString("remark");
                Date date = rs.getDate("event_time");
                Vector row = new Vector();
                row.add(stid);//storage into vector
                row.add(name);
                row.add(reason);
                row.add(remark);
                row.add(date);
                resultArray.add(row);
            } while (rs.next());
            return resultArray;
        } catch (Exception e) {
            System.out.print(e.getMessage());
        } finally {
        }
        return null;
    }

    public static Vector<Vector> selectFrequency(String date1, String date2) {
        try {
            Statement stmt = DBconnect().createStatement();//connect database
            pstmt = conn.prepareStatement("select event_time, gender,reason, count(*) as frequency from student,visit where student.stu_no=visit.stu_no and event_time between ? and ? group by reason,gender");
            pstmt.setString(1, date1);
            pstmt.setString(2, date2);
            rs = pstmt.executeQuery();
            rs.last();//to get the number of the row
            int rowCount = rs.getRow();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();//get the column number
            rs.first();//move back to the first row
            Vector<Vector> resultArray = new Vector<Vector>();//to storage the data
            do {//to get the student id, student name, sex and email
                Date date = rs.getDate("event_time");
                String gender = rs.getString("gender");
                String reason = rs.getString("reason");
                int frequency = rs.getInt("frequency");
                Vector row = new Vector();
                row.add(date);
                row.add(gender);//storage into vector
                row.add(reason);
                row.add(frequency);
                resultArray.add(row);
            } while (rs.next());
            return resultArray;
        } catch (Exception e) {
            System.out.print(e.getMessage());
        } finally {
        }
        return null;
    }

    public static void addStudent(int stid, String andrewId, String studentName, String program, String gender) throws Exception {

        Statement stmt = DBconnect().createStatement();//connect database

        pstmt = conn.prepareStatement("INSERT into student (stu_no, andrew_id, stu_name,program,gender)  VALUES (?,?,?,?,?)");
        pstmt.setInt(1, stid);
        pstmt.setString(2, andrewId);
        pstmt.setString(3, studentName);
        pstmt.setString(4, program);
        pstmt.setString(5, gender);

        int temp = pstmt.executeUpdate();
    }

    public static ArrayList selectInformation(int stid) {//返回第三问需要的所有信息
        ArrayList informationList = new ArrayList();
        try {
            stmt = DBconnect().createStatement();//connect database
            pstmt = conn.prepareStatement("SELECT * FROM student where stu_no=?");//根据label或者说学号查询学生的信息
            pstmt.setInt(1, stid);
            rs = pstmt.executeQuery();
            rsmd=rs.getMetaData();
//            sql="SELECT * FROM student where stu_no="+stid+";";
//            rs=stmt.executeQuery(sql);
            rs.first();
            System.out.println(rs.toString());
            int id = rs.getInt("stu_no");
            String aid = rs.getString("andrew_id");
            String name = rs.getString("stu_name");
            String program = rs.getString("program");
            String gender = rs.getString("gender");
            informationList.add(id);
            informationList.add(aid);
            informationList.add(name);
            informationList.add(program);
            informationList.add(gender);//基本信息
            pstmt = conn.prepareStatement("select event_time from visit where stu_no=? ORDER BY event_time;");//根据label或者说学号查询学生的信息
            pstmt.setInt(1, stid);
            rs = pstmt.executeQuery();
            rs.last();
            Date lastTime = rs.getDate("event_time");
            informationList.add(lastTime);//最后访问
            pstmt = conn.prepareStatement("select count(*) as frequency from visit where stu_no=?;");//根据label或者说学号查询学生的信息
            pstmt.setInt(1, stid);
            rs = pstmt.executeQuery();
            rs.first();
            int frequency = rs.getInt("frequency");
            informationList.add(frequency);//访问次数
            pstmt = conn.prepareStatement("select * from visit where stu_no=? ORDER BY event_time;");//根据label或者说学号查询学生的信息
            pstmt.setInt(1, stid);
            rs = pstmt.executeQuery();
            rs.last();
            String reason = rs.getString("reason");
            String ann = rs.getString("remark");
            informationList.add(reason);//最后访问那一次的原因和remark
            informationList.add(ann);
        } catch (Exception e) {
            System.out.printf(e.getMessage());
        } finally {
        }
        return informationList;
    }
     static String[] findStudentByLabel(int n){
        String[] result=new String[5];
        try{
            sql="select * from student where stu_no='"+n+"';";
            
            pstmt = conn.prepareStatement(sql);
            rs=pstmt.executeQuery(sql);
            System.out.println(sql);
//            DB0.rsmd=rs.getMetaData();
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
        int result;
        try{
            sql="select max(event_time) from visit where stu_no='"+n+"';";
            rs=stmt.executeQuery(sql);
            rsmd=rs.getMetaData();
            result=rs.getDate(1);
        }
        catch(SQLException e){
            System.out.println(e);
        }
        return result;
    }
}
