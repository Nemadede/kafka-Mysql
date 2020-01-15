package connectclasses;

import connectclasses.new_scheme.ContinuosPuller;
import connectclasses.new_scheme.ProducerThread2;
import connectclasses.new_scheme.Puller;
import connectclasses.new_scheme.QueueClass;
import connectclasses.new_scheme.consumer.Consumer2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class InstanceTest {

    String host = "http://localhost:8069"; //user's odoo configurations
    String db ="demodb";
    String user = "demodb@demo.com";
    String password = "123456";
    String comName = "NemaLove"; // company name which will be used to name sales table
    Integer idNum = 1;

    String host1 = "http://192.168.1.20:8069"; //user's odoo configurations
    String db1="demodb";
    String user1 = "demodb@demo.com";
    String password1 = "123456";
    String comName1 = "GLOXON"; // company name which will be used to name sales table
    Integer idNum1 = 2;

    ContinuosPuller continuosPoller;
    List<Puller> pullerList = new ArrayList<>();

    @Test
    public void queueTest() throws Exception {
        QueueClass queueClass = new QueueClass();
        pullerList.add(new ContinuosPuller(user,host,db,password,idNum,comName));
        queueClass.queueUp(pullerList);
    }

    @Test
    public void consumer2Test() throws SQLException{
        Consumer2 consumer2 = new Consumer2();
        consumer2.run();
    }

}
