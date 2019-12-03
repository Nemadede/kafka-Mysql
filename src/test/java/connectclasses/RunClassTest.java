package connectclasses;

import connectclasses.common.ConfigClass;
import connectclasses.runner.RunClass;
import org.junit.Test;

import java.util.Properties;

public class RunClassTest {
    RunClass runClass;
    String host = "http://localhost:8069"; //user's odoo configurations
    String db ="demodb";
    String user = "demodb@demo.com";
    String password = "123456";
    String comName = "NemaLove"; // company name which will be used to name sales table
    Integer idNum = 1;

    String host2 = "http://192.168.1.15:8069"; //user's odoo configurations
    String db2 ="demodb";
    String user2 = "demodb@demo.com";
    String password2 = "123456";
    String comName2 = "GLOXON"; // company name which will be used to name sales table
    Integer idNum2 = 2;
    ConfigClass configClass;

    @Test
    public void classTest1(){
        runClass = new RunClass(host,db,user,password,comName,idNum);
    }

    @Test
    public void classTest2(){
        runClass = new RunClass(host2,db2,user2,password2,comName2,idNum2);
    }

}
