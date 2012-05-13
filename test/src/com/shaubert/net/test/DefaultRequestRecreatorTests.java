package com.shaubert.net.test;

import com.shaubert.net.core.DefaultRequestRecreator;
import com.shaubert.net.core.RequestBase;

import android.test.AndroidTestCase;

public class DefaultRequestRecreatorTests extends AndroidTestCase {

    public void testRecreation() throws Exception {
        SimpleRequest request = new SimpleRequest();
        DefaultRequestRecreator recreator = new DefaultRequestRecreator(getContext());
        RequestBase request2 = recreator.recreate("com.shaubert.net.test.SimpleRequest2", request.getState());
        assertNotNull(request2);
        assertTrue(request2 instanceof SimpleRequest);
        assertEquals(request.getState(), request2.getState());
    }
    
}
