package com.bachlinh.order.batch.job;

import com.bachlinh.order.batch.Report;

import java.util.Collection;

public interface Worker {

    Collection<Report> getAllReport();

    void stop();
}
