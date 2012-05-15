package com.shaubert.net.test;

import com.shaubert.net.core.RequestBase;
import com.shaubert.net.core.RequestExecutor;
import com.shaubert.net.nutshell.Repository;

public class NormalExecutor extends RequestExecutor {
    
    public static Repository<RequestBase> repository; 
    
    @Override
    public void onCreate() {
        super.onCreate();
        setRepository(repository);
    }
    
}
