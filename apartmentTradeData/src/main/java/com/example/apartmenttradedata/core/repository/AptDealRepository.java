package com.example.apartmenttradedata.core.repository;

import com.example.apartmenttradedata.core.entity.Apt;
import com.example.apartmenttradedata.core.entity.AptDeal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AptDealRepository extends JpaRepository<AptDeal, Long> {

    Optional<AptDeal> findAptDealByAptAndExclusiveAreaAndDealDateAndDealAmountAndFloor(
            Apt apt, Double exclusiveArea, LocalDate dealDate, Long dealAmount, Integer floor
    );

    List<AptDeal> findByDealCanceledIsFalseAndDealDateEquals(LocalDate localDate);
}
