package com.sudhakarproject.service;

import com.sudhakarproject.entity.JobAudit;
import com.sudhakarproject.entity.JobAuditEvent;
import com.sudhakarproject.repository.JobAuditEventRepository;
import com.sudhakarproject.repository.JobAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class JobAuditService {

    private final JobAuditRepository jobAuditRepository;
    private final JobAuditEventRepository jobAuditEventRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveJobStarted(Long jobId,String clientId,String accountType){

        JobAudit jobAudit =  new JobAudit();
        jobAudit.setJobId(jobId);
        jobAudit.setClientIds(clientId);
        jobAudit.setAccountType(accountType);
        jobAudit.setStatus("STARTED");
        jobAudit.setCreatedAt(LocalDateTime.now());
        jobAudit.setErrorMessage("Data Retrived Successfully");

        jobAuditRepository.save(jobAudit);

        saveEvent(jobId,"JOB_STARTED","Job execution started");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markCompleted(Long jobId,
                              Long totalRecords,
                              String fileName) {

        JobAudit audit = jobAuditRepository.findByJobId(jobId).orElseThrow();

        audit.setStatus("COMPLETED");
        audit.setTotalRecords(totalRecords);
        audit.setFileName(fileName);
        audit.setCompletedAt(LocalDateTime.now());

        jobAuditRepository.save(audit);

        saveEvent(jobId, "JOB_COMPLETED", "Job completed successfully");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(Long jobId,
                           String error) {

        JobAudit audit = jobAuditRepository.findByJobId(jobId).orElseThrow();

        audit.setStatus("FAILED");
        audit.setErrorMessage(error);
        audit.setCompletedAt(LocalDateTime.now());

        jobAuditRepository.save(audit);

        saveEvent(jobId, "JOB_FAILED", error);
    }

    public void saveEvent(Long jobId,String type,String msg){

        JobAuditEvent jobAuditEvent = new JobAuditEvent();
        jobAuditEvent.setJobId(jobId);
        jobAuditEvent.setEventType(type);
        jobAuditEvent.setMessage(msg);
        jobAuditEvent.setEventTime(LocalDateTime.now());

        jobAuditEventRepository.save(jobAuditEvent);
    }
}
