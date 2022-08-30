package com.fc.housebatch.core.entity;

import com.fc.housebatch.core.dto.AptDealDto;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "apt")
public class Apt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apt_id")
    private Long aptId;

    @Column(name = "apt_name", nullable = false)
    private String aptName;

    @Column(nullable = false)
    private String jibun;

    @Column(nullable = false)
    private String dong;

    @Column(name = "gu_lawd_cd", nullable = false)
    private String guLawdCd;

    @Column(name = "built_year", nullable = false)
    private Integer  builtYear;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Apt(String aptName, String jibun, String dong, String guLawdCd, Integer builtYear) {
        this.aptName = aptName;
        this.jibun = jibun;
        this.dong = dong;
        this.guLawdCd = guLawdCd;
        this.builtYear = builtYear;
    }

    public static Apt of(AptDealDto aptDealDto) {
        String aptName = aptDealDto.getAptName().trim(); // .trim() => 앞 뒤에 붙은 공백 제거
        String jibun = aptDealDto.getJibun().trim();
        String dong = aptDealDto.getDong().trim();
        String guLawdCd = aptDealDto.getRegionCode().trim();
        Integer builtYear = aptDealDto.getBuiltYear();
        
        return new Apt(aptName, jibun, dong, guLawdCd, builtYear);
    }
}
