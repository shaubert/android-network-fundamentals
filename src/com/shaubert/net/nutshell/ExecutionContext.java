package com.shaubert.net.nutshell;


import android.content.Context;

public interface ExecutionContext {

    Context getContext();
    
    <T extends Request> Repository<T> getRepository();
    
}
