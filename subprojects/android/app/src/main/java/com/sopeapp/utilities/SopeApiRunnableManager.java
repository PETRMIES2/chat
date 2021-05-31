package com.sopeapp.utilities;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SopeApiRunnableManager {

    static SopeApiRunnableManager messageManager;

    public static SopeApiRunnableManager getInstance() {
        if (messageManager == null) {
            messageManager = new SopeApiRunnableManager();

        }
        return messageManager;
    }


    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static final int KEEP_ALIVE_TIME = 5;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private final ThreadPoolExecutor messageThreadPool;
    private final BlockingQueue<Runnable> messageWorkQueue;

    private SopeApiRunnableManager() {
        messageWorkQueue = new LinkedBlockingQueue<Runnable>();
        messageThreadPool = new ThreadPoolExecutor(
                NUMBER_OF_CORES,       // Initial pool size
                NUMBER_OF_CORES,       // Max pool size
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                messageWorkQueue);
    }


    public static void execute(Runnable runnable) {
        messageManager.messageThreadPool.execute(runnable);
    }

}
