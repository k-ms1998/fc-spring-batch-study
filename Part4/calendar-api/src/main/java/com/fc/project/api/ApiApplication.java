package com.fc.project.api;

import com.fc.project.core.domain.entity.TestEntity;
import com.fc.project.core.repository.TestEntityRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @EntityScan: 다른 모듈에 있는 엔티티 스캔하기
 * @EnableJpaRepositories: 다른 모둘에 있는 레포지토리 스캔하기
 */
@EntityScan("com.fc.project.core")
@EnableJpaRepositories("com.fc.project.core")
@EnableJpaAuditing
@RestController
@SpringBootApplication(scanBasePackages = "com.fc.project")
public class ApiApplication {

    private final TestEntityRepository testEntityRepository;

    public ApiApplication(TestEntityRepository testEntityRepository) {
        this.testEntityRepository = testEntityRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

    @GetMapping
    public List<TestEntity> findAll() {
        return testEntityRepository.findAll();
    }

    @PostMapping
    public TestEntity saveOne() {
        return testEntityRepository.save(new TestEntity("hello"));
    }
}
