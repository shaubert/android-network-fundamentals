package com.shaubert.net.core;

import com.shaubert.net.nutshell.ExecutorBridge;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

public class DefaultExecutorBridge implements ExecutorBridge {

    private final Context context;
    private final Class<?> executorClass;

    public <T extends Service> DefaultExecutorBridge(Context context, Class<T> executorClass) {
        this.context = context;
        this.executorClass = executorClass;
    }
    
    @Override
    public void queueRequest(long id) {
        Intent intent = new Intent(context, executorClass);
        intent.putExtra(RequestExecutor.EXTRA_ID, id);
        context.startService(intent);
    }

    @Override
    public void cancelRequest(long id, boolean interrupt) {
        Intent intent = new Intent(context, executorClass);
        intent.putExtra(RequestExecutor.EXTRA_ID, id);
        intent.putExtra(RequestExecutor.EXTRA_CANCEL, true);
        intent.putExtra(RequestExecutor.EXTRA_INTERRUPT, interrupt);
        context.startService(intent);
    }
    
}