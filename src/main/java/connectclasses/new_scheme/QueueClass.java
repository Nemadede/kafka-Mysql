package connectclasses.new_scheme;

import connectclasses.new_scheme.consumer.Consumer2;
import org.json.JSONArray;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class QueueClass {
    Queue<JSONArray> queue = new LinkedList<>();
    Queue<Map> queue2 = new LinkedList<>();
    List<Puller> continuosPullers = new ArrayList<>();
    JSONArray data;
    int cores = Runtime.getRuntime().availableProcessors();
    ExecutorService exec = Executors.newFixedThreadPool(cores);
    ExecutorService exec_2 = Executors.newFixedThreadPool(cores);
//    ForkJoinPool forkJoinPool = new ForkJoinPool(cores);

    Properties properties = new Properties();
    ProducerThread2 producer = new ProducerThread2();
    Consumer2 consumer2 = new Consumer2();

    public QueueClass() throws SQLException {
    }

    //properties
    private void cProperties(){
        Properties properties = new Properties();
        properties.put("bootstrap.servers","127.0.0.1:9092");
        properties.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.put("topic","odooGroup");
        this.properties = properties;
    }

 ;
//TODO: working with this for test drive
// Map data added to queue

    public void queueUp(List<Puller> continuosPullers) {
        this.continuosPullers = continuosPullers;

        while (true){
            System.out.println("Started here");
            CompletableFuture<Void> future1 = new CompletableFuture<>().runAsync(() -> {
                for (Puller continuousPuller: this.continuosPullers){
                    System.out.println("Next here");
                    try {
                        Map map  = continuousPuller.call();
                        if(map != null){
                            System.out.println(" Added To Queue");
                            this.queue2.add(map);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            CompletableFuture<Void> future = new CompletableFuture<>().runAsync(() -> {
                System.out.println("Starting CONSUMER from queueClass......................................................................................................");
                exec_2.execute(consumer2);
            });

            CompletableFuture<Void> future2 = new CompletableFuture().runAsync(() -> {
                try{
                // poll from the queue with the producer
                    if(this.queue2.isEmpty()){
                        System.out.println("Empty");
                    }else {
                        Map mapOfData = queue2.remove();
                        System.out.println(mapOfData);
                       try {
                           System.out.println("Starting PRODUCER from queueClass......................................................................................................");
                           exec.execute(producer.getData(mapOfData));
                       } catch (NullPointerException e){
                           e.printStackTrace();
                       }
                    }


                    Thread.sleep(3000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

            });

            future1.join();
            future.join();
            future2.join();
        }
    }

}
