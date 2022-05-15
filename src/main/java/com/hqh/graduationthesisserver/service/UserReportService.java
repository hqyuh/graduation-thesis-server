package com.hqh.graduationthesisserver.service;

import com.hqh.graduationthesisserver.dto.UserReportDto;

public interface UserReportService {

    void saveReport(UserReportDto userReportDto);

    void deleteTopic(Long id);

}