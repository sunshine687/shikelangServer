package com.sunshine687.shikelang.util;

import java.util.concurrent.CountDownLatch;

public class CountRunnable implements Runnable{
    private String returnStr;
    private CountDownLatch countDownLatch;
    public CountRunnable(CountDownLatch countDownLatch){
        this.countDownLatch = countDownLatch;
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public String getReturnStr() {
        return returnStr;
    }

    public void setReturnStr(String returnStr) {
        this.returnStr = returnStr;
    }

    @Override
    public void run() {
        try{
            synchronized (countDownLatch){
                countDownLatch.countDown();
                returnStr = "" + countDownLatch.getCount();
                System.out.println("threadName: " + Thread.currentThread().getName() +" thread counts = " + countDownLatch.getCount());
            }
            countDownLatch.await();
            returnStr = "***" + returnStr;
            System.out.println("coucurrency countd = " + (100 - countDownLatch.getCount()));
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
