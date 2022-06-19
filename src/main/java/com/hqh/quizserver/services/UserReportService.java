package com.hqh.quizserver.services;

import com.hqh.quizserver.dto.UserReportDto;

public interface UserReportService {

    void saveReport(UserReportDto userReportDto);

    void deleteTopic(Long id);

}