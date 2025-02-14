package com.example.apartmenttradedata.core.entity;

import com.example.apartmenttradedata.core.dto.AptDealDto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Setter
@Getter
@ToString
@Table(name = "apt")
@EntityListeners(AuditingEntityListener.class)
public class Apt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aptId;

    @Column(nullable = false)
    private String aptName;

    @Column(nullable = false)
    private String jibun;

    @Column(nullable = false)
    private String dong;

    @Column(nullable = false)
    private String guLawdCd;

    @Column(nullable = false)
    private Integer builtYear;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public static Apt from(AptDealDto dto) {
        Apt apt = new Apt();
        apt.setAptName(dto.getAptName().trim());
        apt.setJibun(dto.getJibun().trim());
        apt.setDong(dto.getDong().trim());
        apt.setGuLawdCd(dto.getRegionCode().trim());
        apt.setBuiltYear(dto.getBuiltYear());
        return apt;
    }
}
