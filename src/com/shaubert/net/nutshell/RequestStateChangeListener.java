package com.shaubert.net.nutshell;


public interface RequestStateChangeListener {

    void onRequestStateChanged(Request request, RequestState oldState, RequestState newState);
    
}
