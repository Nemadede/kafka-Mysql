package connectclasses;

import connectclasses.common.ConfigClass;
import connectclasses.runner.RunClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Properties;

public class RunClassTest {
    RunClass runClass;
    String host = "http://localhost:8069"; //user's odoo configurations
    String db ="demodb";
    String user = "demodb@demo.com";
    String password = "123456";
    String comName = "NemaLove"; // company name which will be used to name sales table
    Integer idNum = 1;


    String host1 = "http://192.168.1.15:8069"; //user's odoo configurations
    String db1="demodb";
    String user1 = "demodb@demo.com";
    String password1 = "123456";
    String comName1 = "GLOXON"; // company name which will be used to name sales table
    Integer idNum1 = 2;

    String host2 = "http://localhost:8069"; //user's odoo configurations
    String db2="demodb";
    String user2 = "demodb@demo.com";
    String password2 = "123456";
    String comName2 = "PeaceTreat"; // company name which will be used to name sales table
    Integer idNum2 = 1;

    String host3 = "http://192.168.1.15:8069"; //user's odoo configurations
    String db3="demodb";
    String user3 = "demodb@demo.com";
    String password3 = "123456";
    String comName3 = "PeaceTreaters"; // company name which will be used to name sales table
    Integer idNum3 = 2;

    @Test
    public void classTest1() throws SQLException {
        runClass = new RunClass(host,db,user,password,comName,idNum);
    }

    @Test
    public void classTest2() throws SQLException {
        runClass = new RunClass(host1,db1,user1,password1,comName1,idNum1);
    }

    @Test
    public void classTest3() throws SQLException {
        runClass = new RunClass(host2,db2,user2,password2,comName2,idNum2);
    }

    @Test
    public void classTest4() throws SQLException {
        runClass = new RunClass(host3,db3,user3,password3,comName3,idNum3);
    }

}
