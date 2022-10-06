package com.fc.project.core.domain.entity;

import com.fc.project.core.util.Encryptor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private LocalDate birthDay;


    public User(String name, String email, String password, LocalDate birthDay) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.birthDay = birthDay;
    }

    /**
     * Strategy Pattern
     *  -> 인터페이스를 인자로 넘겨줘서 기능을 위임함
     *      -> 기능 테스트를 할떄 편리함
     * @param encryptor
     * @param password
     * @return
     */
    public boolean isMatch(Encryptor encryptor, String password) {
        return encryptor.isMatch(password, this.password);
    }

}
