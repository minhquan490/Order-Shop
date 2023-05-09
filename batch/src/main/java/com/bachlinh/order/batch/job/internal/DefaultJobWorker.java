package com.bachlinh.order.batch.job.internal;

import com.bachlinh.order.batch.Report;
import com.bachlinh.order.batch.job.Job;
import com.bachlinh.order.batch.job.Worker;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

class DefaultJobWorker implements Worker {
    private final List<Report> reports = new ArrayList<>();
    private Timer timer;

    DefaultJobWorker(Job job) {
        timer = new Timer(job.getName());
        timer.schedule(buildTask(job), 1000L * 5L, 100);
    }

    @Override
    public Collection<Report> getAllReport() {
        try {
            return reports;
        } finally {
            reports.clear();
        }
    }

    @Override
    public void stop() {
        timer = null;
    }

    private TimerTask buildTask(Job job) {
        return new TimerTask() {
            @Override
            public void run() {
                LocalDateTime timeExecute = job.getNextExecutionTime();
                LocalDateTime now = LocalDateTime.now();
                if (!timeExecute.isAfter(now)) {
                    job.execute();
                    reports.add(job.getJobReport());
                }
            }
        };
    }
}
