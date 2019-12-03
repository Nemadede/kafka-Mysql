package connectclasses.producerPackage;

import connectclasses.apiClients.Users;
import connectclasses.common.ConfigClass;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CustomPartition implements Partitioner {
    Users users = new Users();
    @Override
    public int partition(String s, Object o, byte[] bytes, Object o1, byte[] bytes1, Cluster cluster) {
        int partition = 0;
        int num = Integer.parseInt(o.toString());
        if(num != 0){
            partition = num;
        }
        return partition;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }

    public static void createPartitions(String topicName, int numPartitions, Properties props) {
//        Properties props = new Properties();
//        props.put("bootstrap.servers","localhost:9092");
        AdminClient adminClient = AdminClient.create(props);

        Map<String, NewPartitions> newPartitionSet = new HashMap<>();
        newPartitionSet.put(topicName, NewPartitions.increaseTo(numPartitions));
        adminClient.createPartitions(newPartitionSet);
        adminClient.close();
    }
}
