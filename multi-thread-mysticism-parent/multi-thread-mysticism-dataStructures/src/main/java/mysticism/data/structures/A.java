package mysticism.data.structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class A {

    public static void main(String[] args) {
        List<Integer> aList = new ArrayList<>();
        List<Integer> threadSafeList = Collections.synchronizedList(aList);
    }
}
