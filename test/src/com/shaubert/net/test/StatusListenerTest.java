package com.shaubert.net.test;

import com.shaubert.net.core.RequestStateBase;
import com.shaubert.net.core.RequestStatusListener;
import com.shaubert.net.nutshell.RequestState;
import com.shaubert.net.nutshell.RequestStatus;

import android.test.AndroidTestCase;

import java.util.concurrent.atomic.AtomicBoolean;

public class StatusListenerTest extends AndroidTestCase {

    public void testHasChanges() throws Exception {
        final AtomicBoolean change = new AtomicBoolean(false);
        RequestStatusListener statusListener = new RequestStatusListener() {
            @Override
            protected boolean hasChanges(RequestState oldState, RequestState newState) {
                boolean hasChanges = super.hasChanges(oldState, newState);
                change.set(hasChanges);
                return hasChanges;
            }
        };
        
        SimpleRequest request = new SimpleRequest();
        request.setFullStateChangeListener(statusListener);
        
        request.setState(request.getState());
        assertFalse(change.get());
        
        RequestStateBase state = new RequestStateBase();
        state.setStatus(RequestStatus.QUEUED);
        request.setState(state);
        assertTrue(change.get());
        
        state = new RequestStateBase();
        state.setStatus(RequestStatus.QUEUED);
        request.setState(state);
        assertFalse(change.get());
        
        state = new RequestStateBase();
        state.setStatus(RequestStatus.PROCESSING);
        request.setState(state);
        assertTrue(change.get());
        
        state = new RequestStateBase();
        state.setStatus(RequestStatus.PROCESSING);
        request.setState(state);
        assertTrue(change.get());
    }
    
}
