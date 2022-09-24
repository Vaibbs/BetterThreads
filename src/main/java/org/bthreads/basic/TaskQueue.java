package org.bthreads.basic;

import java.util.ArrayList;

public class TaskQueue {
    private final Object lock;

    private final ArrayList<Runnable> queue;

    private boolean End;
    private boolean safe;

    public TaskQueue() {
        queue = new ArrayList<>();
        lock = new Object();
        End = false;
        safe = false;
    }

    public Runnable next() {
        synchronized (lock) {
            while (Thread.currentThread().isAlive() && queue.isEmpty() && !End) {
                try {
                    lock.wait();
                } catch (InterruptedException ignored) {}
            }
        }
        if (End && !safe)
            return null;
        else if (End && queue.isEmpty())
            return null;
        return removeAndGetFirst();
    }

    public ArrayList<Runnable> getQueue() {
        if (End && safe)
            return null;
        return queue;
    }

    public void Unlock() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public void endQueue(boolean b) {
        End = true;
        safe = b;
        Unlock();
    }

    private synchronized Runnable removeAndGetFirst() {
        Runnable runnable = queue.get(0);
        queue.remove(0);
        return runnable;
    }
}