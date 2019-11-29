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
    private String dburl = "jdbc:mysql://192.168.1.13:3306/Universe?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&relaxAutoCommit=false";
    private String username = "root";
    private String password = "lovelove";
    private Connection connection = null;
 ConsumerRecord<String,MessageObject> lastRecord = null;

 //TODO: Create TABLE NAME variable so every user can have owned table


//    public MySqlDatabase(String configFile) {
//        HikariConfig config = new HikariConfig(configFile);
//        db = new HikariDataSource(config);
//    }


    public void shutdown() {
        db.close();
    }

    public void insertMessageToDB(List<ConsumerRecord<String, MessageObject>> records) throws Exception {

        System.out.println("You arrived here");

        try{
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();}

        Connection connection = DriverManager.getConnection(dburl,username,password);


// TODO: dynamically create tables with different names
// TODO: 2) Create Online Odoo accounts



        if(isTableExisting("Sale")){
            queryStatements(records);
        } else{
            System.out.println("we are to do this");
            String sql = "CREATE TABLE Sale (amount_tax INTEGER,amount_total INTEGER,amount_untaxed INTEGER,invoice_status VARCHAR(255))";
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            queryStatements(records);
        }



    }

    public void queryStatements(List<ConsumerRecord<String, MessageObject>> records) throws SQLException {
        HashMap<Integer, Long> offsetMap = new HashMap();
        System.out.println("we are to do this Next");
        String query = "INSERT IGNORE INTO Sale (id,amount_tax,amount_total,amount_untaxed,invoice_status) VALUE (?,?,?,?,?)";

        PreparedStatement preparedStatement = connection.prepareStatement(query);
        for(ConsumerRecord<String,MessageObject>record: records){

            record.value();
            JSONObject jsonObject = new JSONObject(record.value());
            messageObject.fromJson(jsonObject);
            System.out.println("Now we are here hurray");
            preparedStatement.setInt(1,messageObject.fromJson(jsonObject).getId());
            preparedStatement.setInt(2, messageObject.fromJson(jsonObject).getAmount_tax());
            preparedStatement.setInt(3, messageObject.fromJson(jsonObject).getAmount_total());
            preparedStatement.setInt(4, messageObject.fromJson(jsonObject).getAmount_untaxed());
            preparedStatement.setString(5, messageObject.fromJson(jsonObject).getInvoice_status());
            preparedStatement.addBatch();

            if (offsetMap.get(record.partition()) == null || offsetMap.get(record.partition()) < record.offset()) {
                offsetMap.put(record.partition(), record.offset());
                this.lastRecord = record;
            }
        }
        preparedStatement.executeBatch();

//        connection.commit();

    }

    public void saveOffset(String topic, int partition, long offset) throws Exception {

    }

    public long getOffset(String topic, int partition) throws SQLException {
        return 0;
    }


    public boolean isTableExisting(String tableName) throws SQLException {
        boolean tableExists = false;
        Connection con = DriverManager.getConnection(dburl,username,password);
        DatabaseMetaData databaseMetaData = con.getMetaData();
        String[] types = {"TABLE"};
        try {
            ResultSet tableNames = databaseMetaData.getTables(null,"%", tableName , null); // instead of types
            tableExists = true;
        } catch (SQLException e) {
            e.printStackTrace();
//                    ("DBHandler.isTableExisting - SQLEXception: "+" sqlState "+e.getSQLState()+" errorcode: "+e.getErrorCode());
        }
        return tableExists;
    }

}
