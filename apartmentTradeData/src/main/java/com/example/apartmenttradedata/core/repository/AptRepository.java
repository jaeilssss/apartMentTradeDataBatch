package com.example.apartmenttradedata.core.repository;

import com.example.apartmenttradedata.core.entity.Apt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AptRepository extends JpaRepository<Apt,Long> {
    Optional<Apt> findAptByAptNameAndJibun(String aptName , String jibun);
}
