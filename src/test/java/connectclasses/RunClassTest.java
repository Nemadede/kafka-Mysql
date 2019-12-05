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
    String password = "password";
    String comName = "NemaLove"; // company name which will be used to name sales table
    Integer idNum = 1;

    @Test
    public void classTest1(){
        runClass = new RunClass(host,db,user,password,comName,idNum);
    }



}
