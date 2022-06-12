package com.hqh.quizserver.service;

import java.io.ByteArrayInputStream;

public interface UserMarkHelperService {
    ByteArrayInputStream loadUserMarkExcel(long id);
}
