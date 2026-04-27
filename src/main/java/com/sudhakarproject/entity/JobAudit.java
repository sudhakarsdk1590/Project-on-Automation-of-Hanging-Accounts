package com.sudhakarproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_audit")
@Data
public class JobAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long jobId;
    private String clientIds;
    private String accountType;
    private String status;
    private Long totalRecords;
    private String fileName;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
