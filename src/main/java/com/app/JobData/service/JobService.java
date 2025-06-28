package com.app.JobData.service;

import com.app.JobData.jobspecification.JobSpecification;
import com.app.JobData.model.JobEntry;
import com.app.JobData.model.JobSearchCriteria;
import com.app.JobData.repository.JobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JobService {
    private final JobRepository jobRepository;

    @Autowired
    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public List<Map<String, Object>> searchJobEntries(
            String jobTitle, String salaryGte, String salaryLte, String gender,
            String fields, String sort, String sortType) {

        JobSearchCriteria criteria = new JobSearchCriteria();
        criteria.setJobTitle(jobTitle);
        try {
            if (StringUtils.hasText(salaryGte)) {
                criteria.setSalaryGte(new BigDecimal(salaryGte));
            }
        } catch (NumberFormatException e) {
            log.error("Invalid salaryGte format: " + salaryGte);
        }
        try {
            if (StringUtils.hasText(salaryLte)) {
                criteria.setSalaryLte(new BigDecimal(salaryLte));
            }
        } catch (NumberFormatException e) {
            log.error("Invalid salaryLte format: " + salaryLte);
        }
        criteria.setGender(gender);
        criteria.setSortField(sort);
        criteria.setSortType(sortType);

        Specification<JobEntry> spec = JobSpecification.withCriteria(criteria);

        Sort sortObj = Sort.unsorted();
        if (StringUtils.hasText(criteria.getSortField())) {
            Sort.Direction direction = "DESC".equalsIgnoreCase(criteria.getSortType()) ? Sort.Direction.DESC : Sort.Direction.ASC;
            String camelCaseSortField = snakeToCamelCase(criteria.getSortField());
            sortObj = Sort.by(direction, camelCaseSortField);
        }

        List<JobEntry> filteredAndSortedEntries = jobRepository.findAll(spec, sortObj);

        if (StringUtils.hasText(fields)) {
            return applySparseFields(filteredAndSortedEntries, fields);
        } else {
            return filteredAndSortedEntries.stream()
                    .map(this::convertJobEntryToMap)
                    .collect(Collectors.toList());
        }
    }

    private List<Map<String, Object>> applySparseFields(List<JobEntry> entries, String fields) {
        String[] fieldNames = fields.split(",");
        return entries.stream()
                .map(jobEntry -> {
                    Map<String, Object> selectedFields = new java.util.HashMap<>();
                    for (String fieldName : fieldNames) {
                        try {
                            Field field;
                            String trimmedFieldName = fieldName.trim();
                            String camelCaseFieldName = snakeToCamelCase(trimmedFieldName);
                            field = JobEntry.class.getDeclaredField(camelCaseFieldName);
                            field.setAccessible(true);
                            selectedFields.put(fieldName.trim(), field.get(jobEntry));
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            log.error("Could not access field for sparse selection: {} {}", fieldName, e.getMessage());
                        }
                    }
                    return selectedFields;
                })
                .collect(Collectors.toList());
    }

    private String snakeToCamelCase(String snakeCaseString) {
        if (snakeCaseString == null || !snakeCaseString.contains("_")) {
            return snakeCaseString; // Already camelCase or single word
        }
        Pattern pattern = Pattern.compile("_([a-z])");
        Matcher matcher = pattern.matcher(snakeCaseString);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private Map<String, Object> convertJobEntryToMap(JobEntry jobEntry) {
        Map<String, Object> map = new java.util.HashMap<>();
        for (Field field : JobEntry.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(jobEntry));
            } catch (IllegalAccessException e) {
                log.error("Could not access field: {} while converting to map. {}", field.getName(), e.getMessage());
            }
        }
        return map;
    }
}
