package com.fdc.repository;

import com.fdc.po.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, String> {
    Optional<File> findById(String fileId);

    List<File> findAllByCreateTimeBetween(Date startTime, Date endTime);
}
