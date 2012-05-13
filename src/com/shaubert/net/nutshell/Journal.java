package com.shaubert.net.nutshell;

public interface Journal<T extends Request> {

    void register(T request);
    
    T getRequest(long requestId);
 
    void cancel(long requestId);
    
}
