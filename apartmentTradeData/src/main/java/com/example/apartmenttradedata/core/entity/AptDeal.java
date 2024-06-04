package com.example.apartmenttradedata.core.entity;

import com.example.apartmenttradedata.core.dto.AptDealDto;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@ToString
@Table(name = "apt_deal")
@EntityListeners(AuditingEntityListener.class)
public class AptDeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long aptDealId;

    @ManyToOne
    @JoinColumn(name = "apt_id")
    private Apt apt;

    @Column(nullable = false)
    private Double exclusiveArea;

    @Column(nullable = false)
    private LocalDate dealDate;

    @Column(nullable = false)
    private Long dealAmount;

    @Column(nullable = false)
    private Integer floor;

    @Column(nullable = false)
    private boolean dealCanceled;

    @Column
    private LocalDate dealCanceledDate;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public static AptDeal of(AptDealDto dto, Apt apt) {
        AptDeal aptDeal = new AptDeal();
        aptDeal.setExclusiveArea(dto.getExclusiveArea());
        aptDeal.setDealDate(dto.getDealDate());
        aptDeal.setDealAmount(dto.getDealAmount());
        aptDeal.setFloor(dto.getFloor());
        aptDeal.setDealCanceled(dto.isDealCanceled());
        aptDeal.setDealCanceledDate(dto.getDealCanceledDate());
        aptDeal.setApt(apt);
        return aptDeal;
    }
}
