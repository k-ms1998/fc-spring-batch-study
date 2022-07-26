package com.fc.housebatch.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FakeSendService implements SendService {
    @Override
    public void send(String email, String message) {
        System.out.println("[Email] : " + email + "\n" +
                "[Message] : " + message);

    }
}
