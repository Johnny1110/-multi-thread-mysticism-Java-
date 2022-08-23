package com.firzo.mysticism.nonLock;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class LockFreeVector<E> {

    private final AtomicReferenceArray<AtomicReferenceArray<E>> buckets;

    private static final int N_BUCKET = 30;

    private static final int FIRST_BUCKET_SIZE = 8;

    private static final boolean debug = true;

    private AtomicReference<Descriptor<E>> descriptor;

    public LockFreeVector() {
        buckets = new AtomicReferenceArray<>(N_BUCKET);
        buckets.set(0, new AtomicReferenceArray<E>(FIRST_BUCKET_SIZE));
        descriptor = new AtomicReference<>(new Descriptor<E>(0, null));
    }

    private static class Descriptor<E> {
        public int size;
        volatile WriteDescriptor<E> writeop;

        public Descriptor(int size, WriteDescriptor<E> writeop) {
            this.size = size;
            this.writeop = writeop;
        }

        public void completeWrite() {
            WriteDescriptor<E> tmpop = writeop;
            if (tmpop != null) {
                tmpop.doIt();
                writeop = null;
            }
        }

    }

    private static class WriteDescriptor<E> {
        public E oldV;
        public E newV;
        public AtomicReferenceArray<E> addr;
        public int addr_index;

        public WriteDescriptor(AtomicReferenceArray<E> addr, int addr_index, E oldV, E newV) {
            this.addr = addr;
            this.addr_index = addr_index;
            this.oldV = oldV;
            this.newV = newV;
        }

        public void doIt() {
            addr.compareAndSet(addr_index, oldV, newV);
        }
    }

    public void push_back(E e){
        Descriptor<E> desc;
        Descriptor<E> newd;
        do {
            desc = descriptor.get();
            desc.completeWrite();
            int pos = desc.size + FIRST_BUCKET_SIZE;
            int zeroNumPos = Integer.numberOfLeadingZeros(pos);
            int bucketIndex = zeroNumFrist - zeroNumPos;
            if (buckets.get(bucketIndex) == null){
                int newLen = 2 * buckets.get(bucketIndex - 1).length();
                if (debug) {
                    System.out.println("New Length is:" + newLen);
                }
                buckets.compareAndSet(bucketIndex, null, new AtomicReferenceArray<E>(newLen));
            }
            int idx = (0x8000000 >>> zeroNumPos) ^ pos;
            newd = new Descriptor<E>(desc.size + 1, new WriteDescriptor<>(
                    buckets.get(bucketIndex), idx, null, e
            ));
        } while (!descriptor.compareAndSet(desc, newd));
        descriptor.get().completeWrite();
    }


}
