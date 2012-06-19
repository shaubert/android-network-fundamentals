package com.shaubert.net.test;

import com.shaubert.net.core.DefaultLoaderIdMapper;

import android.test.AndroidTestCase;

import java.util.Random;

public class DefaultLoaderIdMapperTest extends AndroidTestCase {

    public void testLoaderId() throws Exception {
        int offset = 500 + new Random().nextInt(50000);
        int range = 500 + new Random().nextInt(50000);
        int max = offset + range;
        DefaultLoaderIdMapper converter = new DefaultLoaderIdMapper(offset, range);
        int id = converter.getLoaderIdFrom(1);
        assertTrue(id >= offset && id <= max);
        
        id = converter.getLoaderIdFrom(Integer.MAX_VALUE + 1);
        assertTrue(id >= offset && id <= max);
        
        id = converter.getLoaderIdFrom(Integer.MAX_VALUE - offset + 1);
        assertTrue(id >= offset && id <= max);
    }
    
}
