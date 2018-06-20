package com.bingo.common.thread;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Administrator on 2018-04-09.
 */
public class MyThreadPool {
    public int poolSize = 1;//并发线程数
    public int waitSize = 0;//柱塞线程数，用于阻塞线程执行 <=0 代表所有线程执行都不阻塞。

    private ThreadPoolExecutor pool;//线程池

    //实现一个线程池策略，所有任务放入队列中，等待执行
    RejectedExecutionHandler handler = new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (!executor.isShutdown()){
                try {
                    executor.getQueue().put(r);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public MyThreadPool(int poolSize){
        this.poolSize = poolSize;
        init();
    }

    public MyThreadPool(int poolSize,int waitSize){
        this.poolSize = poolSize;
        this.waitSize = waitSize;
        init();
    }

    private void init(){
        if(waitSize<=0){
            //实现无限队列
            BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
            pool = new ThreadPoolExecutor(poolSize, poolSize, 0, TimeUnit.SECONDS, queue, handler);
        }else{
            //实现阻塞队列
            BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(waitSize);
            pool = new ThreadPoolExecutor(poolSize, poolSize, 0, TimeUnit.SECONDS, queue, handler);
        }
    }

    public void execute(Runnable command) {
        pool.execute(command);

    }

    public void shutdown() {
        pool.shutdown();
    }

    public static void main(String arg[]){
        long currTime = System.currentTimeMillis();

        MyThreadPool pool = new MyThreadPool(2,20);


        for (int i = 0; i < 20; i ++){
            final int temp = i;
            pool.execute(() -> {
                String name = Thread.currentThread().getName();
                System.out.println(DateTimeFormatter.ofPattern("mm:ss").format(LocalDateTime.now())+name + "客户" + temp + "来了.......");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        pool.shutdown();
        System.out.println("shutdown");

    }

}
