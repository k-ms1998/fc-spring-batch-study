package com.fc.project.core.util;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

/**
 * BCryptEncryptor 를 빈 등록 시킴
 * 그러므로, 다른 위치에서 Encryptor 를 주입 받을때 BCryptEncryptor 가 자동으로 주입됨
 * -> BCryptEncryptor 에서 개발한 로직들로 encrypt, isMatch 메서드들이 호출됨
 */
@Component
public class BCryptEncryptor implements Encryptor {

    @Override
    public String encrypt(String origin) {
        return BCrypt.hashpw(origin, BCrypt.gensalt());
    }

    @Override
    public boolean isMatch(String origin, String hashed) {
        try {
            return BCrypt.checkpw(origin, hashed);
        } catch (Exception e) {
            return false;
        }
    }
}
