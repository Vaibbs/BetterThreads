package org.bthreads.basic;

import java.util.LinkedHashMap;

public class ThreadLooper {
    private static final LinkedHashMap<Thread, Integer> threads = new LinkedHashMap<>();

    private final Integer ownerThreadID;

    private final TaskQueue messages;

    private final Thread killerThread;

    private boolean reuse;

    public ThreadLooper() {
        ownerThreadID = scanAndAssignID();
        messages = new TaskQueue();
        reuse = false;
        killerThread = null;
    }

    public ThreadLooper(Thread killerThread) {
        ownerThreadID = scanAndAssignID();
        messages = new TaskQueue();
        reuse = false;
        this.killerThread = killerThread;
    }

    public void loop() {
        if (!reuse)
            reuse = true;
        else
            throw new RuntimeException("ThreadLooper object can't be reused.");
        for (;;) {
            if (!step())
                break;
        }
        removeThreadID();
    }

    public void quit() {
        if (killerThread != null)
            if (!killerThread.equals(Thread.currentThread()))
                throw new RuntimeException("Only the thread that called MainThread.SetUp() can call this method.");
        messages.endQueue(false);
    }

    public void quitSafely() {
        if (killerThread != null)
            if (!killerThread.equals(Thread.currentThread()))
                throw new RuntimeException("Only the thread that called MainThread.SetUp() can call this method.");
        messages.endQueue(true);
    }

    public Integer getOwnerThreadID() {
        checkThread();
        return ownerThreadID;
    }

    protected TaskQueue getMessages() {
        return messages;
    }

    private boolean step() {
        Runnable run = messages.next();
        if (run == null)
            return false;

        run.run();

        return true;
    }

    private void checkThread() {
        if (!threads.getOrDefault(Thread.currentThread(), -1).equals(ownerThreadID))
            throw new RuntimeException("Only the thread that initialized ThreadLooper can call this method.");
    }

    private void removeThreadID() {
        checkThread();
        threads.remove(Thread.currentThread(), ownerThreadID);
    }

    private Integer scanAndAssignID() {
        Integer thID = 0;
        if (threads.containsKey(Thread.currentThread())) throw new RuntimeException("Only one ThreadLooper may be created per thread.");
        while (threads.containsValue(thID)) thID++;
        threads.put(Thread.currentThread(), thID);
        return thID;
    }
}