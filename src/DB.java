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
/**This class contains method for database operation. Including writing and reading information from database.
 * @since 2014/11/16
 * @author Eason Lu
 */
public class DB {

static final String URL = "jdbc:mysql://opencvdb.cxsp5jskrofy.us-west-2.rds.amazonaws.com:3306/opencv";// AWS-RDS
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
            sql="select gender,reason, count(*) as frequency from student,visit where student.stu_no=visit.stu_no and event_time between ? and ?  group by gender,reason;";
            pstmt=conn.prepareStatement(sql);
            pstmt.setString(1, date1);
            pstmt.setString(2, date2);
            rs = pstmt.executeQuery();
            rs.last();//to get the number of the row
//            int rowCount = rs.getRow();
//          int columnCount = rsmd.getColumnCount();//get the column number
            rs.first();//move back to the first row
            Vector<Vector> resultArray = new Vector<Vector>();//to storage the data
            do {//to get the student id, student name, sex and email
                String gender = rs.getString("gender");
                String reason = rs.getString("reason");
                int frequency = rs.getInt("frequency");
                Vector row = new Vector();
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
    public static void addVisit(String visitdate,int no,String visitreason,String remark){
        DB.sql="insert into visit(event_time,stu_no,reason,remark) values('"+visitdate+"','"+no+"','"+visitreason+"','"+remark+"');";
        System.out.println(DB.sql);
        try{
            DB.stmt.executeUpdate(DB.sql);
            System.out.println("Insert success!");
        }
        catch(SQLException e){
            System.out.println(e);
        }
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
        System.out.println("Student added!");
    }
    static String[] findNameByLabel(int label){
        String[] name=new String[2];
        try{
            stmt = DBconnect().createStatement();//connect database
            sql="select stu_name,andrew_id from student where stu_no='"+label+"';";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            rs.first();
            
            name[0]=rs.getString(1);
            name[1]=rs.getString(2);
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return name;
    }
    /**
     * This method is for reading student information from database.
     * @param stid studentid (label of student in photo folder)
     * @return 
     */
    public static ArrayList selectInformation(int stid) {   
        ArrayList informationList = new ArrayList();
        try {
            stmt = DBconnect().createStatement();//connect database
            pstmt = conn.prepareStatement("SELECT * FROM student where stu_no=?");
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
            informationList.add(gender);//basic information
            pstmt = conn.prepareStatement("select event_time from visit where stu_no=? ORDER BY event_time;");//by times of event happening
            pstmt.setInt(1, stid);
            rs = pstmt.executeQuery();
            rs.last();
            Date lastTime = rs.getDate("event_time");
            informationList.add(lastTime);//lastvisit
            pstmt = conn.prepareStatement("select count(*) as frequency from visit where stu_no=?;");//根据label或者说学号查询学生的信息
            pstmt.setInt(1, stid);
            rs = pstmt.executeQuery();
            rs.first();
            int frequency = rs.getInt("frequency");
            informationList.add(frequency);//times of visi
            pstmt = conn.prepareStatement("select * from visit where stu_no=? ORDER BY event_time;");//根据label或者说学号查询学生的信息
            pstmt.setInt(1, stid);
            rs = pstmt.executeQuery();
            rs.last();
            String reason = rs.getString("reason");
            String ann = rs.getString("remark"); //remark is announcement for student
            informationList.add(reason);
            informationList.add(ann);
        } catch (Exception e) {
            System.out.printf(e.getMessage());
        } finally {
        }
        return informationList;
    }

}
