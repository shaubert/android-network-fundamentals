package com.shaubert.net.core;

import com.shaubert.net.nutshell.RequestState;
import com.shaubert.net.nutshell.RequestStatus;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;

public class RequestStateBase implements RequestState {

    public static final String ID_KEY = "_id";
    public static final String STATUS_KEY = "_status";
    public static final String PROGRESS_KEY = "_progress"; 
    public static final String CANCELLED_KEY = "_cancelled";    
    public static final String EXTRAS_KEY = "_extras";
    
    protected ContentValues values;
    private JSONObject extras;
    
    public RequestStateBase() {
        values = new ContentValues();
        setStatus(RequestStatus.NOT_STARTED);
        setProgress(-1f);
        values.put(CANCELLED_KEY, 0);
        extras = new JSONObject();
    }

    public RequestStateBase(ContentValues values) {
        this.values = values;
        if (values.containsKey(EXTRAS_KEY)) {
            try {
                this.extras = new JSONObject(values.getAsString(EXTRAS_KEY));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    @Override
    public long getId() {
        return values.containsKey(ID_KEY) ? values.getAsLong(ID_KEY) : -1;
    }

    public void setId(long id) {
        values.put(ID_KEY, id);
    }
    
    @Override
    public RequestStatus getStatus() {
        String status = values.getAsString(STATUS_KEY);
        return status != null ? RequestStatus.valueOf(status) : RequestStatus.NOT_STARTED;
    }

    public void setStatus(RequestStatus status) {
        values.put(STATUS_KEY, status.toString());
    }
    
    void setCancelled() {
        values.put(CANCELLED_KEY, 1);
    }
    
    public boolean isCancelled() {
        return values.getAsInteger(CANCELLED_KEY) > 0;
    }
    
    public float getProgress() {
        float progress = values.getAsFloat(PROGRESS_KEY);
        if (progress == -1) {
            switch (getStatus()) {
                case NOT_STARTED:
                case QUEUED:
                case PROCESSING:
                    return 0;
                case FINISHED:
                case FINISHED_WITH_ERRORS:
                    return 1f;
            }
        } 
        return progress;
    }
    
    public void setProgress(float progress) {
        values.put(PROGRESS_KEY, progress);
    }
    
    public ContentValues getValues() {
        values.put(EXTRAS_KEY, extras.toString());
        return values;
    }
    
    public boolean getBoolean(String key, boolean defaultValue) {
        try {
            return extras.has(key) ? extras.getBoolean(key) : defaultValue;
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public double getDouble(String key, double defaultValue) {
        try {
            return extras.has(key) ? extras.getDouble(key) : defaultValue;
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public int getInt(String key, int defaultValue) {
        try {
            return extras.has(key) ? extras.getInt(key) : defaultValue;
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public long getLong(String key, long defaultValue) {
        try {
            return extras.has(key) ? extras.getLong(key) : defaultValue;
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public String getString(String key, String defaultValue) {
        try {
            return extras.has(key) ? extras.getString(key) : defaultValue;
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public void put(String key, boolean value) {
        try {
            extras.put(key, Boolean.valueOf(value));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void put(String key, double value) {
        try {
            extras.put(key, Double.valueOf(value));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void put(String key, int value) {
        try {
            extras.put(key, Integer.valueOf(value));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void put(String key, long value) {
        try {
            extras.put(key, Long.valueOf(value));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void put(String key, String value) {
        try {
            extras.put(key, value);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public String toString() {
        return getValues().toString();
    }
}