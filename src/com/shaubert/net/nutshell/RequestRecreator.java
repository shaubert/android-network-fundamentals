package com.shaubert.net.nutshell;


public interface RequestRecreator {

    Request recreate(String className, RequestState state);
    
}
