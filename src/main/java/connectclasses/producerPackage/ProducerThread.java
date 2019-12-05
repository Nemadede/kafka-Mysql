package connectclasses.producerPackage;

import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
import connectclasses.apiClients.OdooApiClient;
import connectclasses.apiClients.Users;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.concurrent.ManagedTask;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;

import static java.util.Arrays.asList;


public class ProducerThread implements Producer {
OdooApiClient odooApiClient;
    private final KafkaProducer<String, String> producer;
    private final Logger logger = LoggerFactory.getLogger(ProducerThread.class);
    private final String topic;
     Integer partition;
     Integer pK;

    CustomPartition customPartition = new CustomPartition();

    String user;
    String host;
    String dbName;
    String password;
    Integer userIdNum;
    String companyName;

    public ProducerThread(Properties properties, String user,String host, String dbName,String password,Integer userIdNum,String companyName) {


        this.topic = properties.getProperty("topic");
        producer = new KafkaProducer<String,String>(properties);
        logger.info("Producer initialized");
        this.partition = userIdNum;
        this.user= user;
        this.host = host;
        this.dbName = dbName;
        this.password=password;
        this.userIdNum =userIdNum;
        this.companyName = companyName;
        this.odooApiClient = new OdooApiClient();


        System.out.println(this.dbName);
//        producer.initTransactions();
        this.odooApiClient.login(this.host,this.dbName,this.user,this.password); //login with credentials


    }


    int dataLength = 0;

    @Override
    public void setLatch(CountDownLatch latch) {

    }

    @Override
    public Integer setPartition(Integer id) {
        return null;
    }

    @Override
    public Integer savedLastRecord(String pK) {
        return this.pK;
    }


public JSONArray getData(){
    JSONArray arrayObj = null;
//pull data from odoo using odoo ApI client
    Object obj = this.odooApiClient.executeMethod( "sale.order","search_read",asList(asList(
            asList("require_payment", "=", "true"))),
            new HashMap() {{
                put("fields", asList("id","amount_tax", "amount_total", "amount_untaxed","invoice_status"));

            }});

        try{
            String strObj = JSON.toJSONString(obj);
            arrayObj = new JSONArray(strObj);
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    return arrayObj;
}


    public void run() {
       try {

           ProducerRecord<String, String> record = null;
           JSONArray recordObj = getData();
           int lastUpdated_length = getData().length();
           System.out.println(lastUpdated_length);

           // do first pull and send to topic
           for(Object eachOj: recordObj){
               String string = JSON.toJSONString(eachOj);
               List key = asList(this.companyName,this.userIdNum.toString());
               record = new ProducerRecord(this.topic,partition,key.toString(),string);                                 //specify key and partition of each record
               try {
                   producer.send(record,(new Callback() {
                       public void onCompletion(RecordMetadata rec, Exception ex) {

                           if (ex != null) {
                               logger.error("Error While processing", ex);
                           }
                       }
                   })).get();
               } catch (InterruptedException e) {
                   e.printStackTrace();
               } catch (ExecutionException e) {
                   e.printStackTrace();
               }
           }


// enter the forever loop which only sends data to the topic if there's an update
           while (true)
           {
               JSONArray dataRecord = getData();
               int currentPullLength = dataRecord.length();

               if(lastUpdated_length == currentPullLength){
                   continue;
               }
               // else statement handling data pull on update
               else
               {
                   System.out.println("Update ohhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh" + lastUpdated_length + currentPullLength);
                   for(Object eachObj : dataRecord){

                       String recordString = JSON.toJSONString(eachObj);
                       List recordkey = asList(this.companyName,this.userIdNum.toString());

                       record = new ProducerRecord(this.topic,partition,recordkey.toString(),recordString);                                 //specify key and partition of each record

                       producer.send(record,(new Callback() {
                           public void onCompletion(RecordMetadata rec, Exception ex) {

                               if (ex != null) {
                                   logger.error("Error While processing", ex);
                               }
                           }
                       }));
                   }
                   lastUpdated_length = dataRecord.length(); //update the record value to the last update count
               }


           }
       } finally
           {
           this.producer.close();
       }

    }


    }








