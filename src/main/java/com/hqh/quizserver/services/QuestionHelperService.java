package com.hqh.quizserver.services;

import org.springframework.web.multipart.MultipartFile;

public interface QuestionHelperService {

    void saveFile(MultipartFile multipartFile, Long id);

}
