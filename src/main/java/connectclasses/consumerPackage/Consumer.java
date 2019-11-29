package connectclasses.consumerPackage;

import org.apache.kafka.common.TopicPartition;

import java.util.concurrent.CountDownLatch;

public interface Consumer extends Runnable {
    String topic();
    String groupID();
    String bootstrapServer();
    long position(TopicPartition partition);
    void seek(TopicPartition partition, long offset);

    void setLatch(CountDownLatch latch);
}
