package com.shaubert.net.demo;

import com.shaubert.net.core.DefaultExecutorBridge;
import com.shaubert.net.core.DefaultJournal;
import com.shaubert.net.core.DefaultRequestRecreator;
import com.shaubert.net.core.LoaderBasedRequestStateWatcher;
import com.shaubert.net.core.RequestRepositoryOnContentResolver;
import com.shaubert.net.core.RequestStateBase;
import com.shaubert.net.core.RequestStatusListener;
import com.shaubert.net.nutshell.Request;
import com.shaubert.net.nutshell.RequestStatus;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class DialogExample extends FragmentActivity {

    private final String filename = Environment.getExternalStorageDirectory() + "/net-fund-demo-image.jpg";
    
    private DefaultJournal journal;
    private long requestId;

    private ImageView imageView;

    private ProgressDialog progressDialog;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_example);
        
        imageView = (ImageView)findViewById(R.id.image);
        findViewById(R.id.button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                requestId = createRequest();
                imageView.setImageDrawable(null);
            }
        });
        
        setupJournal();
        
        if (savedInstanceState != null) {
            requestId = savedInstanceState.getLong("requestId", -1);
            if (requestId != -1) {
                restoreRequest();
            }
        }
    }

    private void restoreRequest() {
        DataLoadRequest request = (DataLoadRequest)journal.getRequest(requestId); 
        journal.registerForUpdates(request);
        if (request != null) {
            RequestStatus status = request.getState().getStatus();
            if (!RequestStatus.isFinishedSomehow(status)) {
                progressDialog = createProgressDialog(request);
                progressDialog.show();
                RequestStatusListener statusListener = createStatusListener(filename);
                request.setFullStateChangeListener(statusListener);
                statusListener.processCurrentState(request);
            } else if (!request.isCancelled() && status == RequestStatus.FINISHED) {
                openImage(filename);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("requestId", requestId);
        dismissProgressDialog();
    }

    
    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            dismissProgressDialog();
        }
    }
    
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    
    private void setupJournal() {
        RequestRepositoryOnContentResolver repository = new RequestRepositoryOnContentResolver(
                getBaseContext(), new DefaultRequestRecreator(getBaseContext()), RequestContract.Request.URI);
        DefaultExecutorBridge executorBridge = new DefaultExecutorBridge(getApplicationContext(), DemoService.class);
        LoaderBasedRequestStateWatcher stateWatcher = new LoaderBasedRequestStateWatcher(repository, getBaseContext(), getSupportLoaderManager());
        journal = new DefaultJournal(repository, executorBridge, stateWatcher);
    }

    private long createRequest() {
        final DataLoadRequest request = new DataLoadRequest("http://assets0.lookatme.ru/assets/event-image/74/d5/72778/event-image-poster.jpg", 
                filename);
        
        progressDialog = createProgressDialog(request);
        request.setFullStateChangeListener(createStatusListener(filename));
        
        journal.register(request);
        journal.registerForUpdates(request);
        progressDialog.show();
        
        return request.getState().getId();
    }

    private RequestStatusListener createStatusListener(final String filename) {
        return new RequestStatusListener() {
            @Override
            public void onFinished(Request request) {
                dismissProgressDialog();
                if (!request.isCancelled()) {
                    openImage(filename);
                }
                journal.unregisterForUpdates(request);
            }
            
            @Override
            public void onRequestProcessing(Request request, float progress) {
                if (progress > 0) {
                    progressDialog.setProgress((int)(progress * 100));
                }
            }
            
            @Override
            public void onError(Request request) {
                dismissProgressDialog();
                Toast.makeText(DialogExample.this, ((RequestStateBase) request.getState()).getString("error"), Toast.LENGTH_LONG).show();
                journal.unregisterForUpdates(request);
            }
        };
    }

    private ProgressDialog createProgressDialog(final DataLoadRequest request) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data");
        progressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                journal.cancelOrInterrupt(request.getState().getId());
            }
        });
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        return progressDialog;
    }

    protected void openImage(String filename) {        
        imageView.setImageURI(Uri.fromFile(new File(filename)));
    }

}