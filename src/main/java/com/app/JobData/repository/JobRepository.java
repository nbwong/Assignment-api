package com.app.JobData.repository;

import com.app.JobData.model.JobEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<JobEntry, Long>, JpaSpecificationExecutor<JobEntry> {

}
