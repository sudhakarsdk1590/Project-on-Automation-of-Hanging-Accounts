package com.sudhakarproject.writer;

import com.sudhakarproject.summarytracker.SummaryTracker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameter;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
@Slf4j
public class SummaryWriter {

    private static final String OUTPUT_DIR = "output";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm:ss a");

    public void write(JobExecution jobExecution,
                      long totalRecords,
                      long hangingAccounts) throws Exception {

        Long jobId = getJobId(jobExecution);

        File dir = new File(OUTPUT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName =
                OUTPUT_DIR + "/job_" + jobId + "_summary.txt";

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(fileName))) {

            LocalDateTime start =
                    toLocalDateTime(jobExecution.getStartTime());

            LocalDateTime end =
                    toLocalDateTime(jobExecution.getEndTime());

            writer.write(line());
            writer.newLine();
            writer.write("           HANGING ACCOUNTS JOB SUMMARY");
            writer.newLine();
            writer.write(line());
            writer.newLine();
            writer.newLine();

            /*==================================================
                JOB DETAILS
            ==================================================*/
            section(writer, "JOB DETAILS");

            field(writer, "Job ID", value(jobId));
            field(writer, "Execution ID",
                    value(jobExecution.getId()));

            field(writer, "Status",
                    value(jobExecution.getStatus()));

            field(writer, "Exit Code",
                    value(jobExecution.getExitStatus()
                            .getExitCode()));

            field(writer, "Start Time", format(start));
            field(writer, "End Time", format(end));
            field(writer, "Duration",
                    calculateDuration(start, end));

            writer.newLine();

            /*==================================================
                INPUT PARAMETERS
            ==================================================*/
            section(writer, "INPUT PARAMETERS");

            field(writer, "Client IDs",
                    safe(jobExecution.getJobParameters()
                            .getString("clientIds")));

            field(writer, "Account Type",
                    safe(jobExecution.getJobParameters()
                            .getString("accountType")));

            field(writer, "Last Updated Before",
                    safe(jobExecution.getJobParameters()
                            .getString("lastUpdatedBefore")));

            writer.newLine();

            /*==================================================
                PROCESSING SUMMARY
            ==================================================*/
            section(writer, "PROCESSING SUMMARY");

            long read = 0;
            long write = 0;
            long skip = 0;
            long commit = 0;

            for (StepExecution step :
                    jobExecution.getStepExecutions()) {

                read += step.getReadCount();
                write += step.getWriteCount();
                skip += step.getSkipCount();
                commit += step.getCommitCount();
            }

            field(writer, "Total Read", value(read));
            field(writer, "Total Written", value(write));
            field(writer, "Total Records", value(totalRecords));
            field(writer, "Hanging Accounts",
                    value(hangingAccounts));
            field(writer, "Skipped Records",
                    value(skip));
            field(writer, "Commit Count",
                    value(commit));

            writer.newLine();

            /*==================================================
                STEP DETAILS
            ==================================================*/
            section(writer, "STEP DETAILS");

            for (StepExecution step :
                    jobExecution.getStepExecutions()) {

                field(writer, "Step Name",
                        step.getStepName());

                field(writer, "Step Status",
                        step.getStatus().toString());

                field(writer, "Read Count",
                        value(step.getReadCount()));

                field(writer, "Write Count",
                        value(step.getWriteCount()));

                field(writer, "Skip Count",
                        value(step.getSkipCount()));

                writer.write(line());
                writer.newLine();
            }

            /*==================================================
                FAILURE DETAILS
            ==================================================*/
            if (jobExecution.getStatus() ==
                    BatchStatus.FAILED) {

                writer.newLine();

                section(writer, "FAILURE DETAILS");

                if (jobExecution
                        .getAllFailureExceptions()
                        .isEmpty()) {

                    field(writer,
                            "Reason",
                            "Unknown Error");

                } else {

                    field(writer,
                            "Reason",
                            jobExecution
                                    .getAllFailureExceptions()
                                    .get(0)
                                    .getMessage());
                }
            }

            writer.newLine();

            /*==================================================
                FOOTER
            ==================================================*/
            writer.write(line());
            writer.newLine();
            writer.write(
                    "Generated By : Hanging Accounts Automation System");
            writer.newLine();
            writer.write(
                    "Generated On : " +
                            LocalDateTime.now()
                                    .format(FORMATTER));
            writer.newLine();
            writer.write(line());
        }

        log.info("Summary file generated : {}", fileName);
    }

    /*======================================================
        HELPERS
    ======================================================*/

    private void section(BufferedWriter writer,
                         String title) throws Exception {

        writer.write(line());
        writer.newLine();
        writer.write(title);
        writer.newLine();
        writer.write(line());
        writer.newLine();
    }

    private void field(BufferedWriter writer,
                       String key,
                       String value) throws Exception {

        writer.write(String.format("%-22s : %s",
                key,
                value));

        writer.newLine();
    }

    private String line() {
        return "============================================================";
    }

    private String value(Object obj) {
        return obj == null ? "N/A" : obj.toString();
    }

    private String safe(String val) {
        return val == null ? "N/A" : val;
    }

    private String format(LocalDateTime dt) {
        return dt == null
                ? "N/A"
                : dt.format(FORMATTER);
    }

    private Long getJobId(JobExecution jobExecution) {

        Long jobId =
                jobExecution.getJobParameters()
                        .getLong("jobId");

        return jobId == null
                ? System.currentTimeMillis()
                : jobId;
    }

    private LocalDateTime toLocalDateTime(
            java.time.LocalDateTime dt) {

        return dt;
    }

    private String calculateDuration(
            LocalDateTime start,
            LocalDateTime end) {

        if (start == null || end == null) {
            return "N/A";
        }

        Duration duration =
                Duration.between(start, end);

        long seconds =
                duration.toSeconds();

        long minutes =
                seconds / 60;

        long balance =
                seconds % 60;

        if (minutes > 0) {
            return minutes +
                    " Min " +
                    balance +
                    " Sec";
        }

        return balance + " Sec";
    }
}
