package connectclasses.new_scheme;

import org.json.JSONArray;

import java.util.Map;
import java.util.concurrent.Callable;

public interface Puller extends Callable {
  JSONArray getData();
  Map call() throws Exception;

//    Map callb();

  String getUser();
}
