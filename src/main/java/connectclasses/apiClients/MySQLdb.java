package connectclasses.apiClients;

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

import static java.util.Arrays.asList;

public class MySQLdb  implements Database {

    private HikariDataSource db;
    String tableName;
    MessageObject messageObject;
    ConsumerRecord<String,MessageObject> lastRecord = null;


    private final Logger logger = LoggerFactory.getLogger(Database.class.getName());

        public MySQLdb(String configFile) {
        HikariConfig config = new HikariConfig(configFile);
        db = new HikariDataSource(config);
    }


    @Override
    public void shutdown() {
         db.close();
    }

    @Override
    public void insertMessageToDB(List<ConsumerRecord<String, MessageObject>> records) throws Exception {
        Connection connection = db.getConnection();
// create table name
        ConsumerRecord record1 = records.get(0);
        Object key = record1.key();
        String recordKey = (String) key;
        List recordKey2 = asList(recordKey.split(","));
        System.out.println(recordKey2);

        for(int i= 0; i<2;i++){
            System.out.println(recordKey2.get(0).toString());
            String name = recordKey2.get(0).toString().substring(1);
            System.out.println(name);
          this.tableName=  "Sale_"+name;

        }
//create table name end

        if(isTableExisting(this.tableName)){
            System.out.println("we are just doing this");
            insertData(records);
        } else{
            System.out.println("we are to do this");
            String sql = "CREATE TABLE "+ tableName +" (id INTEGER PRIMARY KEY NOT NULL,amount_tax INTEGER,amount_total INTEGER,amount_untaxed INTEGER,invoice_status VARCHAR(255))";
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            insertData(records);
        }
//        String query = "INSERT IGNORE INTO "+this.tableName+" (id,amount_tax,amount_total,amount_untaxed,invoice_status) VALUE (?,?,?,?,?) ON DUPLICATE KEY UPDATE id";
            connection.close();

    }

    public void insertData(List<ConsumerRecord<String, MessageObject>> records) throws SQLException {
        HashMap<Integer, Long> offsetMap = new HashMap();
        String tableName = this.tableName;
        Connection connection = db.getConnection();
        String query = "INSERT IGNORE INTO "+ tableName +" (id,amount_tax,amount_total,amount_untaxed,invoice_status) VALUE (?,?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        System.out.println(query +"___ Statement"+ preparedStatement);
        for(ConsumerRecord<String,MessageObject>record: records){

            record.value();
            JSONObject jsonObject = new JSONObject(record.value());
            messageObject.fromJson(jsonObject);
//            System.out.println("Now we are here hurray");
            preparedStatement.setInt(1,messageObject.fromJson(jsonObject).getId());
            preparedStatement.setFloat(2, messageObject.fromJson(jsonObject).getAmount_tax());
            preparedStatement.setFloat(3, messageObject.fromJson(jsonObject).getAmount_total());
            preparedStatement.setFloat(4, messageObject.fromJson(jsonObject).getAmount_untaxed());
            preparedStatement.setString(5, messageObject.fromJson(jsonObject).getInvoice_status());
            preparedStatement.addBatch();

            if (offsetMap.get(record.partition()) == null || offsetMap.get(record.partition()) < record.offset()) {
                offsetMap.put(record.partition(), record.offset());
                this.lastRecord = record;
            }
        }
        preparedStatement.executeBatch();
        connection.commit();


    }

    @Override
    public void saveOffset(String topic, int partition, long offset) throws Exception {

    }

    @Override
    public long getOffset(String topic, int partition) throws SQLException {
            //TODO change to getLastId Number, so you can know where next to pull from
        return 0;
    }


    public boolean isTableExisting(String tableName) throws SQLException {
        boolean tableExists = false;
        Connection con = db.getConnection();
        DatabaseMetaData databaseMetaData = con.getMetaData();
        String[] types = {"TABLE"};
        try {
            ResultSet tableNames = databaseMetaData.getTables(null,null, tableName , null); // instead of types
           if(tableNames.next()){
               tableExists = true;
           } else {
               tableExists = false;
           }
        } catch (SQLException e) {
            e.printStackTrace();
//                    ("DBHandler.isTableExisting - SQLEXception: "+" sqlState "+e.getSQLState()+" errorcode: "+e.getErrorCode());
        }
        return tableExists;
    }
}
