package com.batch.hellospringbatch.core.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "plain_text")
public class PlainText {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String text;

    public void updateText(String text) {
        this.text = text;
    }
}
