package com.shaubert.net.nutshell;


import android.content.Context;

public interface ExecutionContext {

    Context getContext();
    
    <R extends Request, T extends Repository<R>> T getRepository();
    
}
