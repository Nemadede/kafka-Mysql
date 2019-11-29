package connectclasses.producerPackage;

import connectclasses.apiClients.Users;
import connectclasses.common.ConfigClass;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

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
}
