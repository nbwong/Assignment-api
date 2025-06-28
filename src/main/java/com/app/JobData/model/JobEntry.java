package com.app.JobData.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobEntry{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String timestamp;
    private String employer;
    private String location;
    private String jobTitle;
    private String yearsAtEmployer;
    private String yearsOfExperience;
    private BigDecimal salary;
    private String signingBonus;
    private String annualBonus;
    private String annualStockValueBonus;
    private String gender;
    @Column(length = 4000)
    private String additionalComments;
}