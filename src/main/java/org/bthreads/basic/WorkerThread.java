package org.bthreads.basic;

public class WorkerThread extends Thread {
    private final Object lock;
    private ThreadLooper threadLooper;

    private boolean canAnyoneKill;

    public WorkerThread(String name, boolean canAnyoneKill) {
        super(name);
        lock = new Object();
        this.canAnyoneKill = canAnyoneKill;
    }

    protected void onLoopStart() {
    }

    @Override
    public void run() {
        synchronized (lock) {
            if (canAnyoneKill)
                threadLooper = new ThreadLooper();
            else
                threadLooper = new ThreadLooper(Thread.currentThread());
            lock.notifyAll();
        }
        onLoopStart();
        threadLooper.loop();
    }

    public ThreadLooper getThreadLooper() {
        if (!isAlive()) {
            return null;
        }
        synchronized (lock) {
            while (isAlive() && threadLooper == null) {
                try {
                    lock.wait();
                } catch (InterruptedException ignored) {}
            }
        }
        return threadLooper;
    }

    public boolean quit() {
        ThreadLooper looper = getThreadLooper();
        if (looper != null) {
            looper.quit();
            return true;
        }
        return false;
    }

    public boolean quitSafely() {
        ThreadLooper looper = getThreadLooper();
        if (looper != null) {
            looper.quitSafely();
            return true;
        }
        return false;
    }
}