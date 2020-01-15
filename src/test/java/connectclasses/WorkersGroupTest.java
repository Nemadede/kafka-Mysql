package connectclasses;

import connectclasses.new_scheme.ProducerThread2;

import java.util.Properties;

public class WorkersGroupTest {

    public Properties props(){
        Properties properties = new Properties();
        properties.put("bootstrap.servers","127.0.0.1:9092");
        properties.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.put("topic","odooGroup");
        System.out.println(properties);
        return properties;
    }
//    ProducerThread2 p1 = new ProducerThread2(props());
//    ProducerThread2 p2 = new ProducerThread2(props());
//    ProducerThread2 p3 = new ProducerThread2(props());


//    public void run() {
//        Thread thread = new Thread(p1);
//    }
}
