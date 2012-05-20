package com.shaubert.net.demo;

import com.shaubert.net.nutshell.RequestStatus;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;

public class RequestCursor extends CursorWrapper {

    private int idField;
    private int statusField;
    private int progressField;
    private int cancelledField;
    private int extrasField;
    private int classNameField;
    private int creationTimeField;
    private int updateTimeField;
    
    public RequestCursor(Cursor cursor) {
        super(cursor);
        initFields(cursor);
        
    }

    private void initFields(Cursor cursor) {
        idField = cursor.getColumnIndex(RequestContract.Request.ID);
        statusField = cursor.getColumnIndex(RequestContract.Request.STATUS);
        progressField = cursor.getColumnIndex(RequestContract.Request.PROGRESS);
        cancelledField = cursor.getColumnIndex(RequestContract.Request.CANCELLED);
        extrasField = cursor.getColumnIndex(RequestContract.Request.EXTRAS);
        classNameField = cursor.getColumnIndex(RequestContract.Request.CLASS_NAME);
        creationTimeField = cursor.getColumnIndex(RequestContract.Request.CREATION_TIME);
        updateTimeField = cursor.getColumnIndex(RequestContract.Request.UPDATE_TIME);
    }
    
    public long getId() {
        return getLong(idField);
    }

    public RequestStatus getStatus() {
        return RequestStatus.valueOf(getString(statusField));
    }

    public float getProgress() {
        return getFloat(progressField);
    }

    public boolean isCancelled() {
        return getInt(cancelledField) != 0;
    }

    public JSONObject getRequestExtras() {
        try {
            return new JSONObject(getString(extrasField));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public String getClassName() {
        return getString(classNameField);
    }

    public Date getCreationTime() {
        return new Date(getLong(creationTimeField));
    }

    public Date getUpdateTime() {
        return new Date(updateTimeField);
    }
    
}
