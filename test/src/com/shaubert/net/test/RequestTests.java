package com.shaubert.net.test;

import android.test.AndroidTestCase;

import junit.framework.Assert;

public class RequestTests extends AndroidTestCase {

    public void testCancel() throws Exception {
        SimpleRequest request = new SimpleRequest();
        Assert.assertFalse(request.isCancelled());
        request.cancel();
        Assert.assertTrue(request.isCancelled());
    }
    
    public void testProgress() throws Exception {
        SimpleRequest request = new SimpleRequest();
        Assert.assertEquals(request.getState().getProgress(), 0f);
        request.getState().setProgress(0.6f);
        Assert.assertEquals(request.getState().getProgress(), 0.6f);
    }
    
    public void testExtras() throws Exception {
        SimpleRequest request = new SimpleRequest();
        request.getState().put("1", true);
        request.getState().put("2", 1.0);
        request.getState().put("3", 1);
        request.getState().put("4", 1l);
        request.getState().put("5", "1");
        
        Assert.assertEquals(true, request.getState().getBoolean("1", false));
        Assert.assertEquals(1.0, request.getState().getDouble("2", 1.0));
        Assert.assertEquals(1, request.getState().getInt("3", 1));
        Assert.assertEquals(1l, request.getState().getLong("4", 1l));
        Assert.assertEquals("1", request.getState().getString("5"));
        
        Assert.assertEquals(false, request.getState().getBoolean("6", false));
        Assert.assertEquals(2.0, request.getState().getDouble("7", 2.0));
        Assert.assertEquals(2, request.getState().getInt("8", 2));
        Assert.assertEquals(2l, request.getState().getLong("9", 2l));
        Assert.assertEquals("2", request.getState().getString("10", "2"));
    }
    
}
