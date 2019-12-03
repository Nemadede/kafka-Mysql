package connectclasses.consumerPackage;


import connectclasses.apiClients.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

public class ConsumerGroup {
    private final Logger logger = LoggerFactory.getLogger(ConsumerGroup.class.getName());
    private CountDownLatch latch;
    private List<Consumer> consumers = new ArrayList<>();
    private Database database;

    private final String bootstrapServer;
    private final String groupID;
    private final String topic;

    public ConsumerGroup(Properties properties, Database database) {
        this.bootstrapServer = properties.getProperty("bootstrap.servers");
        this.groupID = properties.getProperty("group.id");
        this.topic = properties.getProperty("topic");
        this.database = database;
    }

    public void assignListConsumers(List<Consumer> consumers) {
        this.consumers = consumers;
        latch = new CountDownLatch(this.consumers.size());
        for (Consumer consumer : this.consumers) {
            consumer.setLatch(latch);
        }
    }

    public void run() {
//        boolean started = false;
        for (Consumer consumer: consumers) {

            Thread thread = new Thread( consumer);
//           started = true;
            thread.start();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Caught shutdown hook");
            await(this.latch);
            logger.info("Consumer Group has exited");
        }));

        await(latch);
//        return started;
    }

    private void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("Application got interrupted", e);
        } finally {
            logger.info("Application is closing");
        }
    }


//    public Future<?> run() throws ExecutionException, InterruptedException {
////        boolean started = false;
//        Future<Thread> threadFuture = null;
//        for (Consumer consumer: consumers) {
//
////            Thread thread = new Thread( consumer);
//////           started = true;
//
//            threadFuture = new Future<Thread>() {
//                @Override
//                public boolean cancel(boolean b) {
//                    return false;
//                }
//
//                @Override
//                public boolean isCancelled() {
//                    return false;
//                }
//
//                @Override
//                public boolean isDone() {
//                    return false;
//                }
//
//                @Override
//                public Thread get() throws InterruptedException, ExecutionException {
//                    return new Thread(consumer);
//                }
//
//                @Override
//                public Thread get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
//                    return null;
//                }
//            };
//            threadFuture.get();
//        }
//
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            logger.info("Caught shutdown hook");
//            await(this.latch);
//            logger.info("Consumer Group has exited");
//        }));
//
//        await(latch);
//        return threadFuture;
//    }
}
