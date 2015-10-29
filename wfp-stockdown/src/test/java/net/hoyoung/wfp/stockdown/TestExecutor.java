package net.hoyoung.wfp.stockdown;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by Administrator on 2015/10/29.
 */
public class TestExecutor {
    @Test
    public void test(){
        Executor exe = Executors.newFixedThreadPool(2);
        exe.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("thread 0");
            }
        });
        exe.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("thread 1");
            }
        });
    }
    @Test
    public void test2() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Callable<List<Double>> task = new Callable<List<Double>>() {
            @Override
            public List<Double> call() throws Exception {
                ArrayList<Double> result = new ArrayList<Double>();
                for (int i = 0; i < 10; i++) {
                    result.add(Math.random());
                    Thread.sleep(200);
                }
                return result;
            }
        };
        Future<List<Double>> future = executorService.submit(task);
        Thread.sleep(1000);
        System.out.println("=======================");
        try {
            for (Double d : future.get()){
                System.out.println(d);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }
    @Test
    public void test3(){
        ExecutorService threadPool = Executors.newCachedThreadPool();
        ExecutorCompletionService<Integer> completionService = new ExecutorCompletionService<Integer>(threadPool);

        int count = 3;
        for (int i = 0; i < count; i++) {
            final int finalI = i;
            completionService.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    Thread.sleep(1000* finalI);
                    return new Random().nextInt(100);
                }
            });
        }
        for (int i = 0; i < count; i++) {
            try {
                Future<Integer> future = completionService.take();
                System.out.println(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
    }
}
