package org.esupportail.esupsignature.service;

import org.esupportail.esupsignature.entity.Log;
import org.esupportail.esupsignature.entity.enums.SignRequestStatus;
import org.esupportail.esupsignature.repository.LogRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LogService {

    @Resource
    private LogRepository logRepository;

    @Resource
    private UserService userService;

    public List<Log> getRefuseLogs(Long id) {
        List<Log> logs = logRepository.findBySignRequestIdAndFinalStatus(id, SignRequestStatus.refused.name());
        return setUsers(logs);

    }

    public List<Log> getLogs(Long id) {
        List<Log> logs = logRepository.findBySignRequestIdAndPageNumberIsNotNull(id);
        return setUsers(logs);

    }

    public List<Log> getGlobalLogs(Long id) {
        List<Log> logs = logRepository.findBySignRequestIdAndStepNumberIsNotNull(id);
        return setUsers(logs);
    }

    private List<Log> setUsers(List<Log> logs) {
        for (Log log :logs) {
            log.setUser(userService.getUserByEppn(log.getEppn()));
        }
        return logs;
    }

}