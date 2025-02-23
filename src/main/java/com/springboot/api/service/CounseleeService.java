package com.springboot.api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.common.util.DateTimeUtil;
import com.springboot.api.domain.CounselCard;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.Counselee;
import com.springboot.api.dto.counselee.AddCounseleeReq;
import com.springboot.api.dto.counselee.DeleteCounseleeBatchReq;
import com.springboot.api.dto.counselee.DeleteCounseleeBatchRes;
import com.springboot.api.dto.counselee.SelectCounseleeBaseInformationByCounseleeIdRes;
import com.springboot.api.dto.counselee.SelectCounseleePageRes;
import com.springboot.api.dto.counselee.SelectCounseleeRes;
import com.springboot.api.dto.counselee.UpdateCounseleeReq;
import com.springboot.api.repository.CounselSessionRepository;
import com.springboot.api.repository.CounseleeRepository;
import com.springboot.enums.CardRecordStatus;
import com.springboot.enums.HealthInsuranceType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

        return new SelectCounseleeBaseInformationByCounseleeIdRes(counselee.getId(), counselee.getName(),
                dateTimeUtil.calculateKoreanAge(counselee.getDateOfBirth(), LocalDate.now()),
                counselee.getDateOfBirth().toString(), counselee.getGenderType(), counselee.getAddress(),
                counselee.getHealthInsuranceType(), counselee.getCounselCount(), counselee.getLastCounselDate(),
                diseases, Optional.ofNullable(currentCounselCard).map(CounselCard::getCardRecordStatus)
                        .orElse(CardRecordStatus.UNRECORDED),
                counselee.isDisability());

    }

    @CacheEvict(value = { "birthDates", "welfareInstitutions" }, allEntries = true)
    public String addCounselee(AddCounseleeReq addCounseleeReq) {
        log.info("addCounseleeReq: {}", addCounseleeReq);
        
        boolean exists = counseleeRepository.existsByPhoneNumber(addCounseleeReq.getPhoneNumber());
        if (exists) {
            throw new IllegalArgumentException("이미 등록된 전화번호입니다");
        }
        
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
    @Transactional
    public String updateCounselee(UpdateCounseleeReq updateCounseleeReq) {
        Counselee existingCounselee = counseleeRepository.findById(updateCounseleeReq.getCounseleeId())
                .orElseThrow(() -> new NoContentException("상담자를 찾을 수 없습니다"));

        if (isPhoneNumberChanged(existingCounselee, updateCounseleeReq)) {
            boolean exists = counseleeRepository.existsByPhoneNumber(updateCounseleeReq.getPhoneNumber());

            if (exists) {
                throw new IllegalArgumentException("이미 등록된 전화번호입니다");
            }
        }

        // 4. 변경된 값만 업데이트
        if (updateCounseleeReq.getName() != null) {
            existingCounselee.setName(updateCounseleeReq.getName());
        }
        if (updateCounseleeReq.getPhoneNumber() != null) {
            existingCounselee.setPhoneNumber(updateCounseleeReq.getPhoneNumber());
        }
        if (updateCounseleeReq.getDateOfBirth() != null) {
            existingCounselee.setDateOfBirth(updateCounseleeReq.getDateOfBirth());
        }
        if (updateCounseleeReq.getGenderType() != null) {
            existingCounselee.setGenderType(updateCounseleeReq.getGenderType());
        }
        if (updateCounseleeReq.getAddress() != null) {
            existingCounselee.setAddress(updateCounseleeReq.getAddress());
        }

        // boolean 타입은 항상 업데이트
        existingCounselee.setDisability(updateCounseleeReq.isDisability());

        // null 허용 필드들
        existingCounselee.setNote(updateCounseleeReq.getNote());
        existingCounselee.setCareManagerName(updateCounseleeReq.getCareManagerName());
        existingCounselee.setAffiliatedWelfareInstitution(updateCounseleeReq.getAffiliatedWelfareInstitution());

        return counseleeRepository.save(existingCounselee).getId();
    }

    private boolean isPhoneNumberChanged(Counselee existingCounselee, UpdateCounseleeReq updateCounseleeReq) {
        return !existingCounselee.getPhoneNumber().equals(updateCounseleeReq.getPhoneNumber());
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
        return counseleeRepository.findDistinctBirthDates().stream()
                .filter(date -> date != null)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "welfareInstitutions")
    public List<String> getDistinctAffiliatedWelfareInstitutions() {
        return counseleeRepository.findDistinctAffiliatedWelfareInstitutions().stream()
                .filter(institution -> institution != null)
                .collect(Collectors.toList());
    }
}
