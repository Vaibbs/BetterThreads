package org.bthreads.basic;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Executor {
    public interface Callback {
        void onQueued();
        void onStart();
        void onEnd();
    }

    public static synchronized <T> void handleObjectAs(ThreadLooper threadLooper, Consumer<T> procedure, T msg, ThreadLooper callbackThreadLooper, Callback callback) {
        if (callback != null) {
            callbackThreadLooper.getMessages().getQueue().add(callback::onQueued);
            callbackThreadLooper.getMessages().Unlock();
        }
        threadLooper.getMessages().getQueue().add(() -> {
            if (callback != null) {
                callbackThreadLooper.getMessages().getQueue().add(callback::onStart);
                callbackThreadLooper.getMessages().Unlock();
            }
            procedure.accept(msg);
            if (callback != null) {
                callbackThreadLooper.getMessages().getQueue().add(callback::onEnd);
                callbackThreadLooper.getMessages().Unlock();
            }
        });
        threadLooper.getMessages().Unlock();
    }

    public static synchronized <T> void handleObjectAsAtFrontOfQueue(ThreadLooper threadLooper, Consumer<T> procedure, T msg, ThreadLooper callbackThreadLooper, Callback callback) {
        if (callback != null) {
            callbackThreadLooper.getMessages().getQueue().add(callback::onQueued);
            callbackThreadLooper.getMessages().Unlock();
        }
        threadLooper.getMessages().getQueue().add(0, () -> {
            if (callback != null) {
                callbackThreadLooper.getMessages().getQueue().add(callback::onStart);
                callbackThreadLooper.getMessages().Unlock();
            }
            procedure.accept(msg);
            if (callback != null) {
                callbackThreadLooper.getMessages().getQueue().add(callback::onEnd);
                callbackThreadLooper.getMessages().Unlock();
            }
        });
        threadLooper.getMessages().Unlock();
    }

    public static synchronized <T> void handleObjectAsDelayed(ThreadLooper threadLooper, Consumer<T> procedure, T msg, TimeUnit timeUnit, long delay, ThreadLooper callbackThreadLooper, Callback callback) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            if (callback != null) {
                callbackThreadLooper.getMessages().getQueue().add(callback::onQueued);
                callbackThreadLooper.getMessages().Unlock();
            }
            threadLooper.getMessages().getQueue().add(() -> {
                if (callback != null) {
                    callbackThreadLooper.getMessages().getQueue().add(callback::onStart);
                    callbackThreadLooper.getMessages().Unlock();
                }
                procedure.accept(msg);
                if (callback != null) {
                    callbackThreadLooper.getMessages().getQueue().add(callback::onEnd);
                    callbackThreadLooper.getMessages().Unlock();
                }
            });
            threadLooper.getMessages().Unlock();
        }, delay, timeUnit);
        scheduler.shutdown();
    }

    public static synchronized void executeAs(ThreadLooper threadLooper, Runnable procedure, ThreadLooper callbackThreadLooper, Callback callback) {
        if (callback != null) {
            callbackThreadLooper.getMessages().getQueue().add(callback::onQueued);
            callbackThreadLooper.getMessages().Unlock();
        }
        threadLooper.getMessages().getQueue().add(() -> {
            if (callback != null) {
                callbackThreadLooper.getMessages().getQueue().add(callback::onStart);
                callbackThreadLooper.getMessages().Unlock();
            }
            procedure.run();
            if (callback != null) {
                callbackThreadLooper.getMessages().getQueue().add(callback::onEnd);
                callbackThreadLooper.getMessages().Unlock();
            }
        });
        threadLooper.getMessages().Unlock();
    }

    public static synchronized void executeAsAtFrontOfQueue(ThreadLooper threadLooper, Runnable procedure, ThreadLooper callbackThreadLooper, Callback callback) {
        if (callback != null) {
            callbackThreadLooper.getMessages().getQueue().add(callback::onQueued);
            callbackThreadLooper.getMessages().Unlock();
        }
        threadLooper.getMessages().getQueue().add(0, () -> {
            if (callback != null) {
                callbackThreadLooper.getMessages().getQueue().add(callback::onStart);
                callbackThreadLooper.getMessages().Unlock();
            }
            procedure.run();
            if (callback != null) {
                callbackThreadLooper.getMessages().getQueue().add(callback::onEnd);
                callbackThreadLooper.getMessages().Unlock();
            }
        });
        threadLooper.getMessages().Unlock();
    }

    public static synchronized void executeAsDelayed(ThreadLooper threadLooper, Runnable procedure, TimeUnit timeUnit, long delay, ThreadLooper callbackThreadLooper, Callback callback) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            if (callback != null) {
                callbackThreadLooper.getMessages().getQueue().add(callback::onQueued);
                callbackThreadLooper.getMessages().Unlock();
            }
            threadLooper.getMessages().getQueue().add(() -> {
                if (callback != null) {
                    callbackThreadLooper.getMessages().getQueue().add(callback::onStart);
                    callbackThreadLooper.getMessages().Unlock();
                }
                procedure.run();
                if (callback != null) {
                    callbackThreadLooper.getMessages().getQueue().add(callback::onEnd);
                    callbackThreadLooper.getMessages().Unlock();
                }
            });
            threadLooper.getMessages().Unlock();
        }, delay, timeUnit);
        scheduler.shutdown();
    }
}