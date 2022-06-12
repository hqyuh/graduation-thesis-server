package com.hqh.quizserver.service;

import com.hqh.quizserver.dto.UserReportDto;

public interface UserReportService {

    void saveReport(UserReportDto userReportDto);

    void deleteTopic(Long id);

}