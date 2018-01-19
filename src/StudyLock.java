import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StudyLock {

    public static void main(String[] args) {
//        SyncThread syncThread = new SyncThread();
//        Thread thread1 = new Thread(syncThread, "SyncThread1");
//        Thread thread2 = new Thread(syncThread, "SyncThread2");
//        thread1.start();
//        thread2.start();

//        SyncThread syncThread1 = new SyncThread();
//        SyncThread syncThread2 = new SyncThread();
//        Thread thread1 = new Thread(syncThread1, "SyncThread1");
//        Thread thread2 = new Thread(syncThread2, "SyncThread2");
//        thread1.start();
//        thread2.start();

//        Counter counter = new Counter();
//        Thread thread1 = new Thread(counter, "A");
//        Thread thread2 = new Thread(counter, "B");
//        thread1.start();
//        thread2.start();

        Account account = new Account("zhang san", 10000.0f);
        AccountOperator accountOperator = new AccountOperator(account);

        final int THREAD_NUM = 5;
        Thread threads[] = new Thread[THREAD_NUM];
        for (int i = 0; i < THREAD_NUM; i++) {
            threads[i] = new Thread(accountOperator, "Thread" + i);
            threads[i].start();
        }
    }
}

class SyncThread implements Runnable {

    private static int count;

    SyncThread() {
        count = 0;
    }

