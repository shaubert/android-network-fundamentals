package com.shaubert.net.test;

import com.shaubert.net.core.DefaultJournal;
import com.shaubert.net.nutshell.RequestStatus;

import android.test.AndroidTestCase;

public class JournalTests extends AndroidTestCase {

    private DefaultJournal journal;
    private SimpleRepository repository;
    private SimpleExecutor executor;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        repository = new SimpleRepository();
        executor = new SimpleExecutor(repository, getContext());
        journal = new DefaultJournal(repository, executor, new SimpleWatcher(repository));
    }
    
    public void testRegistraction() throws Exception {
        SimpleRequest request = new SimpleRequest();
        journal.register(request);
        
        assertEquals(1, request.getState().getId(), 1);
        assertEquals(request, repository.select(request.getState().getId()));
        assertEquals(1, request.executedTimes);
        assertEquals(RequestStatus.FINISHED, request.getState().getStatus());
    }
    
    public void testCancelation() throws Exception {
        SimpleRequest request = new SimpleRequest();
        repository.insert(request);
        journal.cancel(request.getState().getId());
        assertEquals(true, request.isCancelled());
    }
    
    public void testCancelationWithInterruption() throws Exception {
        SimpleRequest request = new SimpleRequest();
        repository.insert(request);
        journal.cancelOrInterrupt(request.getState().getId());
        assertEquals(true, request.isCancelled());
    }
}