package com.sudhakarproject.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class JobRequest {
    private String accountType;
    private LocalDate lastUpdatedBefore;
    private List<String> clientIds;
}
