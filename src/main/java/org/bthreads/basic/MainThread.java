package org.bthreads.basic;

public abstract class MainThread {
    private static final Object lock = new Object();

    private static ThreadLooper threadLooper;

    private static Thread mThread;

    private static boolean canAnyoneKill;

    public static void SetUp(boolean canAnyoneKill) {
        mThread = Thread.currentThread();
        MainThread.canAnyoneKill = canAnyoneKill;
        synchronized (lock) {
            if (MainThread.canAnyoneKill)
                threadLooper = new ThreadLooper();
            else
                threadLooper = new ThreadLooper(mThread);
            lock.notifyAll();
        }
    }

    public static void StartLoop() {
        checkThread();
        getThreadLooper().loop();
    }

    public static void quit() {
        if (!MainThread.canAnyoneKill)
            checkThread();
        threadLooper.quit();
    }

    public static void quitSafely() {
        if (!MainThread.canAnyoneKill)
            checkThread();
        threadLooper.quitSafely();
    }

    public static ThreadLooper getThreadLooper() {
        if (!mThread.isAlive()) {
            return null;
        }
        synchronized (lock) {
            while (mThread.isAlive() && threadLooper == null) {
                try {
                    lock.wait();
                } catch (InterruptedException ignored) {}
            }
        }
        return threadLooper;
    }

    private static void checkThread() {
        if (mThread == null)
            throw new RuntimeException("MainThread.SetUp() should be called");
        if (!Thread.currentThread().equals(mThread))
            throw new RuntimeException("Only the thread that called MainThread.SetUp() can call this method.");
    }
}