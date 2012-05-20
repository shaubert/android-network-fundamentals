package com.shaubert.net.core;

import com.shaubert.net.nutshell.Repository;
import com.shaubert.net.nutshell.Request;
import com.shaubert.net.nutshell.RequestState;
import com.shaubert.net.nutshell.RequestStateWatcher;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoaderBasedRequestStateWatcher implements RequestStateWatcher, LoaderCallbacks<RequestState> {

    private static final String REQUEST_ID = "request-id";
    
    public static final int OFFSET = 1000;
    
    private Repository<Request> repository;
    private Context context;
    
    private Map<Long, List<Request>> requests;

    private final LoaderManager loaderManager;
    
    @SuppressWarnings("unchecked")
    public LoaderBasedRequestStateWatcher(Repository<? extends Request> repository, Context context, LoaderManager loaderManager) {
        this.repository = (Repository<Request>)repository;
        this.context = context;
        this.loaderManager = loaderManager;
        this.requests = new HashMap<Long, List<Request>>();
    }

    @Override
    public void attachRequest(Request request) {
        List<Request> list = requests.get(request.getState().getId());
        boolean needCreate = false;
        if (list == null) {
            list = new ArrayList<Request>();
            requests.put(request.getState().getId(), list);
            needCreate = true;
        }
        if (needCreate) {
            loaderManager.initLoader(getLoaderId(request), generateArgsFor(request), this);
        }
        list.add(request);
    }
    
    @Override
    public void detachRequest(Request request) {
        List<Request> list = requests.get(request.getState().getId());
        if (list != null) {
            list.remove(request);
        }
        loaderManager.destroyLoader(getLoaderId(request));
    }
    
    @Override
    public Loader<RequestState> onCreateLoader(int id, Bundle args) {
        long requestId = args.getLong(REQUEST_ID);
        return new RequestStateLoader(context, repository, requestId);
    }

    @Override
    public void onLoadFinished(Loader<RequestState> loader, RequestState data) {
        List<Request> list = requests.get(data.getId());
        if (list != null) {
            for (Request request : list) {
                request.setState(data);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<RequestState> loader) {
    }

    public static <T extends Request> int getLoaderId(T request) {
        int id =  Math.abs((int)(request.getState().getId()));
        if (id >= OFFSET) {
            return id;
        } else {
            id = Math.abs(id + OFFSET);
            if (id > OFFSET) {
                return id;
            } else {
                return OFFSET + id;
            }
        }
    }
    
    public static <T extends Request> Bundle generateArgsFor(T request) {
        Bundle args = new Bundle();
        args.putLong(REQUEST_ID, request.getState().getId());
        return args;
    }    
}
