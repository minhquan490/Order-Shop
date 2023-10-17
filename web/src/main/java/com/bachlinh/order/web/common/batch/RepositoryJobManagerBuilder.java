package com.bachlinh.order.web.common.batch;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;

import com.bachlinh.order.batch.Report;
import com.bachlinh.order.batch.job.JobCenter;
import com.bachlinh.order.batch.job.JobManager;
import com.bachlinh.order.batch.job.internal.AbstractJobManager;
import com.bachlinh.order.batch.job.internal.AbstractJobManagerBuilder;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.core.exception.system.common.CriticalException;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.BatchReport;
import com.bachlinh.order.repository.RepositoryManager;
import com.bachlinh.order.web.repository.spi.BatchReportRepository;

public class RepositoryJobManagerBuilder extends AbstractJobManagerBuilder {

    @Override
    protected JobManager doBuild(DependenciesResolver resolver, String profile, JobCenter.Builder builder) {
        return new RepositoryJobManager(builder, profile, resolver);
    }

    private static class RepositoryJobManager extends AbstractJobManager {

        private static final DateTimeFormatter TIME_CREATED_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        private final RepositoryManager repositoryManager;
        private final EntityFactory entityFactory;
        private final Environment environment;

        private String batchReportStacktraceFolder;

        RepositoryJobManager(JobCenter.Builder jobCenterBuilder, String profile, DependenciesResolver dependenciesResolver) {
            super(jobCenterBuilder, profile, dependenciesResolver);
            this.repositoryManager = dependenciesResolver.resolveDependencies(RepositoryManager.class);
            this.entityFactory = dependenciesResolver.resolveDependencies(EntityFactory.class);
            this.environment = Environment.getInstance(profile);
        }

        protected void saveReport() {
            BatchReportRepository batchReportRepository = repositoryManager.getRepository(BatchReportRepository.class);
            Collection<BatchReport> batchReports = new LinkedList<>();
            for (Report report : reports) {
                BatchReport batchReport = entityFactory.getEntity(BatchReport.class);
                batchReport.setHasError(report.isHasError());
                batchReport.setErrorDetail(report.getError() == null ? "" : stackTraceToFile(report.getError()));
                batchReport.setBatchName(report.getJobName());
                batchReport.setTimeReport(Timestamp.from(Instant.now()));
                batchReports.add(batchReport);
            }
            batchReportRepository.saveAllReport(batchReports);
            reports.clear();
        }

        @Override
        protected Trigger getTrigger() {
            return new CronTrigger("0 0 */3 ? * *");
        }

        private String stackTraceToFile(Exception exception) {
            if (batchReportStacktraceFolder == null) {
                batchReportStacktraceFolder = environment.getProperty("server.batch-report-stacktrace.path");
            }

            createFolderIfNecessary();

            String fileName = getReportFileName();
            Path fileReportPath = Path.of(batchReportStacktraceFolder, fileName);

            createFileIfNecessary(fileReportPath);

            try (RandomAccessFile randomAccessFile = new RandomAccessFile(fileReportPath.toFile(), "rw");
                 FileChannel fileChannel = randomAccessFile.getChannel()) {

                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                exception.printStackTrace(printWriter);

                ByteBuffer stacktraceData = ByteBuffer.wrap(stringWriter.toString().getBytes(StandardCharsets.UTF_8));

                int totalByteWritten = fileChannel.write(stacktraceData);

                if (totalByteWritten != 0) {
                    return fileReportPath.toUri().getPath();
                } else {
                    Files.deleteIfExists(fileReportPath);
                    return "";
                }
            } catch (IOException e) {
                throw new CriticalException("Can not write stacktrace to file", e);
            }
        }

        private void createFolderIfNecessary() {
            Path reportFolderPath = Path.of(batchReportStacktraceFolder);
            if (!Files.exists(reportFolderPath)) {
                try {
                    Files.createDirectories(reportFolderPath);
                } catch (IOException e) {
                    String message = STR. "Can not create folder [\{ batchReportStacktraceFolder }]" ;
                    throw new CriticalException(message);
                }
            }
        }

        private String getReportFileName() {
            String id = UUID.randomUUID().toString();
            LocalDateTime now = LocalDateTime.now();
            String timeCreated = TIME_CREATED_FORMATTER.format(now);
            return STR. "\{ id }_\{ timeCreated }.txt" ;
        }

        private void createFileIfNecessary(Path filePath) {
            if (!Files.exists(filePath)) {
                try {
                    Files.createFile(filePath);
                } catch (IOException e) {
                    String message = STR. "Can not create file [\{ filePath.toUri().getPath() }]" ;
                    throw new CriticalException(message);
                }
            }
        }
    }
}
