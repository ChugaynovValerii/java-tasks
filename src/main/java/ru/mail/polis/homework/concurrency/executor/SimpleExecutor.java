package ru.mail.polis.homework.concurrency.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Нужно сделать свой экзекьютор с линивой инициализацией потоков до какого-то заданного предела.
 * Линивая инициализация означает, что если вам приходит раз в 5 секунд задача, которую вы выполняете 2 секунды,
 * то вы создаете только один поток. Если приходит сразу 2 задачи - то два потока.  То есть, если приходит задача
 * и есть свободный запущенный поток - он берет задачу, если такого нет, то создается новый поток.
 * <p>
 * Задачи должны выполняться в порядке FIFO
 * Потоки после завершения выполнения задачи НЕ умирают, а ждут.
 * <p>
 * Max 10 баллов
 * <p>
 * Напишите 3 теста (2 балла за тест)
 * 1) запуск 1 задачи несколько раз с интервалом (должен создаться только 1 поток)
 * 2) запуск параллельно n - 1 задач несколько раз (должно создаться n - 1 потоков) и задачи должны завершится
 * примерно одновременно
 * 3) запуск параллельно n + m задач несколько раз (должно создаться n потоков) и первые n задач должны завершится
 * примерно одновременно, вторые m задач должны завершиться чуть позже первых n и тоже примерно одновременно
 * Max 6 баллов
 */
public class SimpleExecutor implements Executor {
    private final BlockingQueue<Runnable> tasksQueue = new LinkedBlockingQueue<>();
    private final static int DEFAULT_MAX_THREAD = 20;
    private final int maxThread;
    private final AtomicInteger threadCount = new AtomicInteger(0);
    
    private volatile boolean readyToTake;
    
    public SimpleExecutor() {
        this(DEFAULT_MAX_THREAD);
    }
    
    public SimpleExecutor(int maxThread) {
        this.maxThread = maxThread;
    }
    
    @Override
    public void execute(Runnable command) {
        tasksQueue.add(command);
        while (readyToTake) {
        
        }
        if (!tasksQueue.isEmpty() && threadCount.get() < maxThread) {
            new Thread(this::run).start();
            threadCount.incrementAndGet();
        }
    }
    
    /**
     * Должен возвращать количество созданных потоков.
     */
    public int getLiveThreadsCount() {
        return threadCount.get();
    }
    
    private void run() {
        while (true) {
            try {
                readyToTake = true;
                Runnable command = tasksQueue.take();
                readyToTake = false;
                command.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
