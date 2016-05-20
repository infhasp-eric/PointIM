package com.pointim.utils;

import java.util.LinkedList;

/**
 * 任务队列管理器
 * Created by Eric
 * on 2016/5/20
 * for project PointIM
 */
public class WorkQueue {
    private final int nThreads;//线程池的大小
    private final PoolWorker[] threads;//用数组实现线程池
    private final LinkedList queue;//任务队列

    public WorkQueue(int nThreads) {
        this.nThreads = nThreads;
        queue = new LinkedList();
        threads = new PoolWorker[nThreads];
        for (int i = 0;
             i < nThreads;
             i++) {
            threads[i] = new PoolWorker();
            threads[i].start();//启动所有工作线程
        }
    }

    //添加任务
    public void execute(Runnable r)
    {//执行任务
        synchronized (queue) {
            queue.addLast(r);
            queue.notify();
        }
    }

    private class PoolWorker extends Thread {//工作线程类
        public void run() {
            Runnable r;
            while (true) {
                synchronized (queue) {
                    while (queue.isEmpty()) {//如果任务队列中没有任务，等待
                        try {
                            queue.wait();
                        } catch (InterruptedException ignored) {
                            ignored.printStackTrace();
                        }
                    }
                    r = (Runnable) queue.removeFirst();//有任务时，取出任务
                }
                try {
                    r.run();//执行任务
                } catch (RuntimeException e) {
                    // You might want to log something here
                    e.printStackTrace();
                }
            }
        }
    }
}