    @Override
    public void run() {
        synchronized (this) {
            for (int i = 0; i < 5; i++) {
                try {
                    System.out.println(Thread.currentThread().getName() + ":" + count++);
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getCount() {
        return count;
    }
}

class Counter implements Runnable {

    private int count;

    Counter() {
        this.count = 0;
    }

    private void addCount() {
        synchronized (this) {
            for (int i = 0; i < 5; i++) {
                try {
                    System.out.println(Thread.currentThread().getName() + ":" + count++);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void printCount() {
        for (int i = 0; i < 5; i++) {
            try {
                System.out.println(Thread.currentThread().getName() + ":" + count);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        if (threadName.equals("A")) {
            addCount();
        } else if (threadName.equals("B")) {
            printCount();
        }
    }
}

class Account {
    Account(String name, float amount) {
        this.name = name;
        this.amount = amount;
    }

    String name;
    float amount;

    public void deposit(float amt) {
        amount += amt;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void withdraw(float amt) {
        amount -= amt;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public float getBalance() {
        return amount;
    }
}

class AccountOperator implements Runnable {

    private final Account account;

    AccountOperator(Account account) {
        this.account = account;
    }

    @Override
    public void run() {
        synchronized (this) {
            ThreadLocal<String> stringThreadLocal = null;
            stringThreadLocal.set(Thread.currentThread().getName());
//            account.withdraw(100);
//            if (account.getBalance() == 10100.0f) {
//                try {
//                    System.out.println(Thread.currentThread().getName() + " account :" + account.getBalance());
//                    account.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            account.deposit(100);
//            System.out.println(Thread.currentThread().getName() + " account :" + account.getBalance());
        }
    }
}

class TestNum {
    // ①通过匿名内部类覆盖ThreadLocal的initialValue()方法，指定初始值
    private static ThreadLocal<Integer> seqNum = ThreadLocal.withInitial(() -> 0);
//    private static int seqNum = 0;

    // ②获取下一个序列值
    public int getNextNum() {
        seqNum.set(seqNum.get() + 1);
        return seqNum.get();
    }

//    public int getNextNum() {
//        seqNum += 1;
//        return seqNum;
//    }

    public static void main(String[] args) {
        TestNum sn = new TestNum();
        // ③ 3个线程共享sn，各自产生序列号
        TestClient runn = new TestClient(sn);
//        TestClient t2 = new TestClient(sn);
//        TestClient t3 = new TestClient(sn);
        Thread t1 = new Thread(runn);
        Thread t2 = new Thread(runn);
        Thread t3 = new Thread(runn);
        t1.start();
        t2.start();
        t3.start();
        System.out.println("thread[" + Thread.currentThread().getName() + "] --> sn["
                + sn.getNextNum() + "]");
    }

    private static class TestClient implements Runnable {
        private TestNum sn;

        TestClient(TestNum sn) {
            this.sn = sn;
        }

        public void run() {
            for (int i = 0; i < 3; i++) {
                // ④每个线程打出3个序列值
                System.out.println("thread[" + Thread.currentThread().getName() + "] --> sn["
                        + sn.getNextNum() + "]");
            }
        }
    }
}

class AtomicTest {

    private static long randomTime() {
        return (long) (Math.random() * 1000);
    }

    public static void main(String[] args) {
        // 阻塞队列，能容纳100个文件
        final BlockingQueue<File> queue = new LinkedBlockingQueue<>(100);
        // 线程池
        final ExecutorService exec = Executors.newFixedThreadPool(5);
        final File root = new File("D:\\ISO");
        // 完成标志
        final File exitFile = new File("");
        // 原子整型，读个数
        // AtomicInteger可以在并发情况下达到原子化更新，避免使用了synchronized，而且性能非常高。
        final AtomicInteger rc = new AtomicInteger();
        // 原子整型，写个数
        final AtomicInteger wc = new AtomicInteger();
        // 读线程
        Runnable read = new Runnable() {
            public void run() {
                scanFile(root);
                scanFile(exitFile);
            }

            void scanFile(File file) {
                if (file.isDirectory()) {
                    File[] files = file.listFiles(pathname -> pathname.isDirectory() || pathname.getPath().endsWith(".iso"));
                    for (File one : Objects.requireNonNull(files))
                        scanFile(one);
                } else {
                    try {
                        // 原子整型的incrementAndGet方法，以原子方式将当前值加 1，返回更新的值
                        int index = rc.incrementAndGet();
                        System.out.println("Read0: " + index + " " + file.getPath());
                        // 添加到阻塞队列中
                        queue.put(file);
                    } catch (InterruptedException ignored) {

                    }
                }
            }
        };
        // submit方法提交一个 Runnable 任务用于执行，并返回一个表示该任务的 Future。
        exec.submit(read);

        // 四个写线程
        for (int index = 0; index < 4; index++) {
            // write thread
            final int num = index;
            Runnable write = new Runnable() {
                String threadName = "Write" + num;

                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(randomTime());
                            // 原子整型的incrementAndGet方法，以原子方式将当前值加 1，返回更新的值
                            int index = wc.incrementAndGet();
                            // 获取并移除此队列的头部，在元素变得可用之前一直等待（如果有必要）。
                            File file = queue.take();
                            // 队列已经无对象
                            if (file == exitFile) {
                                // 再次添加"标志"，以让其他线程正常退出
                                queue.put(exitFile);
                                break;
                            }
                            System.out.println(threadName + ": " + index + " " + file.getPath());
                        } catch (InterruptedException ignored) {
                        }
                    }
                }

            };
            exec.submit(write);
        }
        exec.shutdown();
    }
}

class SleepingRunnable implements Runnable {

    private Lock mLock;

    SleepingRunnable(Lock mLock) {
        this.mLock = mLock;
    }

    @Override
    public void run() {
        mLock.lock();
//        if (("T4").equals(Thread.currentThread().getName())) {
//            mLock.lock();
//        }
        long currentTime = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() >= (currentTime + 1000)) {
                try {
                    System.out.println(Thread.currentThread().getName() + " is Sleeping");
                    Thread.sleep(1000);
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    mLock.unlock();
                }
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " is Running");
            }
        }
    }
}

class AlwaysRunningRunnable implements Runnable {

    private Lock mLock;

    AlwaysRunningRunnable(Lock mLock) {
        this.mLock = mLock;
    }

    @Override
    public void run() {
        while (true) {
            try {
                mLock.lockInterruptibly();
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mLock.unlock();
            }
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } finally {
//            }
            System.out.println(Thread.currentThread().getName() + " is Running");
        }
    }
}

class StopOtherThreadRunnable implements Runnable {

    private Lock mLock;
    private Thread mThread;

    StopOtherThreadRunnable(Lock mLock, Thread mThread) {
        this.mLock = mLock;
        this.mThread = mThread;
    }

    @Override
    public void run() {
//        while (true) {
        mLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + " is Running");
            Thread.sleep(1000);
            mThread.interrupt();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mLock.unlock();
    }
}


/**
 * 可重入锁
 */
class LockStudy {
    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        SleepingRunnable sleepingRunnable = new SleepingRunnable(lock);
        Thread t1 = new Thread(sleepingRunnable, "T1");
        Thread t2 = new Thread(sleepingRunnable, "T2");
        Thread t3 = new Thread(sleepingRunnable, "T3");
        Thread t4 = new Thread(sleepingRunnable, "T4");
        Thread t5 = new Thread(sleepingRunnable, "T5");
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
    }
}

/**
 * 可重入读写锁
 */
class RWLockStudy {
    public static void main(String[] args) {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        Thread t1 = new Thread(new SleepingRunnable(readLock), "T1");
        Thread t2 = new Thread(new SleepingRunnable(writeLock), "T2");
        Thread t3 = new Thread(new SleepingRunnable(writeLock), "T3");
        Thread t4 = new Thread(new SleepingRunnable(readLock), "T4");
        t2.start();
        t1.start();
        t4.start();
        t3.start();
    }
}

/**
 * 可中断上锁
 */
class LockInterruptiblyStudy {
    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        AlwaysRunningRunnable alwaysRunningRunnable = new AlwaysRunningRunnable(lock);
        Thread t1 = new Thread(alwaysRunningRunnable, "T1");
        StopOtherThreadRunnable sleepingRunnable = new StopOtherThreadRunnable(lock, t1);
        Thread t2 = new Thread(sleepingRunnable, "T2");
        t1.start();
        t2.start();
    }
}


class Buffer {
    private final Lock lock;
    private final Condition notFull;
    private final Condition notEmpty;
    private int maxSize;
    private List<Date> storage;

    Buffer(int size) {
        //使用锁lock，并且创建两个condition，相当于两个阻塞队列
        lock = new ReentrantLock();
        notFull = lock.newCondition();
        notEmpty = lock.newCondition();
        maxSize = size;
        storage = new LinkedList<>();
    }

    public void put() {
        lock.lock();
        try {
            while (storage.size() == maxSize) {//如果队列满了
                System.out.print(Thread.currentThread().getName() + ": wait \n");
                ;
                notFull.await();//阻塞生产线程
            }
            storage.add(new Date());
            System.out.print(Thread.currentThread().getName() + ": put:" + storage.size() + "\n");
            Thread.sleep(1000);
            notEmpty.signalAll();//唤醒消费线程
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void take() {
        lock.lock();
        try {
            while (storage.size() == 0) {//如果队列满了
                System.out.print(Thread.currentThread().getName() + ": wait \n");
                ;
                notEmpty.await();//阻塞消费线程
            }
            Date d = ((LinkedList<Date>) storage).poll();
            System.out.print(Thread.currentThread().getName() + ": take:" + storage.size() + "\n");
            Thread.sleep(1000);
            notFull.signalAll();//唤醒生产线程

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}

class Producer implements Runnable {
    private Buffer buffer;

    Producer(Buffer b) {
        buffer = b;
    }

    @Override
    public void run() {
        while (true) {
            buffer.put();
        }
    }
}

class Consumer implements Runnable {
    private Buffer buffer;

    Consumer(Buffer b) {
        buffer = b;
    }

    @Override
    public void run() {
        while (true) {
            buffer.take();
        }
    }
}

class StudyCondition {
    public static void main(String[] arg) {
        Buffer buffer = new Buffer(10);
        Producer producer = new Producer(buffer);
        Consumer consumer = new Consumer(buffer);
        for (int i = 0; i < 3; i++) {
            new Thread(producer, "producer-" + i).start();
        }
        for (int i = 0; i < 3; i++) {
            new Thread(consumer, "consumer-" + i).start();
        }
    }
}






