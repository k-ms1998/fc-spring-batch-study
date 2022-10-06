package com.fc.project.core.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BCryptEncryptorTest {

    @Test
    void given_when_then() throws Exception {
        // Given
        final String origin = "password";
        final String fakeOrigin = "fake";
        final Encryptor encryptor = new BCryptEncryptor();

        // When
        final String hashed = encryptor.encrypt(origin);

        // Then
        Assertions.assertThat(encryptor.isMatch(origin, hashed)).isTrue();
        Assertions.assertThat(encryptor.isMatch(fakeOrigin, hashed)).isFalse();

    }
}