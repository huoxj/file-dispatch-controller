package com.fdc.repository;

import com.fdc.po.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LogRepository extends JpaRepository<Log, String>, JpaSpecificationExecutor<Log> {
}
