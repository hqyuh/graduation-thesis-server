package com.hqh.graduationthesisserver.service;

import com.hqh.graduationthesisserver.dto.ReviewAnswerDto;
import com.hqh.graduationthesisserver.dto.UserAnswerDto;

import java.util.List;


public interface UserAnswerService {

    void saveAllUserAnswer(List<UserAnswerDto> userAnswerDto);

    List<ReviewAnswerDto> reviewAnswerUser(Long quizzId);

}
