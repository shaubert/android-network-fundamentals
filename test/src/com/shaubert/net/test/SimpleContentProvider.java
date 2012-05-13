package com.shaubert.net.test;

import com.shaubert.net.core.RequestRepository;
import com.shaubert.net.core.RequestStateBase;

import org.ecype.diego.ContractContentProvider;

import android.net.Uri;
import androidx.persistence.Column;
import androidx.persistence.ColumnType;
import androidx.persistence.Contract;
import androidx.persistence.Entity;
import androidx.persistence.GeneratedValue;
import androidx.persistence.Id;

public class SimpleContentProvider extends ContractContentProvider {

    @Contract(authority = "test", dbFileName = "")
    public static class RequestContract {
        
        @Entity
        public static class Request {
            @Id
            @GeneratedValue
            @Column(type = ColumnType.INT)
            public static final String ID = RequestStateBase.ID_KEY;

            @Column(type = ColumnType.STRING, nullable = false)
            public static final String STATUS = RequestStateBase.STATUS_KEY;
            
            @Column(type = ColumnType.FLOAT, nullable = false)
            public static final String PROGRESS = RequestStateBase.PROGRESS_KEY;
            
            @Column(type = ColumnType.INT, nullable = false)
            public static final String CANCELLED = RequestStateBase.CANCELLED_KEY;
            
            @Column(type = ColumnType.STRING)
            public static final String EXTRAS = RequestStateBase.EXTRAS_KEY;
            
            @Column(type = ColumnType.STRING, nullable = false)
            public static final String CLASS_NAME = RequestRepository.CLASS_NAME_KEY;
            
            @Column(type = ColumnType.LONG)
            public static final String CREATION_TIME = RequestRepository.CREATION_TIME_KEY;
            
            @Column(type = ColumnType.LONG)
            public static final String UPDATE_TIME = RequestRepository.UPDATE_TIME_KEY;
            
            public static final Uri URI = Uri.parse("content://test/request");
        }
        
    }
    
    public SimpleContentProvider() {
        super(RequestContract.class);
    }
    
}
