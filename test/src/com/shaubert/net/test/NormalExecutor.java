package com.shaubert.net.test;

import com.shaubert.net.core.RequestBase;
import com.shaubert.net.core.RequestExecutor;
import com.shaubert.net.nutshell.ExecutorBridge;
import com.shaubert.net.nutshell.Repository;

import android.content.Context;
import android.content.Intent;

public class NormalExecutor extends RequestExecutor {

    public static class Bridge implements ExecutorBridge {

        private final Context context;

        public Bridge(Context context) {
            this.context = context;
        }
        
        @Override
        public void queueRequest(long id) {
            Intent intent = new Intent(context, NormalExecutor.class);
            intent.putExtra(NormalExecutor.EXTRA_ID, id);
            context.startService(intent);
        }

        @Override
        public void cancelRequest(long id) {
            Intent intent = new Intent(context, NormalExecutor.class);
            intent.putExtra(NormalExecutor.EXTRA_ID, id);
            intent.putExtra(NormalExecutor.EXTRA_CANCEL, true);
            context.startService(intent);
        }
    }
    
    public static Repository<RequestBase> repository; 
    
    public NormalExecutor() {
        super(repository);
    }

}
