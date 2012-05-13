package com.shaubert.net.core;

import com.shaubert.net.nutshell.Repository;
import com.shaubert.net.nutshell.Request;
import com.shaubert.net.nutshell.RequestState;

import android.content.Context;
import android.database.ContentObserver;
import android.support.v4.content.AsyncTaskLoader;

public class RequestStateLoader extends AsyncTaskLoader<RequestState> {

    private RequestState loadedState;
    private final Repository<Request> repository;
    private final long requestId;
    
    private ContentObserver observer;
    private boolean registered;
    
    public RequestStateLoader(Context context, Repository<Request> repository, long requestId) {
        super(context);
        this.repository = repository;
        this.requestId = requestId;
        this.observer = new ForceLoadContentObserver();
    }
    
    @Override
    public RequestState loadInBackground() {
        if (!registered) {
            repository.registerObserver(requestId, observer);
            registered = true;
        }
        return repository.select(requestId).getState();
    }
    
    @Override
    protected void onStartLoading() {
        if (loadedState != null) {
            deliverResult(loadedState);
        }
        if (takeContentChanged() || loadedState == null) {
            forceLoad();
        }
    }

    /* Runs on the UI thread */
    @Override
    public void deliverResult(RequestState state) {
        if (isReset()) {
            return;
        }
        
        loadedState = state;
        
        if (isStarted()) {
            super.deliverResult(state);
        }
    }
    
    /**
     * Must be called from the UI thread
     */
    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(RequestState data) {
        repository.unregisterObserver(observer);
        registered = false;
    }
    
    @Override
    protected void onReset() {
        super.onReset();
        
        // Ensure the loader is stopped
        onStopLoading();
        
        repository.unregisterObserver(observer);
        registered = true;
        loadedState = null;
    }

}