package com.example.search_engine.services.entityServices;

public class ThrowableCauseHelper {

    public static Throwable getInitialCause(Throwable throwable) {
        if (throwable.getCause() == null) return throwable;
        return getInitialCause(throwable.getCause());
    }

}
