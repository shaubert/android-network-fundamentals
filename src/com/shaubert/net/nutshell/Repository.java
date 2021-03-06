package com.shaubert.net.nutshell;

import android.database.ContentObserver;

import java.util.List;


public interface Repository<T> {

    T select(long id);
    
    void insert(T entity);
    
    void insert(List<T> entities);
    
    void update(T entity);
    
    void update(List<T> entities);
    
    void delete(T entity);
    
    void delete(List<T> entities);
    
    void deleteAll();
    
    void registerObserver(ContentObserver observer);
    
    void registerObserver(long entityId, ContentObserver observer);
    
    void unregisterObserver(ContentObserver observer);
}