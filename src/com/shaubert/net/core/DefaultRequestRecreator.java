package com.shaubert.net.core;

import com.shaubert.net.nutshell.RequestRecreator;
import com.shaubert.net.nutshell.RequestState;

import android.content.Context;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class DefaultRequestRecreator implements RequestRecreator {

    private final Context context;

    private HashMap<String, WeakReference<Constructor<RequestBase>>> constructorsCache;
    
    public DefaultRequestRecreator(Context context) {
        this.context = context;
        this.constructorsCache = new HashMap<String, WeakReference<Constructor<RequestBase>>>();
    }
    
    @Override
    public RequestBase recreate(String className, RequestState state) {
        try {
            final Constructor<RequestBase> constructor;
            WeakReference<Constructor<RequestBase>> ref = constructorsCache.get(className);
            if (ref != null && ref.get() != null) {
                constructor = ref.get();
            } else {
                @SuppressWarnings("unchecked")
                Class<RequestBase> clazz = (Class<RequestBase>)Class.forName(className, true, context.getClassLoader());
                try {
                    constructor = clazz.getConstructor(RequestStateBase.class);
                    constructorsCache.put(className, new WeakReference<Constructor<RequestBase>>(constructor));
                } catch (Exception e) {
                    throw new IllegalStateException("Class " + className + " must have constructor with " + RequestStateBase.class.getCanonicalName() + " parameter", e);
                }
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