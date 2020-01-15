package connectclasses.new_scheme;

import com.alibaba.fastjson.JSON;
import connectclasses.producerPackage.Producer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import static java.util.Arrays.asList;

public class ProducerThread2 implements Producer {
    private String topic;
    Properties properties = new Properties();
    private KafkaProducer<String,String> producer = new KafkaProducer<String, String>(cProperties());
    JSONArray data;
    String companyName;
    Integer userIdNum;

//    public KafkaProducer newProducer(){
//        producer = new KafkaProducer<>(cProperties());
//        return producer;
//    }

    private final Logger logger = LoggerFactory.getLogger(ProducerThread2.class);

    private Properties cProperties(){
        Properties properties = new Properties();
        properties.put("bootstrap.servers","127.0.0.1:9092");
        properties.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.put("topic","odooGroup");
      return properties;
    }

//    public ProducerThread2(){
//        Properties properties = cProperties();
////        this.producer = new KafkaProducer<>(properties);
//        this.topic = properties.getProperty("topic");
//    }
    public ProducerThread2(){
//        System.out.println("here now.. What do you think");
        Properties properties = cProperties();
        this.producer = new KafkaProducer<>(properties);
        this.topic = properties.getProperty("topic");
        System.out.println(this.topic);
//        getData(data);
    }

    @Override
    public void setLatch(CountDownLatch latch) {

    }

    @Override
    public Integer setPartition(Integer id) {
        return null;
    }

    @Override
    public Integer savedLastRecord(String pK) {
        return null;
    }

    @Override
    public void run() {
        returnRecord();
    }

//    public void getData(JSONArray data, String companyName, String userIdNum){
//        this.data = data;
//        this.companyName = companyName;
//        this.userIdNum = userIdNum;
//
//        run();
//    }

    public Runnable getData(Map data){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("data is being processed by the producer");
            }
        };

        this.data = (JSONArray) data.get("data");
        this.companyName = (String) data.get("companyName");
        this.userIdNum = (Integer) data.get("userId");
        System.out.println("see this_____________ " + this.data);
        System.out.println("see this_____________ " + this.companyName);
        System.out.println("see this_____________ " + this.userIdNum);
       run();

       return runnable;
    }

    public void returnRecord(){
        ProducerRecord record = null;
        for(Object eachObj : this.data){
            String recordString = JSON.toJSONString(eachObj);
            List recordkey = asList(this.companyName,this.userIdNum.toString());
            record = new ProducerRecord(this.topic,recordkey.toString(),recordString);                                 //specify key and partition of each record
            producer.send(record,(new Callback() {
                public void onCompletion(RecordMetadata rec, Exception ex) {
                System.out.println("we sent the message to the topic");
                    if (ex != null) {
                        logger.error("Error While processing", ex);
                    }
                }
            }));
        }


    }
}
