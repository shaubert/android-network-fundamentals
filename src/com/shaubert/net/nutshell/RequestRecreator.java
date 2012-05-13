package com.shaubert.net.nutshell;


public interface RequestRecreator<T extends Request, S extends RequestState> {

    T recreate(String className, S state);
    
}
