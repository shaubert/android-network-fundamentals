package com.shaubert.net.test;

import com.shaubert.net.core.RequestBase;
import com.shaubert.net.nutshell.RequestRepository;
import com.shaubert.net.nutshell.RequestState;

import android.database.ContentObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleRepository implements RequestRepository<RequestBase> {

    public Map<Long, RequestBase> requests = new HashMap<Long, RequestBase>();
    public Map<Long, List<ContentObserver>> observers = new HashMap<Long, List<ContentObserver>>();
    
    @Override
    public RequestBase select(long id) {
        return requests.get(id);
    }

    @Override
    public RequestState selectState(long requestId) {
        RequestBase request = select(requestId);
        if (request != null) {
            return request.getState();
        } else {
            return null;
        }
    }
    
    @Override
    public void insert(RequestBase entity) {
        long id = requests.size() + 1;
        requests.put(id, entity);
        entity.getState().setId(id);
        
        notifyObservers(entity.getState().getId());
        notifyObservers(-1);
    }

    @Override
    public void insert(List<RequestBase> entities) {
        for (RequestBase request : entities) {
            insert(request);
        }
    }

    @Override
    public void update(RequestBase entity) {
        requests.put(entity.getState().getId(), entity);
        notifyObservers(entity.getState().getId());
        notifyObservers(-1);
    }

    @Override
    public void update(List<RequestBase> entities) {
        for (RequestBase request : entities) {
            update(request);
        }
    }

    @Override
    public void delete(RequestBase entity) {
        requests.remove(entity.getState().getId());
        notifyObservers(entity.getState().getId());
        notifyObservers(-1);
    }

    @Override
    public void delete(List<RequestBase> entities) {
        for (RequestBase request : entities) {
            delete(request);
        }
    }

    @Override
    public void deleteAll() {
        requests.clear();
        notifyAll();
    }

    @Override
    public void registerObserver(ContentObserver observer) {
        putObserver(-1, observer);
    }

    @Override
    public void registerObserver(long entityId, ContentObserver observer) {
        putObserver(entityId, observer);
    }

    public void notifyObservers(long id) {
        for (ContentObserver observer : getObservers(id)) {
            observer.dispatchChange(false);
        }
    }
    
    public void notifyAllObservers() {
        for (List<ContentObserver> list : observers.values()) {
            for (ContentObserver observer : list) {
                observer.dispatchChange(false);
            }
        } 
    }
    
    @Override
    public void unregisterObserver(ContentObserver observer) {
        for (List<ContentObserver> list : observers.values()) {
            list.remove(observer); 
        }
    }

    public void putObserver(long id, ContentObserver observer) {
        List<ContentObserver> list = observers.get(id);
        if (list == null) {
            list = new ArrayList<ContentObserver>();
            observers.put(id, list);
        }
        list.add(observer);
    }
    
    public List<ContentObserver> getObservers(long id) {
        List<ContentObserver> list =  observers.get(id);
        if (list == null) {
            list = new ArrayList<ContentObserver>();
            observers.put(id, list);
        }
        return list;
    }
    
}
