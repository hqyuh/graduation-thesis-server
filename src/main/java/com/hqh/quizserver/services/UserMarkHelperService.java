package com.hqh.quizserver.services;

import java.io.ByteArrayInputStream;

public interface UserMarkHelperService {
    ByteArrayInputStream loadUserMarkExcel(long id);
}
