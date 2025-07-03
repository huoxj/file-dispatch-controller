package com.fdc.repository;

import com.fdc.po.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ShareRepository extends JpaRepository<Share, String>, JpaSpecificationExecutor<Share> {
    boolean existsByFileIdAndUserId(String fileId, String userId);
}
