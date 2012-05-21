package com.shaubert.net.core;

public class RequestIdToLoaderIdConverter implements RequestIdMapper {

    public static final int DEFAULT_OFFSET = 1000;
    
    private int offset;

    public RequestIdToLoaderIdConverter() {
        this(DEFAULT_OFFSET);
    }
    
    public RequestIdToLoaderIdConverter(int offset) {
        this.offset = offset;
    }

    @Override
    public int getLoaderIdFrom(long requestId) {
        int id =  Math.abs((int)(requestId));
        if (id >= offset) {
            return id;
        } else {
            id = Math.abs(id + offset);
            if (id > offset) {
                return id;
            } else {
                return offset + id;
            }
        }
    }

}
