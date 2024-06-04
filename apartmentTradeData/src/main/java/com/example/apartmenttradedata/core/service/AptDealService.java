package com.example.apartmenttradedata.core.service;

import com.example.apartmenttradedata.core.dto.AptDealDto;
import com.example.apartmenttradedata.core.dto.AptDto;
import com.example.apartmenttradedata.core.entity.Apt;
import com.example.apartmenttradedata.core.entity.AptDeal;
import com.example.apartmenttradedata.core.repository.AptDealRepository;
import com.example.apartmenttradedata.core.repository.AptRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AptDealDto에 있는 값을 Apt, AptDeal 엔티티로 저장한다.
 */
@AllArgsConstructor
@Service
public class AptDealService {
    private final AptRepository aptRepository;
    private final AptDealRepository aptDealRepository;

    @Transactional
    public void upsert(AptDealDto aptDealDto) {
        Apt apt = getAptOrNew(aptDealDto);
        saveAptDeal(aptDealDto, apt);
    }

    private Apt getAptOrNew(AptDealDto aptDealDto) {
        Apt apt = aptRepository.findAptByAptNameAndJibun(aptDealDto.getAptName(), aptDealDto.getJibun())
                .orElseGet(() -> Apt.from(aptDealDto));
        return aptRepository.save(apt);
    }

    // 정적 생성자 메서드 명을 작성할 때 여러개의 인자가 있을 경우 of 하나 일 경우 from Effective 자바 가이드에 있음
    private void saveAptDeal(AptDealDto aptDealDto, Apt apt) {
        AptDeal aptDeal = aptDealRepository.findAptDealByAptAndExclusiveAreaAndDealDateAndDealAmountAndFloor(
                        apt, aptDealDto.getExclusiveArea(), aptDealDto.getDealDate(), aptDealDto.getDealAmount(), aptDealDto.getFloor())
                .orElseGet(() -> AptDeal.of(aptDealDto, apt));
        aptDeal.setDealCanceled(aptDealDto.isDealCanceled());
        aptDeal.setDealCanceledDate(aptDealDto.getDealCanceledDate());
        aptDealRepository.save(aptDeal);
    }

    public List<AptDto> findByGuLawdCdAndDealDate(String guLawdCd, LocalDate dealDate) {
        return aptDealRepository.findByDealCanceledIsFalseAndDealDateEquals(dealDate)
                .stream()
                .filter(aptDeal -> aptDeal.getApt().getGuLawdCd().equals(guLawdCd))
                .map(aptDeal -> new AptDto(aptDeal.getApt().getAptName(), aptDeal.getDealAmount()))
                .collect(Collectors.toList());
    }
}
