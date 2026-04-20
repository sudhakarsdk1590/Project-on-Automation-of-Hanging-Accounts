package com.sudhakarproject.joblistener;

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

    @Override
    public void afterJob(JobExecution jobExecution) {

        long total = 0;
        long hanging = 0;

        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            ExecutionContext ctx = stepExecution.getExecutionContext();

            total += ctx.getLong("total", 0);
            hanging += ctx.getLong("hanging", 0);
        }

        try {
            summaryWriter.write(jobExecution, total, hanging);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
