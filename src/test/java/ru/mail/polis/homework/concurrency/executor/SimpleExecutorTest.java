package ru.mail.polis.homework.concurrency.executor;

import org.junit.Assert;
import org.junit.Test;


public class SimpleExecutorTest {

    private static class TestTask implements Runnable{
    
        private final int id;
        private final int taskTime;
        public TestTask(int id, int taskTime) {
            this.id = id;
            this.taskTime = taskTime;
        }
    
        @Override
        public void run() {
            try {
                Thread.sleep(taskTime);
                System.out.println("task " + id + " finished");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    @Test
    public void testOneTask() throws InterruptedException {
        SimpleExecutor executor = new SimpleExecutor();
        for (int i = 0; i < 20; i++) {
            TestTask task = new TestTask(i, 300);
            executor.execute(task);
            Thread.sleep(500);
        }
        Assert.assertEquals(1, executor.getLiveThreadsCount());
    }
    
    
    @Test
    public void testNTasks() throws InterruptedException {
        SimpleExecutor executor = new SimpleExecutor(30);
        for (int i = 0; i < 30; i++) {
            TestTask task = new TestTask(i, 5000);
            executor.execute(task);
        }
        Thread.sleep(6000);
        Assert.assertEquals(30, executor.getLiveThreadsCount());
    }
    
    
    @Test
    public void testMoreThanNTasks() throws InterruptedException {
        SimpleExecutor executor = new SimpleExecutor(20);
        for (int i = 0; i < 100; i++) {
            TestTask task = new TestTask(i, 5000);
            executor.execute(task);
        }
        Thread.sleep(30000);
        Assert.assertEquals(20, executor.getLiveThreadsCount());
    }
}