package com.springboot.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.common.util.DateTimeUtil;
import com.springboot.api.domain.CounselCard;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.Counselee;
import com.springboot.api.dto.counselee.*;
import com.springboot.api.repository.CounselSessionRepository;
import com.springboot.api.repository.CounseleeRepository;
import com.springboot.enums.CardRecordStatus;
import com.springboot.enums.HealthInsuranceType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CounseleeService {

    private final CounseleeRepository counseleeRepository;
    public final CounselSessionRepository counselSessionRepository;
    public final DateTimeUtil dateTimeUtil;

    public SelectCounseleeBaseInformationByCounseleeIdRes selectCounseleeBaseInformation(String counselSessionId) {

        CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
                .orElseThrow(IllegalArgumentException::new);

        Counselee counselee = Optional.ofNullable(counselSession.getCounselee()).orElseThrow(NoContentException::new);

        CounselCard currentCounselCard = counselSession.getCounselCard();

        CounselCard targetCounselCard = (currentCounselCard == null
                || currentCounselCard.getCardRecordStatus().equals(CardRecordStatus.RECORDING))
                        ? getPreviousCounselCard(counselee.getId(), counselSession.getScheduledStartDateTime())
                        : currentCounselCard;

        List<String> diseases = new ArrayList<>();

        if (targetCounselCard != null && targetCounselCard.getHealthInformation() != null) {
            JsonNode healthInfo = targetCounselCard.getHealthInformation();
            JsonNode diseasesInfoJson = healthInfo.get("diseaseInfo");
            JsonNode diseasesJson = diseasesInfoJson != null ? diseasesInfoJson.get("diseases") : null;

            if (diseasesJson != null && diseasesJson.isArray()) {
                diseasesJson.forEach(diseaseJson -> diseases.add(diseaseJson.asText()));
            }
        }

        // Step 6: 결과 반환
        return new SelectCounseleeBaseInformationByCounseleeIdRes(counselee.getId(), counselee.getName(),
                dateTimeUtil.calculateKoreanAge(counselee.getDateOfBirth(), LocalDate.now()),
                counselee.getDateOfBirth().toString(), counselee.getGenderType(), counselee.getAddress(),
                counselee.getHealthInsuranceType(), counselee.getCounselCount(), counselee.getLastCounselDate(),
                diseases // diseases
                         // 값
                         // 반환
                , Optional.ofNullable(currentCounselCard).map(CounselCard::getCardRecordStatus)
                        .orElse(CardRecordStatus.UNRECORDED),
                counselee.isDisability());

    }

    @CacheEvict(value = { "birthDates", "welfareInstitutions" }, allEntries = true)
    public String addCounselee(AddCounseleeReq addCounseleeReq) {
        log.info("addCounseleeReq: {}", addCounseleeReq);
        Counselee targetCounselee = Counselee.builder()
                .registrationDate(LocalDate.now())
                .name(addCounseleeReq.getName())
                .phoneNumber(addCounseleeReq.getPhoneNumber())
                .dateOfBirth(addCounseleeReq.getDateOfBirth())
                .genderType(addCounseleeReq.getGenderType())
                .address(addCounseleeReq.getAddress())
                .healthInsuranceType(HealthInsuranceType.NON_COVERED)
                .isDisability(addCounseleeReq.isDisability())
                .note(addCounseleeReq.getNote())
                .careManagerName(addCounseleeReq.getCareManagerName())
                .affiliatedWelfareInstitution(addCounseleeReq.getAffiliatedWelfareInstitution())
                .build();
        log.info("targetCounselee: {}", targetCounselee);
        targetCounselee = counseleeRepository.save(targetCounselee);

        return targetCounselee.getId();
    }

    @CacheEvict(value = { "birthDates", "welfareInstitutions" }, allEntries = true)
    public String updateCounselee(UpdateCounseleeReq updateCounseleeReq) {
        Counselee targetCounselee = counseleeRepository.findById(updateCounseleeReq.getCounseleeId())
                .orElseThrow(IllegalArgumentException::new);
        targetCounselee.setName(updateCounseleeReq.getName());
        targetCounselee.setPhoneNumber(updateCounseleeReq.getPhoneNumber());
        targetCounselee.setDateOfBirth(updateCounseleeReq.getDateOfBirth());
        targetCounselee.setGenderType(updateCounseleeReq.getGenderType());
        targetCounselee.setAddress(updateCounseleeReq.getAddress());
        targetCounselee.setDisability(updateCounseleeReq.isDisability());
        targetCounselee.setNote(updateCounseleeReq.getNote());
        targetCounselee.setCareManagerName(updateCounseleeReq.getCareManagerName());
        targetCounselee.setAffiliatedWelfareInstitution(updateCounseleeReq.getAffiliatedWelfareInstitution());
        targetCounselee = counseleeRepository.save(targetCounselee);
        return targetCounselee.getId();
    }

    public SelectCounseleeRes selectCounselee(String counseleeId) {
        Counselee counselee = counseleeRepository.findById(counseleeId)
                .orElseThrow(IllegalArgumentException::new);
        return SelectCounseleeRes.builder()
                .id(counselee.getId())
                .name(counselee.getName())
                .age(dateTimeUtil.calculateKoreanAge(counselee.getDateOfBirth(), LocalDate.now()))
                .dateOfBirth(counselee.getDateOfBirth())
                .phoneNumber(counselee.getPhoneNumber())
                .gender(counselee.getGenderType())
                .address(counselee.getAddress())
                .affiliatedWelfareInstitution(counselee.getAffiliatedWelfareInstitution())
                .healthInsuranceType(counselee.getHealthInsuranceType())
                .counselCount(counselee.getCounselCount())
                .lastCounselDate(counselee.getLastCounselDate())
                .registrationDate(counselee.getRegistrationDate())
                .careManagerName(counselee.getCareManagerName())
                .note(counselee.getNote())
                .isDisability(counselee.isDisability())
                .build();
    }

    public SelectCounseleePageRes selectCounselees(int page, int size, String name,
            List<LocalDate> birthDates, List<String> affiliatedWelfareInstitutions) {

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Counselee> counseleePage;
        if (name == null && (birthDates == null || birthDates.isEmpty())
                && (affiliatedWelfareInstitutions == null || affiliatedWelfareInstitutions.isEmpty())) {
            counseleePage = counseleeRepository.findAll(pageRequest);
        } else {
            counseleePage = counseleeRepository.findWithFilters(name, birthDates,
                    affiliatedWelfareInstitutions, pageRequest);
        }

        List<SelectCounseleeRes> content = counseleePage.getContent().stream()
                .sorted(Comparator.comparing(Counselee::getRegistrationDate).reversed())
                .map(counselee -> SelectCounseleeRes.builder()
                        .id(counselee.getId())
                        .name(counselee.getName())
                        .age(dateTimeUtil.calculateKoreanAge(counselee.getDateOfBirth(), LocalDate.now()))
                        .dateOfBirth(counselee.getDateOfBirth())
                        .phoneNumber(counselee.getPhoneNumber())
                        .gender(counselee.getGenderType())
                        .address(counselee.getAddress())
                        .affiliatedWelfareInstitution(counselee.getAffiliatedWelfareInstitution())
                        .healthInsuranceType(counselee.getHealthInsuranceType())
                        .counselCount(counselee.getCounselCount())
                        .lastCounselDate(counselee.getLastCounselDate())
                        .registrationDate(counselee.getRegistrationDate())
                        .careManagerName(counselee.getCareManagerName())
                        .note(counselee.getNote())
                        .isDisability(counselee.isDisability())
                        .build())
                .collect(Collectors.toList());

        return new SelectCounseleePageRes(
                content,
                counseleePage.getTotalPages(),
                counseleePage.getTotalElements(),
                counseleePage.getNumber(),
                counseleePage.hasNext(),
                counseleePage.hasPrevious());
    }

    @CacheEvict(value = { "birthDates", "welfareInstitutions" }, allEntries = true)
    public void deleteCounselee(String counseleeId) {
        counseleeRepository.deleteById(counseleeId);
    }

    @CacheEvict(value = { "birthDates", "welfareInstitutions" }, allEntries = true)
    @Transactional
    public List<DeleteCounseleeBatchRes> deleteCounseleeBatch(
            List<DeleteCounseleeBatchReq> deleteCounseleeBatchReqList) {

        List<DeleteCounseleeBatchRes> deleteCounseleeBatchResList = new ArrayList<>();

        deleteCounseleeBatchReqList.forEach(deleteCounseleeBatchReq -> {
            deleteCounselee(deleteCounseleeBatchReq.getCounseleeId());
            deleteCounseleeBatchResList.add(DeleteCounseleeBatchRes
                    .builder()
                    .deletedCounseleeId(deleteCounseleeBatchReq.getCounseleeId())
                    .build());
        });

        return deleteCounseleeBatchResList;

    }

    private CounselCard getPreviousCounselCard(String counseleeId, LocalDateTime scheduledStartDateTime) {

        // 이전 상담 세션을 가져오는 메서드
        List<CounselSession> previousCounselSessions = counselSessionRepository
                .findByCounseleeIdAndScheduledStartDateTimeLessThan(counseleeId, scheduledStartDateTime);

        for (CounselSession previousSession : previousCounselSessions) {
            CounselCard previousCounselCard = previousSession.getCounselCard();
            if (previousCounselCard != null
                    && !previousCounselCard.getCardRecordStatus().equals(CardRecordStatus.RECORDED)) {
                return previousCounselCard;
            }
        }

        return null;
    }

    @Cacheable(value = "birthDates")
    public List<LocalDate> getDistinctBirthDates() {
        return counseleeRepository.findDistinctBirthDates();
    }

    @Cacheable(value = "welfareInstitutions")
    public List<String> getDistinctAffiliatedWelfareInstitutions() {
        return counseleeRepository.findDistinctAffiliatedWelfareInstitutions();
    }
}
