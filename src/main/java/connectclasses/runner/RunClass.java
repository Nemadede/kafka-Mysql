package connectclasses.runner;

import connectclasses.apiClients.MySQLdb;
import connectclasses.common.ConfigClass;
import connectclasses.consumerPackage.Consumer;
import connectclasses.consumerPackage.ConsumerGroup;
import connectclasses.consumerPackage.ConsumerThread;
import connectclasses.producerPackage.Producer;
import connectclasses.producerPackage.ProducerGroup;
import connectclasses.producerPackage.ProducerThread;

import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedTask;
import javax.enterprise.concurrent.ManagedTaskListener;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class RunClass implements Runnable {
    ConfigClass config;

    //user's odoo configurations
    String host;
    String db;
    String user;
    String password ;
    String comName ;
    Integer idNum;

    public RunClass(String host, String db, String user, String password, String comName,Integer idNum) {
        this.host = host;
        this.db = db;
        this.user = user;
        this.password = password;
        this.comName = comName;
        this.idNum= idNum;
        run();
    }
    public RunClass(){}

    private Properties pProperties(){
    Properties properties = new Properties();
    properties.put("bootstrap.servers","127.0.0.1:9092");
    properties.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
    properties.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
    properties.put("topic","odooGroup");
    System.out.println(properties);

    return properties;
}

    private Properties cProperties(){
    Properties properties = new Properties();
    properties.put("bootstrap.servers","127.0.0.1:9092");
    properties.put("group.id", "group1");
    properties.put("topic","odooGroup");
    properties.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
    properties.put("value.deserializer","org.springframework.kafka.support.serializer.JsonDeserializer");
    properties.put("enable.auto.commit","false");
    properties.put("min.batch.size","1");
    return properties;
}

// Variables
    private Properties consumerProps = cProperties();
    private Properties producerProps = pProperties();

    String databaseConfig = "/home/nema-love/ideaprojects/zip files/groupwork/src/main/resources/DB.properties";
    MySQLdb database = new MySQLdb(databaseConfig);

    ConsumerGroup consumerGroup = new ConsumerGroup(consumerProps,database);
    ProducerGroup producerGroup = new ProducerGroup(producerProps,config);

    List<Producer> producers = new ArrayList<>();
    List<Consumer> consumers = new ArrayList<>();

    int cores = Runtime.getRuntime().availableProcessors();




 //Method run
    @Override
    public void run() {
        for(int i = 0; i < cores; i++){

            producers.add(new ProducerThread(producerProps,user,host,db,password,this.idNum,comName));
            System.out.println(this.idNum);
            consumers.add(new ConsumerThread(consumerProps,database,this.idNum));

        }

        consumerGroup.assignListConsumers(consumers);
        producerGroup.assignListProducer(producers);

//        Thread t1 = new Thread(consumerGroup.run());

            CompletableFuture<Void> future = new CompletableFuture().runAsync(() ->{
                System.out.println("just before the consumer");
                consumerGroup.run();
            }).thenRun(() ->{
                System.out.println("Out of the loop");
            });

        CompletableFuture<Void> future2 = new CompletableFuture().runAsync(() ->{
            System.out.println("just before the producer");
            producerGroup.run();
        }).thenRun(() ->{
            System.out.println("Out of the loop2");
        });

        future.join();
        future2.join();


    }


}
