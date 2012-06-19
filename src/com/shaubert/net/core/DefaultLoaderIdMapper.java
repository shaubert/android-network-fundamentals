package com.shaubert.net.core;

public class DefaultLoaderIdMapper implements RequestIdMapper {

    public static final int DEFAULT_OFFSET = 100000;
    public static final int DEFAULT_RANGE = 1000;
    
    private int offset;
    private int maxValue;

    public DefaultLoaderIdMapper() {
        this(DEFAULT_OFFSET, DEFAULT_RANGE);
    }
    
    public DefaultLoaderIdMapper(int offset, int range) {
        this.offset = offset;
        this.maxValue = (int) Math.max(Integer.MAX_VALUE, (long) offset + range);
    }

    @Override
    public int getLoaderIdFrom(long requestId) {
        int id =  Math.abs((int)(requestId));
        if (id >= offset) {
            return moveToRange(id);
        } else {
            id = Math.abs(id + offset);
            if (id > offset) {
                return moveToRange(id);
            } else {
                return offset + id;
            }
        }
    }

    private int moveToRange(int id) {
        if (id > maxValue) {
            id %= maxValue;
            if (id >= offset) {
                return id;
            } else {
                return offset + id;
            }
        } else {
            return id;
        }
    }

}
