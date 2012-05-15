package com.shaubert.net.test;

import com.shaubert.net.nutshell.Repository;
import com.shaubert.net.nutshell.Request;
import com.shaubert.net.nutshell.RequestStateWatcher;

import android.database.ContentObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleWatcher implements RequestStateWatcher {

    public Repository<? extends Request> repository;
    
    private Map<Long, List<Request>> requests = new HashMap<Long, List<Request>>();
    private Map<Long, ContentObserver> observers = new HashMap<Long, ContentObserver>();
    
    public SimpleWatcher(Repository<? extends Request> repository) {
        this.repository = repository;
    }
    
    @Override
    public void attachRequest(final Request request) {
        List<Request> list = requests.get(request.getState().getId());
        if (list == null) {
            list = new ArrayList<Request>();
            requests.put(request.getState().getId(), list);
        }
        list.add(request);
        
        ContentObserver observer = observers.get(request.getState().getId());
        if (observer == null) {
            final long id = request.getState().getId();
            observer = new ContentObserver(null) {
                @Override
                public void onChange(boolean selfChange) {
                    for (Request request : requests.get(id)) {
                        request.setState(repository.select(request.getState().getId()).getState());
                    }
                }
            };
            repository.registerObserver(request.getState().getId(), observer);
        }

    }
    
    @Override
    public void detachRequest(Request request) {
        List<Request> list = requests.get(request.getState().getId());
        if (list != null) {
            list.remove(request);
            if (list.isEmpty()) {
                ContentObserver observer = observers.get(request.getState().getId());
                repository.unregisterObserver(observer);
            }
        }
    }
    
}
