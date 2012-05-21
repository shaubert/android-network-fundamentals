package com.shaubert.net.test;

import com.shaubert.net.core.RequestIdToLoaderIdConverter;

import android.test.AndroidTestCase;

import java.util.Random;

public class RequestIdToLoaderIdConverterTest extends AndroidTestCase {

    public void testLoaderId() throws Exception {
        int offset = 500 + new Random().nextInt(50000);
        RequestIdToLoaderIdConverter converter = new RequestIdToLoaderIdConverter(offset);
        int id = converter.getLoaderIdFrom(1);
        assertTrue(id >= offset);
        
        id = converter.getLoaderIdFrom(Integer.MAX_VALUE + 1);
        assertTrue(id >= offset);
        
        id = converter.getLoaderIdFrom(Integer.MAX_VALUE - offset + 1);
        assertTrue(id >= offset);
    }
    
}
