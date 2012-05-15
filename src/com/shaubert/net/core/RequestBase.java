package com.shaubert.net.core;

import com.shaubert.net.nutshell.Request;
import com.shaubert.net.nutshell.RequestState;
import com.shaubert.net.nutshell.RequestStateChangeListener;

public abstract class RequestBase implements Request {

    private RequestStateBase state;
    private RequestStateChangeListener changeListener;
    
    public RequestBase(RequestStateBase state) {
        this.state = state != null ? state : new RequestStateBase();
    }

    @Override
    public void setState(RequestState state) {
        RequestStateBase oldState = this.state;
        this.state = (RequestStateBase)state;
        notifyChangeListener(oldState);
    };
    
    private void notifyChangeListener(RequestStateBase oldState) {
        if (changeListener != null) {
            changeListener.onRequestStateChanged(this, oldState, getState());
        }
    }
    
    public void setFullStateChangeListener(RequestStateChangeListener changeListener) {
        this.changeListener = changeListener;
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