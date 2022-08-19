package com.hqh.quizserver.services;

import com.hqh.quizserver.dto.UserReportDTO;

public interface UserReportService {

    void saveReport(UserReportDTO userReportDto);

    void deleteTopic(Long id);

}