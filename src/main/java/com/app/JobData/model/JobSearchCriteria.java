package com.app.JobData.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class JobSearchCriteria {
    private String jobTitle;
    private BigDecimal salaryGte;
    private BigDecimal salaryLte;
    private String gender;
    private String sortField;
    private String sortType;
}