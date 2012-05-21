package com.shaubert.net.test;

import com.shaubert.net.core.DefaultJournal;
import com.shaubert.net.core.LoaderBasedRequestStateWatcher;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.test.AndroidTestCase;

import java.io.FileDescriptor;
import java.io.PrintWriter;

public class LoaderBasedWatcherTest extends AndroidTestCase {

    public static class LoaderManager2 extends LoaderManager {
        
        public int destroy;
        public int init;
        
        @Override
        public void destroyLoader(int arg0) {
            destroy++;
        }

        @Override
        public void dump(String arg0, FileDescriptor arg1, PrintWriter arg2, String[] arg3) {
        }

        @Override
        public <D> Loader<D> getLoader(int arg0) {
            return null;
        }

        @Override
        public <D> Loader<D> initLoader(int arg0, Bundle arg1, LoaderCallbacks<D> arg2) {
            init++;
            return null;
        }

        @Override
        public <D> Loader<D> restartLoader(int arg0, Bundle arg1, LoaderCallbacks<D> arg2) {
            return null;
        }
        
    }
    
    private DefaultJournal journal;
    private SimpleRepository repository;
    private LoaderBasedRequestStateWatcher watcher;
    private LoaderManager2 loaderManager;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        repository = new SimpleRepository();
        NormalExecutor.repository = repository;
        loaderManager = new LoaderManager2();
        watcher = new LoaderBasedRequestStateWatcher(repository, getContext(), loaderManager);
        journal = new DefaultJournal(repository, new SimpleExecutor(repository, getContext()), watcher);
    }
    
    public void testAttach() throws Exception {
        SimpleRequest request = new SimpleRequest();
        journal.register(request);
        journal.registerForUpdates(request);
        assertEquals(1, loaderManager.init);
    }
    
    public void testDettach() throws Exception {
        SimpleRequest request = new SimpleRequest();
        journal.register(request);
        journal.registerForUpdates(request);
        journal.unregisterForUpdates(request);
        assertEquals(1, loaderManager.destroy);
    }
    
    public void testDettachWithMultipleAttaches() throws Exception {
        SimpleRequest request = new SimpleRequest();
        journal.register(request);
        journal.registerForUpdates(request);
        journal.registerForUpdates(request);
        journal.unregisterForUpdates(request);
        assertEquals(0, loaderManager.destroy);
    }
    
}
