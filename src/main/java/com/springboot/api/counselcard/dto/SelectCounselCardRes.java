package com.springboot.api.counselcard.dto;

import com.springboot.api.counselcard.dto.information.base.BaseInformationDTO;
import com.springboot.api.counselcard.dto.information.health.HealthInformationDTO;
import com.springboot.api.counselcard.dto.information.independentlife.IndependentLifeInformationDTO;
import com.springboot.api.counselcard.dto.information.living.LivingInformationDTO;
import com.springboot.enums.CardRecordStatus;

import lombok.Builder;

@Builder
public record SelectCounselCardRes(
        String counselCardId, BaseInformationDTO baseInformation, HealthInformationDTO healthInformation,
        LivingInformationDTO livingInformation, IndependentLifeInformationDTO independentLifeInformation,
        CardRecordStatus cardRecordStatus) {
}
