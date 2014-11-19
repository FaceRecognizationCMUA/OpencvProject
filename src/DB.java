
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
    static PreparedStatement pstmt = null;
    static ResultSet rs = null;
    static String username = "admin";
    static String password = "cmua2014";
}
