package com.shaubert.net.test;

import com.shaubert.net.core.RequestBase;
import com.shaubert.net.nutshell.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleRepository implements Repository<RequestBase> {

    public Map<Long, RequestBase> requests = new HashMap<Long, RequestBase>();
    
    @Override
    public RequestBase select(long id) {
        return requests.get(id);
    }

    @Override
    public void insert(RequestBase entity) {
        long id = requests.size() + 1;
        requests.put(id, entity);
        entity.getState().setId(id);
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
    }

}
