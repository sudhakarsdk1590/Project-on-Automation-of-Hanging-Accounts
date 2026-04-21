package com.sudhakarproject.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_audit_event")
@Data
public class JobAuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long jobId;
    private String eventType;
    private String message;
    private LocalDateTime eventTime;
}
