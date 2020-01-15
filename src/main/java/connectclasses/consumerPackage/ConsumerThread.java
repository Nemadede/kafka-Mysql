package connectclasses.consumerPackage;

import connectclasses.apiClients.Database;
import connectclasses.common.MessageObject;
import connectclasses.producerPackage.CustomPartition;
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
    private CustomPartition customPartition;

    public ConsumerThread(Properties properties, Database database,Integer part){
        this.database = database;
        this.part = part;
        this.bootstrapServer = properties.getProperty("bootstrap.servers");
        this.groupID = properties.getProperty("group.id");
        this.topic = properties.getProperty("topic");

        this.consumer = new KafkaConsumer<String, MessageObject>(properties,new StringDeserializer(),new JsonDeserializer(MessageObject.class));
//        this.consumer = new KafkaConsumer<>(properties);
        this.parti = new TopicPartition(this.topic,part);
        customPartition.createPartitions(this.topic,part,properties);
//        this.consumer.subscribe(Collections.singletonList(this.topic));
        this.consumer.assign(Collections.singleton(this.parti));
        this.minBatchSize = Long.parseLong(properties.getProperty("min.batch.size"));

    }

    public String topic() {
        return this.topic;
    }

    public String groupID() {
        return this.groupID;
    }

    public String bootstrapServer() {
        return this.bootstrapServer;
    }

    public long position(TopicPartition partition) {
        return this.part;
    }

    public void seek(TopicPartition partition, long offset) {

    }

    @Override
    public void setLatch(CountDownLatch latch) {

    }

    public void run() {
        try {
            while (true){
                // pull record from topic
                ConsumerRecords<String, MessageObject> records = this.consumer.poll(Duration.ofMillis(1000));

           int i= records.count();

                for( ConsumerRecord<String, MessageObject> record: records){

                    buffer.add(record);
                }

                    try {

                        if (buffer.size() >= this.minBatchSize) {
                            database.insertMessageToDB(buffer); // insert into the database
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
