package com.fdc.repository;

import com.fdc.po.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Arrays;
import java.util.List;

public interface ShareRepository extends JpaRepository<Share, String>, JpaSpecificationExecutor<Share> {
    boolean existsByFileIdAndUserId(String fileId, String userId);

    Share findByUserIdAndFileId(String userId, String fileId);

    List<Share> findAllByUserIdInAndFileId(List<String> userIds, String fileId);
}
