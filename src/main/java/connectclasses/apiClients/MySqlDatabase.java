package connectclasses.apiClients;

import com.alibaba.fastjson.JSON;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import connectclasses.common.MessageObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.List;

public class MySqlDatabase implements Database  {

    private HikariDataSource db;
    private final Logger logger = LoggerFactory.getLogger(Database.class.getName());
    MessageObject messageObject;
    private String dburl = "jdbc:mysql://192.168.43.221:3306/Universe?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&relaxAutoCommit=false";
    private String username = "root";
    private String password = "lovelove";
    private Connection connection = DriverManager.getConnection(dburl,username,password);
 ConsumerRecord<String,MessageObject> lastRecord = null;

    public MySqlDatabase() throws SQLException {
    }

    public void shutdown() {
//        db.close();
    }

    public void insertMessageToDB(List<ConsumerRecord<String, MessageObject>> records) throws Exception {
        queryStatements(records);
    }

    private void queryStatements(List<ConsumerRecord<String, MessageObject>> records) throws SQLException {
        HashMap<Integer, Long> offsetMap = new HashMap();

        String query = "INSERT IGNORE INTO Sales (id,amount_tax,amount_total,amount_untaxed,invoice_status,company_id) VALUE (?,?,?,?,?,?)";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        for(ConsumerRecord<String,MessageObject>record: records){
            record.value();
            JSONObject jsonObject = new JSONObject(record.value());
            messageObject.fromJson(jsonObject);
            preparedStatement.setInt(1,messageObject.fromJson(jsonObject).getId());
            preparedStatement.setFloat(2, messageObject.fromJson(jsonObject).getAmount_tax());
            preparedStatement.setFloat(3, messageObject.fromJson(jsonObject).getAmount_total());
            preparedStatement.setFloat(4, messageObject.fromJson(jsonObject).getAmount_untaxed());
            preparedStatement.setString(5, messageObject.fromJson(jsonObject).getInvoice_status());
            preparedStatement.setInt(6,messageObject.fromJson(jsonObject).getCompany_id());
            preparedStatement.addBatch();

            if (offsetMap.get(record.partition()) == null || offsetMap.get(record.partition()) < record.offset()) {
                offsetMap.put(record.partition(), record.offset());
                this.lastRecord = record;
            }
        }
        preparedStatement.executeBatch();

    }

    public void saveOffset(String topic, int partition, long offset) throws Exception {

    }

    public long getOffset(String topic, int partition) throws SQLException {
        return 0;
    }


//

}
