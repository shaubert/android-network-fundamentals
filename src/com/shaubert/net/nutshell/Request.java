package com.shaubert.net.nutshell;


public interface Request {

    RequestState getState();
    
    void execute(ExecutionContext executionContext) throws Exception;
    
    boolean isCancelled();
    
    void cancel();
    
}
