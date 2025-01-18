package com.springboot.api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.common.util.DateTimeUtil;
import com.springboot.api.domain.CounselCard;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.Counselee;
import com.springboot.api.dto.counselee.AddAndUpdateCounseleeReq;
import com.springboot.api.dto.counselee.SelectCounseleeBaseInformationByCounseleeIdRes;
import com.springboot.api.repository.CounselSessionRepository;
import com.springboot.api.repository.CounseleeRepository;
import com.springboot.enums.CardRecordStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CounseleeService {

    public final CounseleeRepository counseleeRepository;
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

        if (targetCounselCard != null) {
            JsonNode diseasesInfoJson = targetCounselCard.getHealthInformation().get("diseaseInfo");
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
                diseases // diseases 값 반환
                , Optional.ofNullable(currentCounselCard).map(CounselCard::getCardRecordStatus)
                        .orElse(CardRecordStatus.UNRECORDED),
                counselee.isDisability());
    }

    public String AddandUpdateCounselee(AddAndUpdateCounseleeReq addAndUpdateCounseleeReq) {
        Counselee targetCounselee;
        if (addAndUpdateCounseleeReq.getCounseleeId() == null) {
            targetCounselee = Counselee.builder().name(addAndUpdateCounseleeReq.getName())
                    .phoneNumber(addAndUpdateCounseleeReq.getPhoneNumber())
                    .dateOfBirth(addAndUpdateCounseleeReq.getDateOfBirth())
                    .genderType(addAndUpdateCounseleeReq.getGenderType()).address(addAndUpdateCounseleeReq.getAddress())
                    .healthInsuranceType(addAndUpdateCounseleeReq.getHealthInsuranceType())
                    .isDisability(addAndUpdateCounseleeReq.isDisability()).note(addAndUpdateCounseleeReq.getNotes())
                    .careManagerName(addAndUpdateCounseleeReq.getCareManagerName()).build();
            targetCounselee = counseleeRepository.save(targetCounselee);
        } else {
            targetCounselee = counseleeRepository.findById(addAndUpdateCounseleeReq.getCounseleeId())
                    .orElseThrow(IllegalArgumentException::new);
            targetCounselee.setName(addAndUpdateCounseleeReq.getName());
            targetCounselee.setPhoneNumber(addAndUpdateCounseleeReq.getPhoneNumber());
            targetCounselee.setDateOfBirth(addAndUpdateCounseleeReq.getDateOfBirth());
            targetCounselee.setGenderType(addAndUpdateCounseleeReq.getGenderType());
            targetCounselee.setAddress(addAndUpdateCounseleeReq.getAddress());
            targetCounselee.setHealthInsuranceType(addAndUpdateCounseleeReq.getHealthInsuranceType());
            targetCounselee.setDisability(addAndUpdateCounseleeReq.isDisability());
            targetCounselee.setNote(addAndUpdateCounseleeReq.getNotes());
            targetCounselee.setCareManagerName(addAndUpdateCounseleeReq.getCareManagerName());
            targetCounselee = counseleeRepository.save(targetCounselee);
        }
        return targetCounselee.getId();
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
}
