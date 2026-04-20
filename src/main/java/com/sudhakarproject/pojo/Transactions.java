package com.sudhakarproject.pojo;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Transactions {
    private Long id;
    private String accountId;
    private String clientId;
    private String accountType;
    private Double amount;
    private String status;
    private Integer retryCount;
    private Date transactionDate;
    private Date lastUpdatedDate;
    private Integer Dpd;
    private Date createdDate;
}
