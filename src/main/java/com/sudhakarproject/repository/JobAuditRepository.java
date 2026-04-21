package com.sudhakarproject.repository;

import com.sudhakarproject.entity.JobAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobAuditRepository extends JpaRepository<JobAudit,Long> {
    Optional<JobAudit> findByJobId(Long jobId);
}
