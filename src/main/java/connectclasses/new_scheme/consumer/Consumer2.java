package connectclasses.new_scheme.consumer;

import connectclasses.apiClients.Database;
import connectclasses.apiClients.MySqlDatabase;
import connectclasses.common.MessageObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static java.util.Arrays.asList;

public class Consumer2 implements Runnable{
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
    ConsumerRecords<String, MessageObject> records;
    private long minBatchSize =Long.parseLong(cProperties().getProperty("min.batch.size"));
    private List<ConsumerRecord<String,MessageObject>> buffer = new ArrayList<>();
    private KafkaConsumer<String, MessageObject> consumer = new KafkaConsumer<String, MessageObject>(cProperties(),new StringDeserializer(),new JsonDeserializer(MessageObject.class));
    Database database = new MySqlDatabase();
    String topic = cProperties().getProperty("topic");

    public Consumer2() throws SQLException {
        this.consumer.subscribe(Collections.singletonList(this.topic));
    }

    public Integer getCompanyId(ConsumerRecord<String, MessageObject> record){
        int company_id =0;
        //get the id from the records
        Object key = record.key();
        String recordKey = (String) key;
        List recordKey2 = asList(recordKey.split(","));
        for(int i= 0; i<2;i++){
            String cname = recordKey2.get(1).toString().substring(1);
            char[] a= cname.toCharArray();
             company_id = Integer.parseInt(String.valueOf(a[0]));
        }
        //end
            return company_id;
    }

    @Override
    public void run() {
     try{
       while (true){
           this.records = this.consumer.poll(Duration.ofMillis(1000));
           for( ConsumerRecord<String, MessageObject> record: records){
            Integer companyid = getCompanyId(record);
            record.value().setCompany_id(companyid);
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
//            this.consumer.close();
       }

     } catch (Exception e){
         e.printStackTrace();
     }
     finally
      {

        this.consumer.close();

        }
    }
}
