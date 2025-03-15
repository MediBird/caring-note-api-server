package com.springboot.api.counselee.service;

import java.time.LocalDate;
import java.util.ArrayList;
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
import com.springboot.api.counselcard.entity.CounselCard;
import com.springboot.api.counselcard.repository.CounselCardRepository;
import com.springboot.api.counselee.dto.AddCounseleeReq;
import com.springboot.api.counselee.dto.DeleteCounseleeBatchReq;
import com.springboot.api.counselee.dto.DeleteCounseleeBatchRes;
import com.springboot.api.counselee.dto.SelectCounseleeAutocompleteRes;
import com.springboot.api.counselee.dto.SelectCounseleeBaseInformationByCounseleeIdRes;
import com.springboot.api.counselee.dto.SelectCounseleePageRes;
import com.springboot.api.counselee.dto.SelectCounseleeRes;
import com.springboot.api.counselee.dto.UpdateCounseleeReq;
import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselee.repository.CounseleeRepository;
import com.springboot.enums.CardRecordStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CounseleeService {

    private final CounseleeRepository counseleeRepository;
    private final CounselCardRepository counselCardRepository;

    public SelectCounseleeBaseInformationByCounseleeIdRes selectCounseleeBaseInformation(String counselSessionId) {

        Counselee counselee = counseleeRepository.findByCounselSessionId(counselSessionId)
                .orElseThrow(NoContentException::new);

        CounselCard counselCard = counselCardRepository.findLastRecordedCounselCard(counselee.getId())
                .orElse(null);

        List<String> diseases = new ArrayList<>();

        if (counselCard != null && counselCard.getHealthInformation() != null) {
            JsonNode healthInfo = counselCard.getHealthInformation();
            JsonNode diseasesInfoJson = healthInfo.get("diseaseInfo");
            JsonNode diseasesJson = diseasesInfoJson != null ? diseasesInfoJson.get("diseases") : null;

            if (diseasesJson != null && diseasesJson.isArray()) {
                diseasesJson.forEach(diseaseJson -> diseases.add(diseaseJson.asText()));
            }
        }

        return SelectCounseleeBaseInformationByCounseleeIdRes.from(counselee, diseases,
                Optional.ofNullable(counselCard).map(CounselCard::getCardRecordStatus)
                        .orElse(CardRecordStatus.UNRECORDED));
    }

    @CacheEvict(value = { "birthDates", "welfareInstitutions" }, allEntries = true)
    public String addCounselee(AddCounseleeReq addCounseleeReq) {
        if (counseleeRepository.existsByPhoneNumber(addCounseleeReq.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        Counselee targetCounselee = new Counselee(addCounseleeReq);
        return counseleeRepository.save(targetCounselee).getId();
    }

    @CacheEvict(value = { "birthDates", "welfareInstitutions" }, allEntries = true)
    @Transactional
    public String updateCounselee(UpdateCounseleeReq updateCounseleeReq) {
        Counselee targetCounselee = counseleeRepository.findById(updateCounseleeReq.getCounseleeId())
                .orElseThrow(IllegalArgumentException::new);

        if (counseleeRepository.existsByPhoneNumber(updateCounseleeReq.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        targetCounselee.update(updateCounseleeReq);
        targetCounselee = counseleeRepository.save(targetCounselee);
        return targetCounselee.getId();
    }

    public SelectCounseleeRes selectCounselee(String counseleeId) {
        Counselee counselee = counseleeRepository.findById(counseleeId)
                .orElseThrow(IllegalArgumentException::new);
        return SelectCounseleeRes.from(counselee);
    }

    public SelectCounseleePageRes selectCounselees(int page, int size, String name,
            List<LocalDate> birthDates, List<String> affiliatedWelfareInstitutions) {

        Page<Counselee> counseleePage = counseleeRepository.findWithFilters(name, birthDates,
                affiliatedWelfareInstitutions, PageRequest.of(page, size));

        return SelectCounseleePageRes.of(counseleePage);
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

    @Cacheable(value = "birthDates")
    public List<LocalDate> getDistinctBirthDates() {
        return counseleeRepository.findDistinctBirthDates().stream()
                .filter(date -> date != null)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "welfareInstitutions")
    public List<String> getDistinctAffiliatedWelfareInstitutions() {
        return counseleeRepository.findDistinctAffiliatedWelfareInstitutions().stream()
                .filter(institution -> institution != null && !institution.isEmpty())
                .collect(Collectors.toList());
    }

    public List<SelectCounseleeAutocompleteRes> searchCounseleesByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }

        List<Counselee> counselees = counseleeRepository.findByNameContaining(keyword.trim());
        return SelectCounseleeAutocompleteRes.fromList(counselees);
    }
}
