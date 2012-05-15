package com.shaubert.net.nutshell;


public interface RequestStateWatcher {

    void attachRequest(Request request);
    
    void detachRequest(Request request);
    
}