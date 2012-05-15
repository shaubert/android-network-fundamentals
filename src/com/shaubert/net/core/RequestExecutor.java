package com.shaubert.net.core;

import com.shaubert.net.nutshell.ExecutionContext;
import com.shaubert.net.nutshell.Repository;
import com.shaubert.net.nutshell.Request;
import com.shaubert.net.nutshell.RequestStatus;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class RequestExecutor extends Service implements ExecutionContext {
       
    public static final String EXTRA_ID = "_request_id_";
    public static final String EXTRA_CANCEL = "_cancel_";
    public static final String EXTRA_INTERRUPT = "_interrupt_";
    
    private volatile Looper serviceLooper;
    private volatile ServiceHandler serviceHandler;
    private boolean redelivery;
    private Repository<RequestBase> repository;
    
    private Object locker = new Object();
    private List<Long> cancelledIds = new ArrayList<Long>();
    private RequestBase executing;
    
    public void setRepository(Repository<RequestBase> repository) {
        this.repository = repository;
    }
    
    /**
     * Control redelivery of intents.  If called with true,
     * {@link #onStartCommand(Intent, int, int)} will return
     * {@link Service#START_REDELIVER_INTENT} instead of
     * {@link Service#START_NOT_STICKY}, so that if this service's process
     * is called while it is executing the Intent in
     * {@link #onHandleIntent(Intent)}, then when later restarted the same Intent
     * will be re-delivered to it, to retry its execution.
     */
    public void setIntentRedelivery(boolean enabled) {
        redelivery = enabled;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Request> Repository<T> getRepository() {
        return (Repository<T>)repository;
    }
    
    @Override
    public Context getContext() {
        return this;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread(getClass().getSimpleName());
        thread.start();

        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStart(intent, startId);
        return redelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
    }
    
    @Override
    public void onDestroy() {
        serviceLooper.quit();
    }
    
    @Override
    public void onStart(Intent intent, int startId) {
        long id = getIdOrThrowIfNotExists(intent);
        RequestBase request = getRequest(id);
        synchronized (locker) {
            boolean cancel = hasCancelExtra(intent);
            if (cancel) {
                cancelledIds.add(id);
                cancel(request, shouldInterrupt(intent));
            } else {
                request.getState().setStatus(RequestStatus.QUEUED);
                repository.update(request);
            }
        }
        
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        serviceHandler.sendMessage(msg);
    }

    public RequestBase getRequest(long requestId) {
        return repository.select(requestId);
    }
    
    private boolean hasCancelExtra(Intent intent) {
        return intent.getBooleanExtra(EXTRA_CANCEL, false);
    }
    
    private boolean shouldInterrupt(Intent intent) {
        return intent.getBooleanExtra(EXTRA_INTERRUPT, false);
    }

    private long getIdOrThrowIfNotExists(Intent intent) {
        long id = intent.getLongExtra(EXTRA_ID, -1);
        if (id == -1) {
            throw new IllegalArgumentException("request without id");
        }
        return id;
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        
        @Override
        public void handleMessage(Message msg) {
            Intent intent = (Intent)msg.obj;
            if (!hasCancelExtra(intent)) {
                long id = getIdOrThrowIfNotExists(intent);
                boolean cancelled = false;
                synchronized (locker) {
                    if (cancelledIds.contains(id)) {
                        cancelled = true;
                    } else {
                        executing = getRequest(id);
                    }
                }
                if (!cancelled) {
                    if (!executing.isCancelled()) {
                        execute(executing);
                        synchronized (locker) {
                            executing = null;
                        }
                    }
                }
            }
            stopSelf(msg.arg1);
        }
    }

    protected void execute(RequestBase request) {
        request.getState().setStatus(RequestStatus.PROCESSING);
        repository.update(request);

        RequestStatus status = RequestStatus.FINISHED;
        try {
            request.execute(this);
        } catch (Exception e) {
            Log.w(getClass().getSimpleName(), null, e);
            status = RequestStatus.FINISHED_WITH_ERRORS;
        }
        
        if (!request.isCancelled()) {
            request.getState().setStatus(status);
            repository.update(request);
        }
    }
    
    protected void cancel(RequestBase request, boolean interrupt) {
        synchronized (locker) {
            if (executing != null && executing.getState().getId() == request.getState().getId()) {
                executing.cancel();
                if (interrupt) {
                    serviceLooper.getThread().interrupt();
                }
            }
        }
        request.cancel();
        repository.update(request);
    }
    
}