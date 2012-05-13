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

import java.util.List;
import java.util.Map;

public class LoaderBasedRequestStateWatcher implements RequestStateWatcher, LoaderCallbacks<RequestState> {

    private static final String REQUEST_ID = "request-id";
    
    private static final int OFFSET = 1000;
    
    private Repository<Request> repository;
    private Context context;
    
    private Map<Long, List<Request>> requests;

    private final LoaderManager loaderManager;
    
    public LoaderBasedRequestStateWatcher(Repository<Request> repository, Context context, LoaderManager loaderManager) {
        this.repository = repository;
        this.context = context;
        this.loaderManager = loaderManager;
    }

    public <T extends Request> void attachRequest(T request) {
        List<Request> list = requests.get(request.getState().getId());
        boolean needCreate = false;
        if (list == null) {
            requests.put(request.getState().getId(), list);
            needCreate = true;
        }
        if (needCreate) {
            loaderManager.initLoader(getLoaderId(request), generateArgsFor(request), this);
        }
        list.add(request);
    }
    
    public <T extends Request> void detachRequest(T request) {
        List<Request> list = requests.get(request.getState().getId());
        if (list != null) {
            list.remove(request);
            if (list.isEmpty()) {
                loaderManager.destroyLoader(getLoaderId(request));
            }
        }
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
        return (int)(OFFSET + Math.abs((int)(request.getState().getId() - OFFSET)));
    }
    
    public static <T extends Request> Bundle generateArgsFor(T request) {
        Bundle args = new Bundle();
        args.putLong(REQUEST_ID, request.getState().getId());
        return args;
    }    
}
