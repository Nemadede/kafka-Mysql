package connectclasses;

import connectclasses.apiClients.MySQLdb;
import connectclasses.apiClients.MySqlDatabase;
import connectclasses.consumerPackage.Consumer;
import connectclasses.consumerPackage.ConsumerGroup;
import connectclasses.consumerPackage.ConsumerThread;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ConsumerThreadTest {
//TODO Manually assign number of producers and consumers as you manually assign partitions
    String databaseConfig = "/home/nema-love/ideaprojects/zip files/groupwork/src/main/resources/DB.properties";
//   MySqlDatabase database = new MySqlDatabase();
MySQLdb database = new MySQLdb(databaseConfig);
   String consumerConfig = "/home/nema-love/ideaprojects/zip files/groupwork/src/main/resources/Consumer.properties";
   Properties consumerProps = new Properties();

   @Test
    public void TestConsumer() throws IOException {
       consumerProps.load(new FileInputStream(consumerConfig));
       ConsumerGroup consumerGroup = new ConsumerGroup(consumerProps, database);
       List<Consumer> consumers = new ArrayList<>();
       int cores = Runtime.getRuntime().availableProcessors();
       System.out.println(cores);
       for (int i = 0; i < cores; i++) {
           consumers.add(new ConsumerThread(consumerProps,database,1));
           consumers.add(new ConsumerThread(consumerProps,database,2));
       }

       consumerGroup.assignListConsumers(consumers);
//       consumerGroup.run();
   }


}
