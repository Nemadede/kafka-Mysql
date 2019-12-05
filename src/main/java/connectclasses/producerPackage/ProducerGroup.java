package connectclasses.producerPackage;

import connectclasses.apiClients.Database;
import connectclasses.apiClients.Users;
import connectclasses.common.ConfigClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ProducerGroup {
    private CountDownLatch latch;
    private List<Producer> producers = new ArrayList<>();

    private final Logger logger = LoggerFactory.getLogger(ProducerGroup.class.getName());
    private final String bootstrapServer;
    private final String groupID;
    private final String topic;
    private final String keyS;
    private final String valueS;
    private ConfigClass config;
    private Users user = new Users();
//    private Database database;

    public ProducerGroup(Properties properties, ConfigClass config){
        this.bootstrapServer = properties.getProperty("bootstrap.servers");
        this.groupID = properties.getProperty("group.id");
        this.topic = properties.getProperty("topic");
        this.keyS = properties.getProperty("key.serializer");
        this.valueS = properties.getProperty("value.serializer");
        this.config = config;
    }


    public void assignListProducer(List<Producer> producers){
        this.producers = producers;
        latch = new CountDownLatch(this.producers.size());
        for(Producer producer: this.producers){
            producer.setPartition(user.setId());
            producer.setLatch(latch);
        }
    }


    public void run(){
        for(Producer producer: producers){
            Thread thread = new Thread(producer);
            thread.start();


        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Caught shutdown hook");
            await(this.latch);
            logger.info("Consumer Group has exited");
        }));
        await(latch);

    }


    private void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("Application got interrupted", e);
        } finally {
            logger.info("Application is closing");
        }
    }
}
