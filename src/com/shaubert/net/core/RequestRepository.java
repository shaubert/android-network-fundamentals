package com.shaubert.net.core;

import com.shaubert.net.nutshell.Repository;
import com.shaubert.net.nutshell.RequestRecreator;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;
/**
 * Delegates CRUD operations to ContentProvider. Request table must implement such contract:
 * <pre>
 * &#064;Entity
 * public Request {
 *      &#064;Id
 *      &#064;GeneratedValue
 *      &#064;Column(type = ColumnType.INT)    
 *      public static final String ID = RequestStateBase.ID_KEY;
 *
 *      &#064;Column(type = ColumnType.STRING, nullable = false)
 *      public static final String STATUS = RequestStateBase.STATUS_KEY;
 *
 *      &#064;Column(type = ColumnType.FLOAT, nullable = false)
 *      public static final String PROGRESS = RequestStateBase.PROGRESS_KEY;
 *  
 *      &#064;Column(type = ColumnType.INT, nullable = false)
 *      public static final String CANCELLED = RequestStateBase.CANCELLED_KEY;
 *  
 *      &#064;Column(type = ColumnType.STRING)
 *      public static final String EXTRAS = RequestStateBase.EXTRAS_KEY;
 *    
 *      &#064;Column(type = ColumnType.STRING, nullable = false)
 *      public static final String CLASS_NAME = RequestRepository.CLASS_NAME_KEY;
 *    
 *      &#064;Column(type = ColumnType.LONG)
 *      public static final String CREATION_TIME = RequestRepository.CREATION_TIME_KEY;
 *    
 *      &#064;Column(type = ColumnType.LONG)
 *      public static final String UPDATE_TIME = RequestRepository.UPDATE_TIME_KEY;
 * }
 * </pre>
 * 
 */
public class RequestRepository implements Repository<RequestBase> {

    public static final String CLASS_NAME_KEY = "_class_name";
    public static final String CREATION_TIME_KEY = "_creation_time";
    public static final String UPDATE_TIME_KEY = "_update_time";

    private enum Op {
        INSERT,
        UPDATE;
    }
    
    private ContentResolver contentResolver;
    private final RequestRecreator<RequestBase, RequestStateBase> recreator;
    private final Uri uri;

    public RequestRepository(Context context, RequestRecreator<RequestBase, RequestStateBase> recreator, Uri uri) {
        this.recreator = recreator;
        this.uri = uri;
        this.contentResolver = context.getContentResolver();
    }
    
    @Override
    public RequestBase select(long id) {
        Cursor cursor = contentResolver.query(uri, null, "_id=" + id, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    ContentValues values = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cursor, values);
                    String className = values.getAsString(CLASS_NAME_KEY);
                    return recreator.recreate(className, new RequestStateBase(values));
                }
            } finally {
                cursor.close();
            }
        } 
        return null;
    }
    
    protected ContentValues requestToContentValues(RequestBase request) {
        ContentValues values = request.getState().getValues();
        values.put(CLASS_NAME_KEY, request.getClass().getCanonicalName());
        long currentTimeMillis = System.currentTimeMillis();
        if (!values.containsKey(CREATION_TIME_KEY)) {
            values.put(CREATION_TIME_KEY, currentTimeMillis);
        }
        values.put(UPDATE_TIME_KEY, currentTimeMillis);
        return values;
    }
    
    protected ArrayList<ContentProviderOperation> requestsToOperations(List<RequestBase> requests, Op op) {
        ArrayList<ContentProviderOperation> result = new ArrayList<ContentProviderOperation>();
        for (RequestBase request : requests) {
            ContentValues values = requestToContentValues(request);
            ContentProviderOperation operation = null;
            switch (op) {
                case INSERT: 
                    operation = ContentProviderOperation.newInsert(uri).withValues(values).build();
                    break;
                case UPDATE:
                    operation = ContentProviderOperation.newUpdate(ContentUris.withAppendedId(uri, request.getState().getId())).withValues(values).build();
                    break;
            }
            result.add(operation);
        }
        return result;
    }
    
    @Override
    public void insert(RequestBase entity) {
        Uri result = contentResolver.insert(uri, requestToContentValues(entity));
        long id = ContentUris.parseId(result);
        entity.getState().setId(id);
    }

    @Override
    public void insert(List<RequestBase> entities) {
        ArrayList<ContentProviderOperation> ops = requestsToOperations(entities, Op.INSERT);
        try {
            ContentProviderResult[] results = contentResolver.applyBatch(uri.getAuthority(), ops);
            for (int i = 0; i < results.length; i++) {
                entities.get(i).getState().setId(ContentUris.parseId(results[i].uri));
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (OperationApplicationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(RequestBase entity) {
        contentResolver.update(ContentUris.withAppendedId(uri, entity.getState().getId()), requestToContentValues(entity), null, null);
    }

    @Override
    public void update(List<RequestBase> entities) {
        ArrayList<ContentProviderOperation> ops = requestsToOperations(entities, Op.UPDATE);
        try {
            contentResolver.applyBatch(uri.getAuthority(), ops);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (OperationApplicationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(RequestBase entity) {
        contentResolver.delete(ContentUris.withAppendedId(uri, entity.getState().getId()), null, null);
    }

    @Override
    public void delete(List<RequestBase> entities) {
        StringBuilder builder = new StringBuilder();
        for (RequestBase request : entities) {
            builder.append(request.getState().getId()).append(",");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        String ids = builder.toString();
        contentResolver.delete(uri, "_id IN (" + ids + ")", null);
    }

    @Override
    public void deleteAll() {
        contentResolver.delete(uri, null, null);
    }

    @Override
    public void registerObserver(ContentObserver observer) {
        contentResolver.registerContentObserver(uri, true, observer);
    }

    @Override
    public void registerObserver(long entityId, ContentObserver observer) {
        contentResolver.registerContentObserver(ContentUris.withAppendedId(uri, entityId), false, observer);
    }
    
    @Override
    public void unregisterObserver(ContentObserver observer) {
        contentResolver.unregisterContentObserver(observer);
    }

}