package com.oleksa.snapshot;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ValueLatch<T> {

    ValueIface<T> impl = new VolatileLatch();

    private interface ValueIface<T> {
        void setValue(T value);
        T getValue() throws InterruptedException;
    }

    /* blocking set & get latch, thread will go to sleep... */
    private class CountDownLatchValueImpl implements ValueIface<T> {
        private T value;
        private CountDownLatch latch = new CountDownLatch(1);
        @Override
        public void setValue(T value) {
            this.value = value;
            latch.countDown();
        }
        @Override
        public T getValue() throws InterruptedException {
            latch.await();
            return value;
        }
    }

    private class LockConditionValueImpl implements ValueIface<T> {
        private T value;
        private Lock lock = new ReentrantLock();
        private Condition present = lock.newCondition();
        @Override
        public void setValue(T value) {
            try {
                lock.lock();
                this.value = value;
                present.signal();
            } finally {
                lock.unlock();
            }
        }
        @Override
        public T getValue() throws InterruptedException {
            try {
                lock.lock();
                present.await();
                return value;
            } finally {
                lock.unlock();
            }
        }
    }

    /* non-blocking set & get, thread is busy waiting... */
    private class UnsafeValueLatch implements ValueIface<T> {
//        private Unsafe unsafe = Unsafe.getUnsafe(); // doesn't work!
        private Unsafe unsafe;
        {
            Field f = null;
            try {
                f = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                unsafe = (Unsafe) f.get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        private volatile T value; // won't work unless volatile !!!
        @Override
        public void setValue(T value) {
            try {
                boolean value1 = unsafe.compareAndSwapObject(this,
                        unsafe.objectFieldOffset(UnsafeValueLatch.class.getDeclaredField("value")),
                        null, value);
                System.out.println("value1 is " + value1);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        @Override
        public T getValue() throws InterruptedException {
            while(true) {
                if (value != null)
                    return value;
            }
        }
    }

    /* again CAS-based approach using concurrent utils */
    private class AtomicReferValueLatch implements ValueIface<T> {
//        private T value; // no need to be volatile, no use at all !!!
        private AtomicReference<T> reference = new AtomicReference<>();
        @Override
        public void setValue(T value) {
            reference.set(value);
        }
        @Override
        public T getValue() throws InterruptedException {
            while (true) {
                T val;
                if ((val = reference.get()) != null)
                    return val;
            }
        }
    }

    private class VolatileLatch implements ValueIface<T> {
        private volatile T value;
        @Override
        public void setValue(T value) {
            this.value = value;
        }

        @Override
        public T getValue() throws InterruptedException {
            while (true) {
                if (value != null) {
                    return value;
                }
            }
        }
    }

    public void setValue(T value) {
        impl.setValue(value);
    }

    public T getValue() throws InterruptedException {
        return impl.getValue();
    }
}
