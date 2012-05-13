package com.shaubert.net.core;

import com.shaubert.net.nutshell.Request;
import com.shaubert.net.nutshell.RequestStateChangeListener;

public abstract class RequestBase implements Request {

    private RequestStateBase state;
    private RequestStateChangeListener<RequestBase> changeListener;
    
    public RequestBase(RequestStateBase state) {
        this.state = state != null ? state : new RequestStateBase();
    }
    
    public void setState(RequestStateBase state) {
        this.state = state;
        notifyChangeListener();
    }

    private void notifyChangeListener() {
        if (changeListener != null) {
            changeListener.onRequestStateChanged(this);
        }
    }
    
    @SuppressWarnings("unchecked")
    public <T extends RequestBase> void setFullStateChangeListener(RequestStateChangeListener<T> changeListener) {
        this.changeListener = (RequestStateChangeListener<RequestBase>)changeListener;
    }
    
    @Override
    public RequestStateBase getState() {
        return state;
    }

    @Override
    public boolean isCancelled() {
        return state.isCancelled();
    }
    
    @Override
    public void cancel() {
        state.setCancelled();
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + getState().toString();
    }
    
}