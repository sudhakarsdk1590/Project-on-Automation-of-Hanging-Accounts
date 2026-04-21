package com.sudhakarproject.repository;

import com.sudhakarproject.entity.JobAuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobAuditEventRepository extends JpaRepository<JobAuditEvent,Long> {
}
