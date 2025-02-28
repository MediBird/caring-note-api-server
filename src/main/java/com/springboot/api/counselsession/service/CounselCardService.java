package com.springboot.api.counselsession.service;

import java.util.AbstractMap;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.api.common.exception.NoContentException;
import com.springboot.api.counselcard.dto.AddCounselCardReq;
import com.springboot.api.counselcard.dto.AddCounselCardRes;
import com.springboot.api.counselcard.dto.DeleteCounselCardReq;
import com.springboot.api.counselcard.dto.DeleteCounselCardRes;
import com.springboot.api.counselcard.dto.SelectCounselCardRes;
import com.springboot.api.counselcard.dto.SelectPreviousCounselCardRes;
import com.springboot.api.counselcard.dto.SelectPreviousItemListByInformationNameAndItemNameRes;
import com.springboot.api.counselcard.dto.UpdateCounselCardReq;
import com.springboot.api.counselcard.dto.UpdateCounselCardRes;
import com.springboot.api.counselcard.dto.information.base.BaseInformationDTO;
import com.springboot.api.counselcard.dto.information.health.HealthInformationDTO;
import com.springboot.api.counselcard.dto.information.independentlife.IndependentLifeInformationDTO;
import com.springboot.api.counselcard.dto.information.living.LivingInformationDTO;
import com.springboot.api.counselcard.entity.CounselCard;
import com.springboot.api.counselcard.repository.CounselCardRepository;
import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.api.counselsession.repository.CounselSessionRepository;
import com.springboot.enums.ScheduleStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CounselCardService {

        private final CounselCardRepository counselCardRepository;
        private final CounselSessionRepository counselSessionRepository;
        private final ObjectMapper objectMapper;

        public SelectCounselCardRes selectCounselCard(String counselSessionId) throws JsonProcessingException {

                CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
                                .orElseThrow(IllegalArgumentException::new);

                CounselCard counselCard = Optional.ofNullable(counselSession.getCounselCard())
                                .orElseThrow(NoContentException::new);

                return new SelectCounselCardRes(counselCard.getId(),
                                objectMapper.treeToValue(counselCard.getBaseInformation(), BaseInformationDTO.class),
                                objectMapper.treeToValue(counselCard.getHealthInformation(),
                                                HealthInformationDTO.class),
                                objectMapper.treeToValue(counselCard.getLivingInformation(),
                                                LivingInformationDTO.class),
                                objectMapper.treeToValue(counselCard.getIndependentLifeInformation(),
                                                IndependentLifeInformationDTO.class),
                                counselCard.getCardRecordStatus());
        }

        public SelectPreviousCounselCardRes selectPreviousCounselCard(String counselSessionId)
                        throws JsonProcessingException {

                CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
                                .orElseThrow(IllegalArgumentException::new);

                Counselee counselee = Optional.ofNullable(counselSession.getCounselee())
                                .orElseThrow(NoContentException::new);

                Pageable pageable = PageRequest.of(0, 1);

                List<CounselSession> previousCounselSessions = counselSessionRepository
                                .findByCounseleeIdPrevious(counselSessionId, counselee.getId(), pageable);

                Optional.of(previousCounselSessions)
                                .filter(sessions -> !sessions.isEmpty())
                                .orElseThrow(NoContentException::new);

                CounselCard previousCounselCard = Optional
                                .ofNullable(previousCounselSessions.getFirst().getCounselCard())
                                .orElseThrow(NoContentException::new);

                return new SelectPreviousCounselCardRes(
                                objectMapper.treeToValue(previousCounselCard.getBaseInformation(),
                                                BaseInformationDTO.class),
                                objectMapper.treeToValue(previousCounselCard.getHealthInformation(),
                                                HealthInformationDTO.class),
                                objectMapper.treeToValue(previousCounselCard.getLivingInformation(),
                                                LivingInformationDTO.class),
                                objectMapper.treeToValue(previousCounselCard.getIndependentLifeInformation(),
                                                IndependentLifeInformationDTO.class));
        }

        @Transactional
        public AddCounselCardRes addCounselCard(AddCounselCardReq addCounselCardReq) {
                CounselSession counselSession = counselSessionRepository
                                .findById(addCounselCardReq.getCounselSessionId())
                                .orElseThrow(IllegalArgumentException::new);

                CounselCard counselCard = CounselCard.builder()
                                .counselSession(counselSession)
                                .baseInformation(objectMapper.valueToTree(addCounselCardReq.getBaseInformation()))
                                .healthInformation(objectMapper.valueToTree(addCounselCardReq.getHealthInformation()))
                                .livingInformation(objectMapper.valueToTree(addCounselCardReq.getLivingInformation()))
                                .independentLifeInformation(objectMapper
                                                .valueToTree(addCounselCardReq.getIndependentLifeInformation()))
                                .cardRecordStatus(addCounselCardReq.getCardRecordStatus())
                                .build();

                CounselCard savedCounselCard = counselCardRepository.save(counselCard);

                return new AddCounselCardRes(savedCounselCard.getId());

        }

        @Transactional
        public UpdateCounselCardRes updateCounselCard(UpdateCounselCardReq updateCounselCardReq) {
                CounselCard counselCard = counselCardRepository.findById(updateCounselCardReq.getCounselCardId())
                                .orElseThrow(IllegalArgumentException::new);

                counselCard.setBaseInformation(objectMapper.valueToTree(updateCounselCardReq.getBaseInformation()));
                counselCard.setHealthInformation(objectMapper.valueToTree(updateCounselCardReq.getHealthInformation()));
                counselCard.setLivingInformation(objectMapper.valueToTree(updateCounselCardReq.getLivingInformation()));
                counselCard.setIndependentLifeInformation(
                                objectMapper.valueToTree(updateCounselCardReq.getIndependentLifeInformation()));
                counselCard.setCardRecordStatus(updateCounselCardReq.getCardRecordStatus());

                return new UpdateCounselCardRes(counselCard.getId());
        }

        @Transactional
        public DeleteCounselCardRes deleteCounselCard(DeleteCounselCardReq deleteCounselCardReq) {
                if (!counselCardRepository.existsById(deleteCounselCardReq.getCounselCardId())) {
                        throw new NoContentException();
                }
                counselCardRepository.deleteById(deleteCounselCardReq.getCounselCardId());
                return new DeleteCounselCardRes(deleteCounselCardReq.getCounselCardId());
        }

        public List<SelectPreviousItemListByInformationNameAndItemNameRes> selectPreviousItemListByInformationNameAndItemName(
                        String counselSessionId,
                        String informationName,
                        String itemName) {

                CounselSession counselSession = counselSessionRepository.findById(counselSessionId)
                                .orElseThrow(IllegalArgumentException::new);

                Counselee counselee = Optional.ofNullable(counselSession.getCounselee())
                                .orElseThrow(NoContentException::new);

                List<CounselSession> previousCounselSessions = counselSessionRepository
                                .findByCounseleeIdAndScheduledStartDateTimeLessThan(
                                                counselee.getId(),
                                                counselSession.getScheduledStartDateTime());

                List<SelectPreviousItemListByInformationNameAndItemNameRes> selectPreviousItemListByInformationNameAndItemNameResList = previousCounselSessions
                                .stream()
                                .filter(cs -> ScheduleStatus.COMPLETED.equals(cs.getStatus()))
                                .map(cs -> {
                                        JsonNode informationJsonNode;

                                        switch (informationName) {
                                                case "baseInformation" ->
                                                        informationJsonNode = cs.getCounselCard().getBaseInformation();
                                                case "healthInformation" ->
                                                        informationJsonNode = cs.getCounselCard()
                                                                        .getHealthInformation();
                                                case "livingInformation" ->
                                                        informationJsonNode = cs.getCounselCard()
                                                                        .getLivingInformation();
                                                default ->
                                                        throw new NoContentException();
                                        }
                                        return new AbstractMap.SimpleEntry<>(cs, informationJsonNode);
                                })
                                .map(entry -> {
                                        JsonNode informationJsonNode = entry.getValue();
                                        JsonNode itemNode = Optional.ofNullable(informationJsonNode.get(itemName))
                                                        .orElseThrow(NoContentException::new);

                                        return new SelectPreviousItemListByInformationNameAndItemNameRes(
                                                        entry.getKey().getScheduledStartDateTime().toLocalDate(),
                                                        itemNode);
                                })
                                .toList();

                if (selectPreviousItemListByInformationNameAndItemNameResList.isEmpty()) {
                        throw new NoContentException();
                }

                return selectPreviousItemListByInformationNameAndItemNameResList;
        }

}
