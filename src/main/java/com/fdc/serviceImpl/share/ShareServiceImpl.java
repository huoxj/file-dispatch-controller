package com.fdc.serviceImpl.share;

import com.fdc.exception.BusinessException;
import com.fdc.po.File;
import com.fdc.po.Share;
import com.fdc.repository.ShareRepository;
import com.fdc.service.FileService;
import com.fdc.service.ShareService;
import com.fdc.serviceImpl.file.excelUtils.TemplateProcesser;
import com.fdc.util.CryptoUtil;
import com.fdc.vo.share.ShareVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShareServiceImpl implements ShareService {

    @Autowired
    private ShareRepository shareRepository;

    @Autowired
    ShareUtil shareUtil;

    @Autowired
    TemplateProcesser excelUtil;

    @Autowired
    private FileService fileService;

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

    @Override
    public ShareVO grantSingle(String userId, String fileId) {
        File file = fileService.getFileById(fileId);
        String encryptedSk = shareUtil.generateShare(file, userId, CryptoUtil.base64Decode(file.getPrivateKey()));

        InputStream data = fileService.getXlsmStream(fileId);
        List<String> userIds = new ArrayList<>(), encryptedSks = new ArrayList<>();
        userIds.add(userId);
        encryptedSks.add(encryptedSk);
        InputStream grantedData = excelUtil.addSharesToFile(data, userIds, encryptedSks);
        fileService.updateFile(fileId, grantedData);
        return shareRepository.findByUserIdAndFileId(userId, fileId).toVO();
    }

    @Override
    public List<ShareVO> grantFilesToUser(String userId, List<String> fileIds){
        List<ShareVO> shareVOs = new ArrayList<>();
        for (String fileId : fileIds) {
            ShareVO shareVO = grantSingle(userId, fileId);
            shareVOs.add(shareVO);
        }
        return shareVOs;
    }

    @Override
    public List<ShareVO> grantUsersToFile(List<String> userIds, String fileId) {
        File file = fileService.getFileById(fileId);
        byte[] sk = CryptoUtil.base64Decode(file.getPrivateKey());
        List<String> encryptedSks = shareUtil.generateShares(file, userIds, sk);

        InputStream data = fileService.getXlsmStream(fileId);
        InputStream grantedData = excelUtil.addSharesToFile(data, userIds, encryptedSks);
        fileService.updateFile(fileId, grantedData);
        return shareRepository.findAllByUserIdInAndFileId(userIds, fileId)
            .stream()
            .map(Share::toVO)
            .collect(Collectors.toList());
    }

    @Override
    public void revokeSingle(String userId, String fileId) {
        InputStream data = fileService.getXlsmStream(fileId);
        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        InputStream revokedData = excelUtil.removeSharesFromFile(data, userIds);
        fileService.updateFile(fileId, revokedData);
    }

    @Override
    public void revokeUserFromFiles(String userId, List<String> fileIds) {
        for (String fileId : fileIds) {
            revokeSingle(userId, fileId);
        }
    }

    @Override
    public void revokeFileFromUsers(List<String> userIds, String fileId) {
        InputStream data = fileService.getXlsmStream(fileId);
        InputStream revokedData = excelUtil.removeSharesFromFile(data, userIds);
        fileService.updateFile(fileId, revokedData);
    }

}
