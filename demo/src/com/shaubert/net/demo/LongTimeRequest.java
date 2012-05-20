package com.shaubert.net.demo;

import com.shaubert.net.core.RequestBase;
import com.shaubert.net.core.RequestStateBase;
import com.shaubert.net.nutshell.ExecutionContext;

import java.util.Random;

public class LongTimeRequest extends RequestBase {

    private Random random = new Random();
    
    public LongTimeRequest(RequestStateBase state) {
        super(state);
    }

    @Override
    public void execute(ExecutionContext executionContext) throws Exception {
        for (int i = 0; i < 20; i++) {
            Thread.sleep(300);
            if (random.nextInt(20) == 2) {
                throw new IllegalStateException();
            }
            getState().setProgress(i / 20f);
            executionContext.getRepository().update(this);
        }
    }
    
}