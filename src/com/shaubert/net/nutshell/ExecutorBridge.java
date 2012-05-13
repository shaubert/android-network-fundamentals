package com.shaubert.net.nutshell;

public interface ExecutorBridge {

    void queueRequest(long id);
    
    void cancelRequest(long id);
    
}
