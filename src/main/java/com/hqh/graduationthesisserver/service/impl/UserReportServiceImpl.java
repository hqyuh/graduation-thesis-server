package com.hqh.graduationthesisserver.service.impl;

import com.hqh.graduationthesisserver.domain.UserReport;
import com.hqh.graduationthesisserver.dto.UserReportDto;
import com.hqh.graduationthesisserver.mapper.UserReportMapper;
import com.hqh.graduationthesisserver.repository.UserReportRepository;
import com.hqh.graduationthesisserver.service.UserReportService;
import com.hqh.graduationthesisserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserReportServiceImpl implements UserReportService {

    private final UserReportRepository userReportRepository;
    private final UserReportMapper userReportMapper;
    private final UserService userService;

    @Autowired
    public UserReportServiceImpl(UserReportRepository userReportRepository,
                                 UserReportMapper userReportMapper,
                                 UserService userService) {
        this.userReportRepository = userReportRepository;
        this.userReportMapper = userReportMapper;
        this.userService = userService;
    }

    @Override
    public void saveReport(UserReportDto userReportDto) {

    }

    @Override
    public void deleteTopic(Long id) {

    }

}
