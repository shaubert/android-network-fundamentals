package com.shaubert.net.nutshell;

public interface Journal {

    void register(Request request);
    
    Request getRequest(long requestId);
 
    void cancel(long requestId);
    
    void cancelOrInterrupt(long requestId);
    
    void registerForUpdates(Request request);
    
    void unregisterForUpdates(Request request);
    
}
