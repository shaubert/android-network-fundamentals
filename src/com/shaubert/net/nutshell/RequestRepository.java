package com.shaubert.net.nutshell;

public interface RequestRepository<T extends Request> extends Repository<T> {

    RequestState selectState(long requestId);
    
}
