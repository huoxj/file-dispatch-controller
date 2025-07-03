package com.fdc.serviceImpl.share;

import com.fdc.po.Share;
import com.fdc.repository.ShareRepository;
import com.fdc.service.ShareService;
import com.fdc.vo.share.ShareVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShareServiceImpl implements ShareService {

    @Autowired
    private ShareRepository shareRepository;

    @Override
    public List<ShareVO> listShares(List<String> userIds, List<String> fileIds) {
        Specification<Share> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (userIds != null && !userIds.isEmpty()) {
                predicates.add(root.get("userId").in(userIds));
            }
            if (fileIds != null && !fileIds.isEmpty()) {
                predicates.add(root.get("fileId").in(fileIds));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        List<Share> shares = shareRepository.findAll(spec);
        List<ShareVO> shareVOs = shares.stream().map(Share::toVO).collect(Collectors.toList());
        return shareVOs;
    }
}
