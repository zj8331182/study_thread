public class ThreadStudy {
    public static void main(String[] args) {
//        MyThread m = new MyThread();
//        Thread thread = new Thread(m);
//        thread.start();
//        while (true) {
//            System.out.println("main thread is run");
//
//        }

//        new TicketWindowThread().start();
//        new TicketWindowThread().start();
//        new TicketWindowThread().start();
//        new TicketWindowThread().start();

//        TicketWindowRunnable runnable = new TicketWindowRunnable();
//        Thread t = new Thread(runnable, "Z_4");
//        t.setPriority(Thread.MIN_PRIORITY);
//        t.start();
//        new Thread(runnable, "Z_1").start();
//        new Thread(runnable, "Z_2").start();
//        new Thread(runnable, "Z_3").start();

//        System.out.println(String.format("Main thread is Daemon %1$s", Thread.currentThread().isDaemon()));
//        DamonRunnable damonRunnable = new DamonRunnable();
//        Thread thread = new Thread(damonRunnable, "Daemon Thread");
//        thread.setDaemon(true);
//        thread.start();
//        for (int i = 0; i < 100; i++) {
//            System.out.println(i);
//        }

//        SleepRunnable sleepRunnable = new SleepRunnable();
//        Thread thread = new Thread(sleepRunnable, "sleepRunnable");
//        thread.start();
//        for (int i = 0; i < 100; i++) {
//            if (i == 5) {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            System.out.println(i);
//        }

//        new YieldThread("A").start();
//        new YieldThread("B").start();

//        Thread thread1 = new Thread(new GetLockRunable(0), "A");
//        Thread thread2 = new Thread(new GetLockRunable(1), "B");
//        thread1.start();
//        thread2.start();
        DataSet dataSet = new DataSet();
        Thread thread1 = new Thread(new InputRunnable(dataSet), "A");
        Thread thread2 = new Thread(new OutPutRunnable(dataSet), "B");
        thread1.start();
        thread2.start();
    }
}

class MyThread implements Runnable {

    @Override
    public void run() {
        while (true) {
            System.out.println("MyTread is run");
        }
    }
}

class TicketWindowThread extends Thread {

    private int number = 10;

    @Override
    public void run() {
        while (number > 0) {
            System.out.println(String.format("%1$s thread has send number %2$d ticket", currentThread().getName(), number));
            number--;
        }
    }
}

class TicketWindowRunnable implements Runnable {

    private int number = 10;

    @Override
    public void run() {
        while (true) {
            saleTicket();
            if (number <= 0) {
                break;
            }
        }
    }

    synchronized private void saleTicket() {
        if (number > 0) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(String.format("%1$s thread has send number %2$d ticket", Thread.currentThread().getName(), number--));
        }
    }
}


class DamonRunnable implements Runnable {

    @Override
    public void run() {
        while (true) {
            System.out.println(String.format("%1$s thread is running", Thread.currentThread().getName()));
        }
    }
}

class SleepRunnable implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            if (i == 3) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(String.format("%1$s thread is running %2$d", Thread.currentThread().getName(), i));
        }
    }
}

class YieldThread extends Thread {

    private String name;

    public YieldThread(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            if (i == 5) {
                System.out.println(String.format("%1$s 让步", name));
                yield();
            }
            System.out.println(String.format("%1$s 输出 %2$d", name, i));
        }
    }
}

//模拟死锁
class GetLockRunable implements Runnable {
    static final Object ALock = new Object();
    static final Object BLock = new Object();
    private int flag;

    GetLockRunable(int flag) {
        this.flag = flag;
    }

    @Override
    public void run() {
        if (flag == 0) {
//            while (true) {
            synchronized (ALock) {
                System.out.println(String.format("%1$s is hold %2$s", Thread.currentThread().getName(), "A"));
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                synchronized (BLock) {
                    System.out.println(String.format("%1$s is hold %2$s", Thread.currentThread().getName(), "B"));
                }
            }

//                System.out.println(String.format("%1$s is not hold %2$s", Thread.currentThread().getName(), "A"));

//                System.out.println(String.format("%1$s is not hold %2$s", Thread.currentThread().getName(), "B"));
//            }
        } else {
            synchronized (BLock) {
                System.out.println(String.format("%1$s is hold %2$s", Thread.currentThread().getName(), "B"));
                synchronized (ALock) {
                    System.out.println(String.format("%1$s is hold %2$s", Thread.currentThread().getName(), "A"));
                }
            }
//            System.out.println(String.format("%1$s is not hold %2$s", Thread.currentThread().getName(), "B"));
//            try {
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println(String.format("%1$s is not hold %2$s", Thread.currentThread().getName(), "A"));
        }
    }
}

class DataSet {

    private int[] datas = new int[5];

    private int inPos, outPos, count;

    synchronized public void put(int value) {
        while (count == datas.length) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        datas[inPos] = value;
        System.out.println(String.format("Input value : %1$d at %2$d", value, inPos));
        inPos++;
        count++;
        if (inPos == datas.length) {
            inPos = 0;
        }
        notify();
    }

    synchronized public int get() {
        while (count == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int data = datas[outPos];
        System.out.println(String.format("Output value : %1$d at %2$d", data, outPos));
        outPos++;
        count--;
        if (outPos >= datas.length) {
            outPos = 0;
        }
        notify();
        return data;
    }
}

class InputRunnable implements Runnable {
    private DataSet dataSet;

    public InputRunnable(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public void run() {
        int temp = 0;
        while (true) {
            dataSet.put(temp++);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class OutPutRunnable implements Runnable {
    private DataSet dataSet;

    OutPutRunnable(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public void run() {
        while (true) {
            dataSet.get();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}