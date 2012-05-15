package com.shaubert.net.test;

import com.shaubert.net.core.RequestBase;
import com.shaubert.net.core.RequestStateBase;
import com.shaubert.net.nutshell.ExecutionContext;

import android.os.SystemClock;

import junit.framework.Assert;

public class SimpleRequest extends RequestBase {
    public int executedTimes;
    public long executionTime;
    
    private Object executionWaiter = new Object();
    private Object cancelWaiter = new Object();
    private boolean sleep;
    private int millis;
    private boolean bomb;
    
    public boolean interrupted;
    
    public SimpleRequest() {
        this(null);
    }
    
    public SimpleRequest(RequestStateBase state) {
        super(state);
    }
    
    @Override
    public void execute(ExecutionContext executionContext) throws Exception {
        Assert.assertNotNull(executionContext);
        Assert.assertNotNull(executionContext.getContext());
        Assert.assertNotNull(executionContext.getRepository());
        
        if (sleep) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException exception) {
                interrupted = true;
                throw exception;
            }
        }
        
        if (bomb) {
            throw new IllegalStateException("BOOM!");
        }
        
        executedTimes++;
        
        synchronized (executionWaiter) {
            executionTime = SystemClock.uptimeMillis();
            executionWaiter.notifyAll();
        }
    }

    public SimpleRequest enableShortSleep() {
        sleep = true;
        millis = 20;
        return this;
    }
    
    public SimpleRequest enableSleep(int millis) {
        sleep = true;
        this.millis = millis;
        return this;
    }
    
    public SimpleRequest enableLongSleep() {
        sleep = true;
        millis = 100;
        return this;
    }
    
    public SimpleRequest addBomb() {
        this.bomb = true;
        return this;
    }
    
    public void waitForExecute() {
        synchronized (executionWaiter) {
            if (executedTimes > 0) {
                return;
            }
            try {
                executionWaiter.wait(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    @Override
    public void cancel() {
        super.cancel();
        synchronized (cancelWaiter) {
            cancelWaiter.notifyAll();
        }
    }
    
    public void waitForCancel() {
        synchronized (cancelWaiter) {
            if (isCancelled()) {
                return;
            }
            try {
                executionWaiter.wait(1000);
                if (!isCancelled()) {
                    throw new RuntimeException();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}