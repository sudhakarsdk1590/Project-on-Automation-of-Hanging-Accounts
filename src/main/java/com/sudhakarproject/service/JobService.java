package com.sudhakarproject.service;

import com.sudhakarproject.dto.JobRequest;
import com.sudhakarproject.exceptions.BatchJobFailedException;
import com.sudhakarproject.exceptions.InvalidRequestException;
import com.sudhakarproject.exceptions.OracleDatabaseDownException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.time.LocalDate;
import java.util.List;

@Slf4j
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
            validateRequest(request);

            jobAuditService.saveJobStarted(
                    jobId,
                    String.join(",",request.getClientIds()),
                    request.getAccountType());

            log.info("Job {} audit row created", jobId);

            validateOracle();

            log.info("Oracle DB validated successfully for Job {}", jobId);

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

            log.info("Job {} submitted successfully", jobId);

            jobAuditService.markRunning(jobId);

            return "Job Submitted Successfully";


        }catch (OracleDatabaseDownException ex){
            log.error("Oracle unavailable for Job {}", jobId, ex);
            jobAuditService.markFailed(jobId,"Oracle DB unavailable");
            throw ex;
        }
        catch (InvalidRequestException ex){
            log.error("Invalid request for Job {}", jobId, ex);

//            jobAuditService.markFailed(
//                    jobId,
//                    ex.getMessage()
//            );

            throw ex;
        }
        catch (Exception ex){
            log.error("Batch launch failed for Job {}", jobId, ex);

            jobAuditService.markFailed(
                    jobId,
                    ex.getMessage()
            );

            throw new BatchJobFailedException(
                    "Unable to start batch job",
                    ex
            );
        }

    }

    private void validateRequest(JobRequest request){

        if (request == null){
            throw new InvalidRequestException("Request cannot be null");
        }

        List<String> clientIds = request.getClientIds();

        if (clientIds.isEmpty() || clientIds == null){
            throw new InvalidRequestException("ClientIds cannot be null");
        }

        if (clientIds.size() > 10){
            throw new InvalidRequestException("Maximum 10 ClientIds are allowed");
        }

        for (String clientId : clientIds){
            if (clientId ==  null || clientId.isBlank()){
                throw new InvalidRequestException("ClientIds cannot be Blank");
            }

            if (!clientId.matches("\\d+")){
                throw new InvalidRequestException("ClientId must contain numbers only : " + clientId);
            }
        }

        if (request.getAccountType() == null || request.getAccountType().isBlank()){
            throw new InvalidRequestException("Account Type is required");
        }

       LocalDate date = request.getLastUpdatedBefore();

        if (date ==  null){
            throw new InvalidRequestException("Last Updated Before date is required");
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
