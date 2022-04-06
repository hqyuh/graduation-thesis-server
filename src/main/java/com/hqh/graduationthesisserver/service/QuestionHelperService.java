package com.hqh.graduationthesisserver.service;

import org.springframework.web.multipart.MultipartFile;

public interface QuestionHelperService {

    void saveFile(MultipartFile multipartFile, Long id);

}
