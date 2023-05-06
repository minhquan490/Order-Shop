package com.bachlinh.order.batch;

import java.util.Collection;
import java.util.LinkedList;

public final class Report {
    private boolean hasError = false;
    private final Collection<Exception> error;

    public Report() {
        this.error = new LinkedList<>();
    }

    public void addError(Exception error) {
        if (!hasError) {
            hasError = true;
        }
        this.error.add(error);
    }

    public Collection<Exception> getError() {
        return error;
    }

    public boolean isHasError() {
        return hasError;
    }
}
