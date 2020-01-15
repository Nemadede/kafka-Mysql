package connectclasses;


import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import kafka.zk.AdminZkClient;
import kafka.zk.KafkaZkClient$;
import org.I0Itec.zkclient.ZkClient;
import org.apache.kafka.common.utils.Time;

import kafka.zk.KafkaZkClient;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

public class TopicTest {
    String zookeeperHost = "127.0.0.1:2181";
    Boolean isSucre = false;
    int sessionTimeoutMs = 200000;
    int connectionTimeoutMs = 15000;
    int maxInFlightRequests = 10;
    Time time = Time.SYSTEM;
    String metricGroup = "myGroup";
    String metricType = "myType";
    String topicName1 = "myTopic";
    int partitions = 3;
    int replication = 1;
    Properties topicConfig = new Properties();

    AdminZkClient adminZkClient;



@Test
public void topicCreator(){
    KafkaZkClient zkClient = KafkaZkClient.apply(zookeeperHost,isSucre,sessionTimeoutMs,connectionTimeoutMs,maxInFlightRequests,time,metricGroup,metricType);
    adminZkClient = new AdminZkClient(zkClient);
  adminZkClient.createTopic(topicName1,partitions,replication,topicConfig, RackAwareMode.Disabled$.MODULE$);

}

// using ZK utils

 String zkConnect = "127.0.0.1:2181";

@Test
    public void topicCreator2() throws IOException {
    ZkClient zkClient = new ZkClient(zkConnect,sessionTimeoutMs,connectionTimeoutMs, ZKStringSerializer$.MODULE$);
    ZkUtils zkUtils = ZkUtils.apply(zkClient,false);

    //setting up brokers
    Properties brokerProps = new Properties();
    brokerProps.setProperty("zookeeper.connect", zkConnect);
    brokerProps.setProperty("broker.id", "0");
    brokerProps.setProperty("log.dirs", Files.createTempDirectory("kafkaUtils-").toAbsolutePath().toString());
//    brokerProps.setProperty("listeners", "PLAINTEXT://" + BROKERHOST + ":" + BROKERPORT);
//    KafkaConfig config = new KafkaConfig(brokerProps);
    Time time = Time.SYSTEM;
    AdminUtils.createTopic(zkUtils,"topicOne",1,1,topicConfig,RackAwareMode.Disabled$.MODULE$);
    }

}
