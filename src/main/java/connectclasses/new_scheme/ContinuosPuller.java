package connectclasses.new_scheme;

import com.alibaba.fastjson.JSON;
import connectclasses.apiClients.OdooApiClient;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;

public class ContinuosPuller implements Puller {
    private final Logger logger = LoggerFactory.getLogger(ContinuosPuller.class);
     String  user;
     String host;
     String dbName;
     String password;
     Integer userIdNum;
    String companyName;
    String topic;
    public final String CNAME = "companyName";
    public final String DATA = "data";
    public final String UID = "userId";
    int lastUpdated_length;
    OdooApiClient odooApiClient;
    ProducerRecord producerRecord;
    int counter = 0;

    public ContinuosPuller(String user, String host, String dbName, String password, Integer userIdNum, String companyName) {
        this.user = user;
        this.host = host;
        this.dbName = dbName;
        this.password = password;
        this.userIdNum = userIdNum;
        this.companyName = companyName;
        this.odooApiClient = new OdooApiClient();
        this.odooApiClient.login(this.host,this.dbName,this.user,this.password);
        this.lastUpdated_length = getData().length();
    }

    public String getUser(){
        return this.user;
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




    @Override
    public Map call()  {
        ProducerRecord<String,String> record = null;
        Map map = new HashMap();
        System.out.println(this.counter);
        if(this.counter != 0){
            System.out.println("counter is greater than one");
        }
        else
            {
            System.out.println(getData());
            map.put(DATA,getData());
            map.put(CNAME,this.companyName);
            map.put(UID,this.userIdNum);
            System.out.println("Mathias");
            this.counter =1;
            return map;
        }

        System.out.println(this.lastUpdated_length);
        while (true){
            System.out.println("I entered here");
            JSONArray dataRecord = getData();
            int currentPullLength = dataRecord.length();
            System.out.println(currentPullLength);
            if (this.lastUpdated_length != currentPullLength){
                map.put(DATA,dataRecord);
                map.put(CNAME,this.companyName);
                map.put(UID,this.userIdNum);
                System.out.println(map);
                System.out.println("Im not out yet");
                this.lastUpdated_length = dataRecord.length();
                return map;
            } else {
                map = null;
                return map;
            }

        }

    }

}
