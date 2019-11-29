package connectclasses.consumerPackage;

import connectclasses.apiClients.Database;
import connectclasses.common.MessageObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class ConsumerThread implements Consumer {

    private final Logger logger = LoggerFactory.getLogger(ConsumerThread.class.getName());
    private String bootstrapServer;
    private String topic = "odoo";
    private String groupID;

    private long minBatchSize;
    private final Integer part;
    TopicPartition parti;

    private KafkaConsumer<String, MessageObject> consumer;
    private List<ConsumerRecord<String,MessageObject>> buffer = new ArrayList<>();

    private Database database;

    public ConsumerThread(Properties properties, Database database,Integer part){
        this.database = database;
        this.part = part;
        this.bootstrapServer = properties.getProperty("bootstrap.servers");
        this.groupID = properties.getProperty("group.id");
        this.topic = properties.getProperty("topic");
        this.consumer = new KafkaConsumer<String, MessageObject>(properties,new StringDeserializer(),new JsonDeserializer(MessageObject.class));
//        this.consumer = new KafkaConsumer<>(properties);
        this.parti = new TopicPartition(this.topic,part);
//        this.consumer.subscribe(Collections.singletonList(this.topic));
        this.consumer.assign(Collections.singleton(parti));
        this.minBatchSize = Long.parseLong(properties.getProperty("min.batch.size"));

    }

    public String topic() {
        return null;
    }

    public String groupID() {
        return null;
    }

    public String bootstrapServer() {
        return null;
    }

    public long position(TopicPartition partition) {
        return 0;
    }

    public void seek(TopicPartition partition, long offset) {

    }

    @Override
    public void setLatch(CountDownLatch latch) {

    }

    public void run() {
        try {
            while (true){
                ConsumerRecords<String, MessageObject> records = this.consumer.poll(Duration.ofMillis(1000));
                System.out.println("This is the records +++++_______" + records);

            System.out.println("This is the buffer_______" + buffer);

           int i= records.count();

           System.out.println("number ___________ " + i);

                for( ConsumerRecord<String, MessageObject> record: records){
                    System.out.println("Check this out" + record);
                    System.out.println("Im in here now");
                    buffer.add(record);
                }

                    try {
                        System.out.println("This is the buffer_______" + buffer);

//                        database.insertMessageToDB(buffer);

                        if (buffer.size() >= this.minBatchSize) {
                            database.insertMessageToDB(buffer);
                            buffer.clear();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

            }
        } finally {

       this.consumer.close();

        }
    }
}
