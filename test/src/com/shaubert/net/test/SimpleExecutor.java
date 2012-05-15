package com.shaubert.net.test;

import com.shaubert.net.core.RequestBase;
import com.shaubert.net.nutshell.ExecutionContext;
import com.shaubert.net.nutshell.ExecutorBridge;
import com.shaubert.net.nutshell.Repository;
import com.shaubert.net.nutshell.RequestStatus;

import android.content.Context;

public class SimpleExecutor implements ExecutorBridge, ExecutionContext {

    private final Repository<RequestBase> repository;
    private final Context context;

    public SimpleExecutor(Repository<RequestBase> repository, Context context) {
        this.repository = repository;
        this.context = context;
    }
    
    @Override
    public void queueRequest(long id) {
        RequestBase request = repository.select(id);
        try {
            request.execute(this);
            request.getState().setStatus(RequestStatus.FINISHED);
        } catch (Exception e) {
            request.getState().setStatus(RequestStatus.FINISHED_WITH_ERRORS);
            throw new RuntimeException(e);
        }
        repository.update(request);
    }

    @Override
    public void cancelRequest(long id, boolean interrupt) {
        RequestBase request = repository.select(id);
        request.cancel();
        repository.update(request);
    }

    @Override
    public Context getContext() {
        return context;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Repository<RequestBase> getRepository() {
        return repository;
    }

}
