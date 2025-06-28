package com.app.JobData.controller;

import com.app.JobData.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/job_data")
public class JobController {
    private final JobService jobService;

    @Autowired
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getJobData(
            @RequestParam(value = "jobTitle", required = false) String jobTitle,
            @RequestParam(value = "salary[gte]", required = false) String salaryGte,
            @RequestParam(value = "salary[lte]", required = false) String salaryLte,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "sort_type", defaultValue = "ASC", required = false) String sortType) {

        List<Map<String, Object>> result = jobService.searchJobEntries(
                jobTitle, salaryGte, salaryLte, gender, fields, sort, sortType);

        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(result);
    }
}
