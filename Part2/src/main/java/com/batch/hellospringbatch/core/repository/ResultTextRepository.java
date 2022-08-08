package com.batch.hellospringbatch.core.repository;

import com.batch.hellospringbatch.core.domain.ResultText;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultTextRepository extends JpaRepository<ResultText, Long> {
}
