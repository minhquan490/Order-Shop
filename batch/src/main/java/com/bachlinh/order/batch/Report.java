package com.bachlinh.order.batch;

import java.util.Collection;
import java.util.LinkedList;

public final class Report {
    private boolean hasError = false;
    private final Collection<Exception> error;
    private final String jobName;

    public Report(String jobName) {
        this.jobName = jobName;
        this.error = new LinkedList<>();
    }

    public void addError(Exception error) {
        if (!hasError) {
            hasError = true;
        }
        this.error.add(error);
    }

    public boolean isHasError() {
        return this.hasError;
    }

    public Collection<Exception> getError() {
        return this.error;
    }

    public String getJobName() {
        return this.jobName;
    }
}
