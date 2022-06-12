package com.hqh.quizserver.service;

import org.springframework.web.multipart.MultipartFile;

public interface QuestionHelperService {

    void saveFile(MultipartFile multipartFile, Long id);

}
