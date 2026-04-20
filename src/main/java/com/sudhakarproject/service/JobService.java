package com.sudhakarproject.service;

import com.sudhakarproject.dto.JobRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobOperator jobOperator;
    private final Job job;

    public String runJob(JobRequest request) {

        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("clientIds", String.join(",", request.getClientIds()))
                    .addString("accountType", request.getAccountType())
                    .addString("lastUpdatedBefore", request.getLastUpdatedBefore().toString())
                    .addLong("run.id", System.currentTimeMillis())
                    .addLong("jobId", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution execution = jobOperator.start(job, params);

            return "Job Started with Execution ID: " + execution.getId();
        } catch (Exception e) {
            return "Job failed: " + e.getMessage();
        }
    }
}
