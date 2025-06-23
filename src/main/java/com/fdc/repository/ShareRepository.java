package com.fdc.repository;

import com.fdc.po.Share;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShareRepository extends JpaRepository<Share, String> {
    boolean existsByFileIdAndUserId(String fileId, String userId);
}
