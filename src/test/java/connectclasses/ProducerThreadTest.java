package connectclasses;

import connectclasses.common.ConfigClass;
import connectclasses.producerPackage.Producer;
import connectclasses.producerPackage.ProducerGroup;
import connectclasses.producerPackage.ProducerThread;
import org.junit.Test;
//import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class ProducerThreadTest {

//    String configFile = "../src/main/resources/Producer.properties";
    Properties properties = new Properties();

    String host = "http://localhost:8069"; //user's odoo configurations
    String db ="demodb";
    String user = "demodb@demo.com";
    String password = "123456";
    Integer userId1 = 1; // assuming that each user will have an Id which will define the partition which the user's data will be on
    String comName = "NemaLove"; // company name which will be used to name sales table

    String host2 = "http://192.168.1.15:8069"; //user's odoo configurations
    String db2 ="demodb";
    String user2 = "demodb@demo.com";
    String password2 = "123456";
    Integer userId2 = 2; // assuming that each user will have an Id which will define the partition which the user's data will be on
    String comName2 = "GLOXON"; // company name which will be used to name sales table
    ConfigClass configClass;


    @Test
    public void testClass() {
        properties.put("bootstrap.servers","127.0.0.1:9092");
        properties.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.put("topic","odooGroup");
//        properties.put("transactional.id",userId1.toString());
        System.out.println(properties);



        ProducerGroup producerGroup = new ProducerGroup(properties,configClass);
        List<Producer> producers = new ArrayList<>();
        int cores = Runtime.getRuntime().availableProcessors();
            System.out.println(cores);
        for(int i = 0; i < cores; i++){
            producers.add(new ProducerThread(properties,user,host,db,password, userId1,comName));
//            producers.add(new ProducerThread(properties, user2, host2, db2, password2, userId2,comName2));
        }
        producerGroup.assignListProducer(producers);
        producerGroup.run();
    }



}
