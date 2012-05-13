package com.shaubert.net.core;

import com.shaubert.net.nutshell.Request;

public abstract class RequestBase implements Request {

    private RequestStateBase state;
    
    public RequestBase(RequestStateBase state) {
        this.state = state != null ? state : new RequestStateBase();
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
