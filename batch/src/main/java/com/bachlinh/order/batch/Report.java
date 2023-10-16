package com.bachlinh.order.batch;

public final class Report {
    private boolean hasError = false;
    private Exception error;
    private final String jobName;

    public Report(String jobName) {
        this.jobName = jobName;
    }

    public void addError(Exception error) {
        if (!hasError) {
            hasError = true;
        }
        this.error = error;
    }

    public boolean isHasError() {
        return this.hasError;
    }

    public Exception getError() {
        return this.error;
    }

    public String getJobName() {
        return this.jobName;
    }
}
