package mysticism.advancedConception;

import java.util.concurrent.ConcurrentHashMap;

public class Temp<K, V> {

    public V put(K key, V value) {
        Segment<K, V> s;
        if (value == null)
            throw new NullPointerException();
        int hash = hash(key);
        int j = (hash >>> segmentShift) & segmentMask;
        if ((s = (Segment<K, V>)UNSAFE.getObject(segment, (j << SSHIFT) + SBASE)) == null) {
            s = ensureSegment(j); //取得段
        }
        return s.put(key, hash, value, false);
    }

    public static void main(String[] args) {
        int num = 32;
        int ans = num >>> 2;
        System.out.println(ans);
    }

}
