package com.shaubert.net.core;

import com.shaubert.net.nutshell.ExecutorBridge;
import com.shaubert.net.nutshell.Journal;
import com.shaubert.net.nutshell.Repository;
import com.shaubert.net.nutshell.RequestStatus;

public class DefaultJournal implements Journal<RequestBase> {

    private Repository<RequestBase> repository;
    private ExecutorBridge executorBridge;
    
    public DefaultJournal(Repository<RequestBase> repository, ExecutorBridge executorBridge) {
        this.repository = repository;
        this.executorBridge = executorBridge;
    }

    @Override
    public void register(RequestBase request) {
        request.getState().setStatus(RequestStatus.NOT_STARTED);
        repository.insert(request);
        executorBridge.queueRequest(request.getState().getId());
    };

    @Override
    public RequestBase getRequest(long requestId) {
        return repository.select(requestId);
    }
    
    @Override
    public void cancel(long requestId) {
        executorBridge.cancelRequest(requestId);
    }

}