package com.bachlinh.order.batch.job.handler;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.BatchJob;
import com.bachlinh.order.batch.job.AbstractJob;
import com.bachlinh.order.batch.job.JobType;
import com.bachlinh.order.crawler.core.visitor.InnerElementVisitor;
import com.bachlinh.order.crawler.core.visitor.PageVisitor;
import com.bachlinh.order.crawler.core.writer.ElementWriter;
import com.bachlinh.order.crawler.driver.Driver;
import com.bachlinh.order.crawler.loader.DriverLoader;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.system.crawler.AwakeCrawlerException;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@BatchJob(name = "crawler")
@ActiveReflection
public class Crawler extends AbstractJob {
    private static final String FILE_TEMPLATE = "{0}/{1}.json";
    private boolean isAlive;
    private Driver driver;
    private final String browserPath;
    private Environment crawlerEnvironment;

    @ActiveReflection
    public Crawler(String name, String activeProfile, DependenciesResolver dependenciesResolver) {
        super(name, activeProfile, dependenciesResolver);
        this.browserPath = getEnvironment().getProperty("server.browser.path");
    }

    public void awake() {
        if (isAlive) {
            throw new AwakeCrawlerException("Crawler is really alive");
        }
        isAlive = true;
        driver = DriverLoader.simpleLoader().load(browserPath, null);
    }

    public void destroy() {
        if (!isAlive) {
            return;
        }
        isAlive = false;
        driver.quit();
        driver = null;
    }

    @Override
    protected void inject() {
        if (crawlerEnvironment == null) {
            crawlerEnvironment = Environment.getInstance("crawler");
        }
    }

    @Override
    protected void doExecuteInternal() throws Exception {
        String tempPath = crawlerEnvironment.getProperty("temp.file.path");
        Path tempFolderPath = Path.of(tempPath);
        if (Files.exists(tempFolderPath)) {
            Files.createDirectory(tempFolderPath);
        }
        awake();
        try {
            ElementWriter writer = ElementWriter.channelWriter(MessageFormat.format(FILE_TEMPLATE, tempPath, LocalDate.now().toString()));
            String classList = crawlerEnvironment.getProperty("element.data.classes");
            String[] urls = crawlerEnvironment.getProperty("target.urls").split(",");
            for (String url : urls) {
                if (!url.isBlank()) {
                    PageVisitor pageVisitor = new PageVisitor(driver, url);
                    pageVisitor.visit();
                    List<InnerElementVisitor> visitors = pageVisitor.getElementByClassName(classList);
                    visitors.forEach(visitor -> {
                        try {
                            writer.write(visitor);
                        } catch (IOException e) {
                            addException(e);
                        }
                    });
                }
            }
        } finally {
            destroy();
        }
    }

    @Override
    protected LocalDateTime doGetNextExecutionTime() {
        return doGetPreviousExecutionTime().plusDays(1);
    }

    @Override
    protected LocalDateTime doGetPreviousExecutionTime() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0));
    }

    @Override
    public JobType getJobType() {
        return JobType.DAILY;
    }
}
