package com.shaubert.net.demo;

import com.shaubert.net.nutshell.RequestStatus;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class RequestCursorAdapter extends CursorAdapter implements OnClickListener {

    public interface OnCancelListener {
        void cancelRequest(long id);
    }
    
    private static class RequestElementTag {
        private TextView title;
        private TextView status;
        private Button cancelButton;
        
        private String titleFormat;
        
        public RequestElementTag(View requestView, OnClickListener listener) {
            title = (TextView)requestView.findViewById(R.id.title);
            status = (TextView)requestView.findViewById(R.id.status);
            cancelButton = (Button)requestView.findViewById(R.id.cancel_button);
            cancelButton.setOnClickListener(listener);
            titleFormat = requestView.getContext().getString(R.string.request_title_format);
        }
        
        public void update(RequestCursor cursor) {
            this.title.setText(String.format(titleFormat, cursor.getId()));
            RequestStatus status = cursor.getStatus();
            String statusString = status.toString();
            if (status == RequestStatus.PROCESSING) {
                statusString += " (" + (int) (cursor.getProgress() * 100) + "%)";
            }
            this.status.setText(statusString);
            this.cancelButton.setTag(cursor.getId());
            this.cancelButton.setEnabled(!cursor.isCancelled());
            this.cancelButton.setText(cursor.isCancelled() ? R.string.cancelled : R.string.cancel_request);
        }
    }
    
    private LayoutInflater inflater;
    private OnCancelListener cancelListener;
    
    public RequestCursorAdapter(Context context, RequestCursor cursor) {
        super(context, cursor, -1);
        inflater = LayoutInflater.from(context);
    }
    
    public void setCancelListener(OnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View requestView = inflater.inflate(R.layout.request_list_element, parent, false);
        requestView.setTag(new RequestElementTag(requestView, this));
        return requestView;
    }
    
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancel_button) {
            if (cancelListener != null) {
                cancelListener.cancelRequest((Long)v.getTag());
            }
        }
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((RequestElementTag) view.getTag()).update((RequestCursor)cursor);
    }

}
