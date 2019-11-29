package connectclasses.producerPackage;

import java.util.concurrent.CountDownLatch;

public interface Producer extends Runnable {
// set latch method
    //set Id and partition method
void setLatch(CountDownLatch latch);
Integer setPartition(Integer id);
Integer savedLastRecord(String pK);

}
