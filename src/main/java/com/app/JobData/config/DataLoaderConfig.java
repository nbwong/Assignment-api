package com.app.JobData.config;


import com.app.JobData.model.JobEntry;
import com.app.JobData.repository.JobRepository;
import com.app.JobData.util.DataCleanerUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DataLoaderConfig {
    private final JobRepository jobRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public DataLoaderConfig(JobRepository jobRepository, ObjectMapper objectMapper) {
        this.jobRepository = jobRepository;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void loadData() {
        try {
            InputStream inputStream = new ClassPathResource("salary_survey-3.json").getInputStream();
            List<java.util.Map<String, String>> rawData = objectMapper.readValue(inputStream, new TypeReference<>() {
            });

            List<JobEntry> jobEntries = rawData.stream()
                    .map(DataCleanerUtil::cleanAndMapToJobEntry)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());

            jobRepository.saveAll(jobEntries);

            log.info("Loaded {} job ", jobEntries.size());

        } catch (IOException e) {
            log.error("Failed to load salary survey data: {}", e.getMessage());
        }
    }
}
