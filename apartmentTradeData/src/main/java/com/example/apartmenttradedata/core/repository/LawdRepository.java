package com.example.apartmenttradedata.core.repository;

import com.example.apartmenttradedata.core.entity.Lawd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface LawdRepository extends JpaRepository<Lawd, Long> {
    Optional<Lawd> findByLawdCd(String lawdCd);

    //select distinct substring(lawd_cd, 1, 5) FROM lawd WHERE exist = 1 and lawd_cd not like "%00000000";

    //@Query annotation

    @Query("select distinct substring(l.lawdCd, 1, 5) from Lawd l where l.exist = 1 and l.lawdCd  not like '%00000000'")
    List<String> findDistinctGuLawdCd();

}
