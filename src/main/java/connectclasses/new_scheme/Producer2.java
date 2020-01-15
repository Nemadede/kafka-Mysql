package connectclasses.new_scheme;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public interface Producer2 extends Callable {
    void setLatch(CountDownLatch latch);
    Integer setPartition(Integer id);
    Integer savedLastRecord(String pK);

}
