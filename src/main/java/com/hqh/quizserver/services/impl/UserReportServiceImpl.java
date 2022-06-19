package com.hqh.quizserver.services.impl;

import com.hqh.quizserver.dto.UserReportDto;
import com.hqh.quizserver.mapper.UserReportMapper;
import com.hqh.quizserver.repositories.UserReportRepository;
import com.hqh.quizserver.services.UserReportService;
import com.hqh.quizserver.services.UserService;
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
