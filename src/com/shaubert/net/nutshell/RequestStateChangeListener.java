package com.shaubert.net.nutshell;


public interface RequestStateChangeListener<T extends Request> {

    void onRequestStateChanged(T request);
    
}
