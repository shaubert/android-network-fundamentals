package com.shaubert.net.test;

import com.shaubert.net.core.DefaultExecutorBridge;
import com.shaubert.net.core.DefaultJournal;
import com.shaubert.net.nutshell.RequestStatus;

import android.content.ComponentName;
import android.content.ContextWrapper;
import android.content.Intent;
import android.test.ServiceTestCase;

import java.util.Random;

public class ExecutorTests extends ServiceTestCase<NormalExecutor> {

    public static class MyContex extends ContextWrapper {

        private final ExecutorTests base;
        private boolean started;

        public MyContex(ExecutorTests base) {
            super(base.getContext());
            this.base = base;
        }
        
        @Override
        public ComponentName startService(Intent service) {
            if (started) {
                base.getService().onStart(service, new Random().nextInt());
            } else {
                base.startService(service);
                started = true;
            }
            return null;
        }
    }
    
    private DefaultJournal journal;
    private SimpleRepository repository;
    
    public ExecutorTests() {
        super(NormalExecutor.class);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        repository = new SimpleRepository();
        NormalExecutor.repository = repository;
        journal = new DefaultJournal(repository, new DefaultExecutorBridge(new MyContex(this), NormalExecutor.class));
    }

    public void testExecution() throws Exception {
        SimpleRequest request = new SimpleRequest();
        journal.register(request);
        request.waitForExecute();
        assertEquals(1, request.executedTimes);
    }
    
    public void testMultipleExecution() throws Exception {
        SimpleRequest request1 = new SimpleRequest();
        SimpleRequest request2 = new SimpleRequest();
        SimpleRequest request3 = new SimpleRequest();
        
        journal.register(request1);
        journal.register(request2);
        journal.register(request3);
        
        request1.waitForExecute();
        request2.waitForExecute();
        request3.waitForExecute();
        
        assertEquals(1, request1.executedTimes);
        assertEquals(1, request2.executedTimes);
        assertEquals(1, request3.executedTimes);
    }
    
    public void testExecutionOrder() throws Exception {
        SimpleRequest request1 = new SimpleRequest().enableShortSleep();
        SimpleRequest request2 = new SimpleRequest().enableShortSleep();
        SimpleRequest request3 = new SimpleRequest().enableShortSleep();
        
        journal.register(request1);
        journal.register(request2);
        journal.register(request3);
        
        request1.waitForExecute();
        request2.waitForExecute();
        request3.waitForExecute();
        
        assertEquals(true, request1.executionTime < request2.executionTime);
        assertEquals(true, request2.executionTime < request3.executionTime);
    }
    
    public void testCancellation() throws Exception {
        SimpleRequest request1 = new SimpleRequest().enableLongSleep();
        SimpleRequest request2 = new SimpleRequest();
        
        journal.register(request1);
        journal.register(request2);
        journal.cancel(request2.getState().getId());
        
        request1.waitForExecute();
        request2.waitForCancel();
        
        assertEquals(1, request1.executedTimes);
        assertEquals(0, request2.executedTimes);
    }
    
    public void testRequestFailure() throws Exception {
        SimpleRequest request1 = new SimpleRequest().addBomb();
        SimpleRequest request2 = new SimpleRequest();
        
        journal.register(request1);
        journal.register(request2);
        
        request2.waitForExecute();
        assertEquals(0, request1.executedTimes);
        assertEquals(RequestStatus.FINISHED_WITH_ERRORS, request1.getState().getStatus());
    }
}
