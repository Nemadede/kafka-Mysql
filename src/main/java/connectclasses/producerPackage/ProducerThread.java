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
    Users users;
    CustomPartition customPartition = new CustomPartition();

    String user;
    String host;
    String dbName;
    String password;
    Integer userIdNum;
    String companyName;
    public ProducerThread(Properties properties, String user,String host, String dbName,String password,Integer userIdNum,String companyName) {


        this.topic = properties.getProperty("topic");
//        users = new Users(user,host,dbName,password,userIdNum,companyName);
        producer = new KafkaProducer<String,String>(properties);
        logger.info("Producer initialized");
        this.partition = userIdNum;
        this.user= user;
        this.host = host;
        this.dbName = dbName;
        this.password=password;
        this.userIdNum =userIdNum;
        this.companyName = companyName;


    }



    public void run() {


        odooApiClient =new OdooApiClient();
        System.out.println(this.dbName);
        odooApiClient.login(this.host,this.dbName,this.user,this.password);

        ProducerRecord<String, String> record = null;
        Object obj = odooApiClient.executeMethod( "sale.order","search_read",asList(asList(
                asList("require_payment", "=", "true"))),
                new HashMap() {{
                    put("fields", asList("id","amount_tax", "amount_total", "amount_untaxed","invoice_status"));

                }});
        String strObj = JSON.toJSONString(obj);
        JSONArray arrayObj = new JSONArray(strObj);
        for(Object eachObj: arrayObj){
                String string = JSON.toJSONString(eachObj);
                List key = asList(this.companyName,this.userIdNum.toString());
//            record = new ProducerRecord("odoo",string);                                                                         //without key specified
            record = new ProducerRecord(this.topic,partition,key.toString(),string);                                 //specify key and partition
//            record = new ProducerRecord("odoo",users.setId().toString(),string);                                              //specify key only


            System.out.println("see this___________" + record);

//            producer.send(record); // this or the try statement below

            try {
                System.out.println("Entered try method");
                producer.send(record,(new Callback() {
                    public void onCompletion(RecordMetadata rec, Exception ex) {
//                    logger.info("Partition ",rec.partition());
                        if (ex != null) {
                            logger.error("Error While processing", ex);
                        }
                    }
                })).get();

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
        }

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

}
