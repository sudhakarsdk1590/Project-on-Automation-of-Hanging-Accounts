package com.sudhakarproject.joblistener;

import com.sudhakarproject.entity.JobAuditEvent;
import com.sudhakarproject.service.JobAuditService;
import com.sudhakarproject.writer.SummaryWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobListener implements JobExecutionListener {

    private final SummaryWriter summaryWriter;
    private final JobAuditService jobAuditService;


    @Override
    public void afterJob(JobExecution jobExecution) {

       Long jobId =  jobExecution.getJobParameters().getLong("jobId");

        long total = 0;
        long hanging = 0;

        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            ExecutionContext ctx = stepExecution.getExecutionContext();

            total += ctx.getLong("total", 0);
            hanging += ctx.getLong("hanging", 0);
        }

        try {
            summaryWriter.write(
                    jobExecution,
                    total,
                    hanging
            );
            if (jobExecution.getStatus()
                    == BatchStatus.COMPLETED) {

                jobAuditService.markCompleted(
                        jobId,
                        jobExecution.getId(),
                        "Processed: " + total +
                                ", Hanging: " + hanging
                );
            }else {
                String error =
                        getFailureMessage(jobExecution);

                jobAuditService.markFailed(
                        jobId,
                        error
                );
            }
        } catch (Exception e) {
            jobAuditService.markFailed(
                    jobId,
                    "Summary generation failed: "
                            + e.getMessage()
            );
        }
    }
    private String getFailureMessage(
            JobExecution jobExecution) {

        if (!jobExecution.getAllFailureExceptions()
                .isEmpty()) {

            return jobExecution
                    .getAllFailureExceptions()
                    .get(0)
                    .getMessage();
        }

        return "Unknown Batch Failure";
    }
}
