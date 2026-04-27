package com.sudhakarproject.service;

import com.sudhakarproject.dto.JobRequest;
import com.sudhakarproject.exceptions.BatchJobFailedException;
import com.sudhakarproject.exceptions.OracleDatabaseDownException;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobOperator jobOperator;
    private final Job job;
    private final JobAuditService jobAuditService;
    private final JdbcTemplate oracleJdbcTemplate;

    public String runJob(JobRequest request) {

        Long jobId = System.currentTimeMillis();

        try {
            jobAuditService.saveJobStarted(
                    jobId,
                    String.join(",",request.getClientIds()),
                    request.getAccountType());

            validateOracle();

            JobParameters params = new JobParametersBuilder()
                    .addLong("jobId", jobId)
                    .addLong("run.id", System.currentTimeMillis())
                    .addString("clientIds",
                            String.join(",", request.getClientIds()))
                    .addString("accountType",
                            request.getAccountType())
                    .addString("lastUpdatedBefore",
                            request.getLastUpdatedBefore().toString())
                    .toJobParameters();

            jobOperator.start(job, params);

            return "Job Submitted Successfully";


        }catch (OracleDatabaseDownException | JobInstanceAlreadyCompleteException |
                JobExecutionAlreadyRunningException | InvalidJobParametersException | JobRestartException ex){
            jobAuditService.markFailed(jobId,ex.getMessage());
            throw new OracleDatabaseDownException("Unable to start batch job");
        }

    }

    private void validateOracle() {
        try {
            oracleJdbcTemplate.execute("SELECT 1 FROM dual");
        } catch (Exception ex) {
            throw new OracleDatabaseDownException(
                    "Oracle DB unavailable");
        }
    }
}
