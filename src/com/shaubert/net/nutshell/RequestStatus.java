package com.shaubert.net.nutshell;

public enum RequestStatus {

    NOT_STARTED,
    QUEUED,
    PROCESSING,
    FINISHED,
    FINISHED_WITH_ERRORS;
    
    public static boolean isWaiting(RequestStatus status) {
        return status == NOT_STARTED || status == QUEUED;
    }
    
    public static boolean isProcessing(RequestStatus status) {
        return status == PROCESSING;
    } 
    
    public static boolean isFinishedSomehow(RequestStatus status) {
        return status == FINISHED || status == FINISHED_WITH_ERRORS;
    }
    
    public static boolean isFinishedWithoutAccidents(RequestStatus status) {
        return status == FINISHED;
    }
}