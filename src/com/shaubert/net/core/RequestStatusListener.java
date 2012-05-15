package com.shaubert.net.core;

import com.shaubert.net.nutshell.Request;
import com.shaubert.net.nutshell.RequestState;
import com.shaubert.net.nutshell.RequestStateChangeListener;
import com.shaubert.net.nutshell.RequestStatus;

public abstract class RequestStatusListener implements RequestStateChangeListener {

    @Override
    public void onRequestStateChanged(Request request, RequestState oldState, RequestState newState) {
        if (hasChanges(oldState, newState)) {
            processStatus(request, newState.getStatus());
        }
    }

    protected boolean hasChanges(RequestState oldState, RequestState newState) {
        RequestStatus oldStatus = oldState.getStatus();
        RequestStatus newStatus = newState.getStatus();
        return oldStatus != newStatus || newStatus == RequestStatus.PROCESSING;
    }

    private void processStatus(Request request, RequestStatus newStatus) {
        switch (newStatus) {
            case QUEUED:
                onRequestQueued(request);
                break;
            case PROCESSING:
                onRequestProcessing(request, ((RequestStateBase) request.getState()).getProgress());
                break;
            case FINISHED:
                onFinished(request);
                break;
            case FINISHED_WITH_ERRORS:
                onError(request);
                break;
        }
    }
    
    public void processCurrentState(Request request) {
        processStatus(request, request.getState().getStatus());
    }
    
    public void onError(Request request) {
    }

    public void onFinished(Request request) {
    }

    public void onRequestProcessing(Request request, float progress) {
    }

    public void onRequestQueued(Request request) {
    }
    
}