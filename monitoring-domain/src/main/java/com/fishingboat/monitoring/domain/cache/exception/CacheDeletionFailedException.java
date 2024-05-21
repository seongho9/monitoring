package com.fishingboat.monitoring.domain.cache.exception;

public class CacheDeletionFailedException extends RuntimeException {
    public CacheDeletionFailedException() {
        super();
    }

    public CacheDeletionFailedException(String message) {
        super(message);
    }

    public CacheDeletionFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheDeletionFailedException(Throwable cause) {
        super(cause);
    }

    protected CacheDeletionFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
