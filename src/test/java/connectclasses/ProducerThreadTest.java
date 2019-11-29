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

public class ProducerThreadTest {

//    String configFile = "../src/main/resources/Producer.properties";
    Properties properties = new Properties();

    String host = "http://localhost:8069"; //user's odoo configurations
    String db ="demodb";
    String user = "demodb@demo.com";
    String password = "123456";
    Integer count = 1; // assuming that each user will have an Id which will define the partition which the user's data will be on
    String comName = "NemaLove"; // company name which will be used to name sales table

    String host2 = "http://192.168.1.15:8069"; //user's odoo configurations
    String db2 ="demodb";
    String user2 = "demodb@demo.com";
    String password2 = "123456";
    Integer count2 = 2; // assuming that each user will have an Id which will define the partition which the user's data will be on
    String comName2 = "GLOXON"; // company name which will be used to name sales table
    ConfigClass configClass;

    @Test
    public void testClass() {
        properties.put("bootstrap.servers","127.0.0.1:9092");
        properties.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.put("topic","odooGroup");
        System.out.println(properties);
        properties.put("number.of.producer","3");
//        producerThread.run();

        ProducerGroup producerGroup = new ProducerGroup(properties,configClass);
        List<Producer> producers = new ArrayList<>();
//        producers.add(new ProducerThread(properties,user,host,db,password,count));


        for(int i = 0; i < Integer.parseInt(properties.getProperty("number.of.producer")); i++){
            producers.add(new ProducerThread(properties,user,host,db,password,count,comName));
            producers.add(new ProducerThread(properties, user2, host2, db2, password2, count2,comName2));
        }
        producerGroup.assignListProducer(producers);
        producerGroup.run();
    }

//    @Test
//    public void testClass2(){
//        properties.put("bootstrap.servers","127.0.0.1:9092");
//        properties.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
//        properties.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
//        properties.put("topic","odooGroup");
//        properties.put("number.of.producer","3");
//        ProducerGroup producerGroup = new ProducerGroup(properties,configClass);
//        List<Producer> producers = new ArrayList<>();
//        for(int i = 0; i < Integer.parseInt(properties.getProperty("number.of.producer")); i++) {
//            producers.add(new ProducerThread(properties, user2, host2, db2, password2, count2,comName2));
//        }
//        producerGroup.assignListProducer(producers);
//        producerGroup.run();
//    }
}
