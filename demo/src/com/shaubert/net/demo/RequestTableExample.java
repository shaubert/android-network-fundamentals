package com.shaubert.net.demo;

import com.shaubert.net.core.DefaultExecutorBridge;
import com.shaubert.net.core.DefaultJournal;
import com.shaubert.net.core.DefaultRequestRecreator;
import com.shaubert.net.core.LoaderBasedRequestStateWatcher;
import com.shaubert.net.core.RequestRepositoryOnContentResolver;
import com.shaubert.net.demo.RequestCursorAdapter.OnCancelListener;
import com.shaubert.net.nutshell.RequestStatus;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

public class RequestTableExample extends FragmentActivity implements LoaderCallbacks<Cursor> {

    public static final int REQUEST_LOADER_ID = 10;
    
    private DefaultJournal journal;
    private ListView listView;

    private View clearRequestTableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_example);

        setupJournal();
        getSupportLoaderManager().initLoader(REQUEST_LOADER_ID, null, this);
    }
    
    @Override
    public void onContentChanged() {
        super.onContentChanged();
        
        View addRequestView = findViewById(R.id.add_request_button);
        addRequestView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewRequest();
            }
        });
        
        clearRequestTableView = findViewById(R.id.clear_table_button);
        clearRequestTableView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getContentResolver().delete(RequestContract.Request.URI, null, null);
            }
        });
        
        listView = (ListView)findViewById(R.id.list);
    }
    
    private void setupJournal() {
        RequestRepositoryOnContentResolver repository = new RequestRepositoryOnContentResolver(
                getBaseContext(), new DefaultRequestRecreator(getBaseContext()), RequestContract.Request.URI);
        DefaultExecutorBridge executorBridge = new DefaultExecutorBridge(getApplicationContext(), DemoService.class);
        LoaderBasedRequestStateWatcher stateWatcher = new LoaderBasedRequestStateWatcher(repository, getBaseContext(), getSupportLoaderManager());
        journal = new DefaultJournal(repository, executorBridge, stateWatcher);
    }
    
    private void startNewRequest() {
        clearRequestTableView.setEnabled(false);
        LongTimeRequest longTimeRequest = new LongTimeRequest(null);
        journal.register(longTimeRequest);
    }  
    
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getBaseContext(), RequestContract.Request.URI, null, null, null, RequestContract.Request.ID + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        RequestCursor requestCursor = new RequestCursor(data);
        boolean hasExecuting = false;
        if (requestCursor.moveToFirst()) {
            do {
                RequestStatus status = requestCursor.getStatus();
                hasExecuting = !requestCursor.isCancelled() && !RequestStatus.isFinishedSomehow(status) && status != RequestStatus.NOT_STARTED; 
            } while (requestCursor.moveToNext() && !hasExecuting);
        }
        clearRequestTableView.setEnabled(!hasExecuting);
        
        RequestCursorAdapter cursorAdapter = (RequestCursorAdapter)listView.getAdapter();
        if (cursorAdapter == null) {
            cursorAdapter = new RequestCursorAdapter(getBaseContext(), requestCursor);
            cursorAdapter.setCancelListener(new OnCancelListener() {
                @Override
                public void cancelRequest(long id) {
                    journal.cancel(id);
                }
            });
            listView.setAdapter(cursorAdapter);
        } else {
            cursorAdapter.swapCursor(requestCursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        clearRequestTableView.setEnabled(false);
        RequestCursorAdapter cursorAdapter = (RequestCursorAdapter)listView.getAdapter();
        if (cursorAdapter != null) {
            cursorAdapter.swapCursor(null);
        }
    }
}
