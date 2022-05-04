package com.hqh.graduationthesisserver.service;

import java.io.ByteArrayInputStream;

public interface UserMarkHelperService {
    ByteArrayInputStream loadUserMarkExcel(long id);
}
