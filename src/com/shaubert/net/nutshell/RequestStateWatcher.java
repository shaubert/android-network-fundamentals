package com.shaubert.net.nutshell;


public interface RequestStateWatcher {

    public <T extends Request> void attachRequest(T request);
    
    public <T extends Request> void detachRequest(T request);
    
}