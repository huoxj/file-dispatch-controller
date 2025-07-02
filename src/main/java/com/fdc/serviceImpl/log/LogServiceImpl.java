package com.fdc.serviceImpl.log;

import com.fdc.po.Log;
import com.fdc.repository.LogRepository;
import com.fdc.service.LogService;
import com.fdc.vo.log.LogType;
import com.fdc.vo.log.LogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogRepository logRepository;

    @Override
    public void log(LogVO logVO) {
        Log log = logVO.toPO();
        logRepository.save(log);
    }

    @Override
    public List<LogVO> queryLogs(List<String> fileIds,
                                 List<String> userIds,
                                 List<LogType> types,
                                 List<String> ips,
                                 Date startTime, Date endTime) {

        Specification<Log> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (fileIds != null && !fileIds.isEmpty()) {
                predicates.add(root.get("fileId").in(fileIds));
            }
            if (userIds != null && !userIds.isEmpty()) {
                predicates.add(root.get("userId").in(userIds));
            }
            if (types != null && !types.isEmpty()) {
                predicates.add(root.get("type").in(types));
            }
            if (ips != null && !ips.isEmpty()) {
                predicates.add(root.get("ip").in(ips));
            }
            if (startTime != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("time"), startTime));
            }
            if (endTime != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("time"), endTime));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Log> logs = logRepository.findAll(spec);
        List<LogVO> logVOs = logs.stream().map(Log::toVO).collect(Collectors.toList());
        return logVOs;
    }
}
