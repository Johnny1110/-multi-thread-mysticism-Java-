package mysticism.data.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.IntStream;

public class SkipListMapDemo {
    public static void main(String[] args) {
        Map<Integer, Integer> map = new ConcurrentSkipListMap<>();
        IntStream.of(9,7,6,8,4,5,1,3,2).forEach(i -> {
            map.put(i, i);
        });
        for(Map.Entry<Integer, Integer> entry:map.entrySet()){
            System.out.println(entry.getKey());
        }
    }
}
