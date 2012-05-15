package com.shaubert.net.core;

import com.shaubert.net.nutshell.RequestRecreator;
import com.shaubert.net.nutshell.RequestState;

import android.content.Context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DefaultRequestRecreator implements RequestRecreator {

    private final Context context;

    public DefaultRequestRecreator(Context context) {
        this.context = context;
    }
    
    @Override
    public RequestBase recreate(String className, RequestState state) {
        try {
            @SuppressWarnings("unchecked")
            Class<RequestBase> clazz = (Class<RequestBase>)Class.forName(className, true, context.getClassLoader());
            Constructor<RequestBase> constructor;
            try {
                constructor = clazz.getConstructor(RequestStateBase.class);
            } catch (Exception e) {
                throw new IllegalStateException("Class " + className + " must have constructor with " + RequestStateBase.class.getCanonicalName() + " parameter", e);
            }
            RequestBase base = constructor.newInstance(state);
            return base;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}