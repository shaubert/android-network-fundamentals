
package com.shaubert.net.demo;

import com.shaubert.net.core.DefaultRequestRecreator;
import com.shaubert.net.core.RequestExecutor;
import com.shaubert.net.core.RequestRepositoryOnContentResolver;

public class DemoService extends RequestExecutor {

    @Override
    public void onCreate() {
        super.onCreate();
        setRepository(new RequestRepositoryOnContentResolver(getContext(), 
                new DefaultRequestRecreator(getContext()),
                RequestContract.Request.URI));
    }

}
