package com.app.JobData.jobspecification;

import com.app.JobData.model.JobEntry;
import com.app.JobData.model.JobSearchCriteria;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class JobSpecification {

    public static Specification<JobEntry> withCriteria(JobSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(criteria.getJobTitle())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("jobTitle")),
                        "%" + criteria.getJobTitle().toLowerCase() + "%"
                ));
            }

            if (criteria.getSalaryGte() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("salary"),
                        criteria.getSalaryGte()
                ));
            }

            if (criteria.getSalaryLte() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("salary"),
                        criteria.getSalaryLte()
                ));
            }

            if (StringUtils.hasText(criteria.getGender())) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("gender")),
                        criteria.getGender().toLowerCase()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
