
package com.shaubert.net.demo;

import com.shaubert.net.core.DefaultRequestRecreator;
import com.shaubert.net.core.RequestExecutor;
import com.shaubert.net.core.RequestRepository;

public class DemoService extends RequestExecutor {

    @Override
    public void onCreate() {
        super.onCreate();
        setRepository(new RequestRepository(getContext(), 
                new DefaultRequestRecreator(getContext()),
                RequestContract.Request.URI));
    }

}
