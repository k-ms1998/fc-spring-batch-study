package com.batch.hellospringbatch.core.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "result_text")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResultText {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String text;

    public ResultText(String text) {
        this.text = text;
    }

    public void updateText(String text) {
        this.text = text;
    }
}
