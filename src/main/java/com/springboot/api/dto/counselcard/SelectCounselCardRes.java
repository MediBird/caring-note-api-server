package com.springboot.api.dto.counselcard;

import com.springboot.api.dto.counselcard.information.base.BaseInformationDTO;
import com.springboot.api.dto.counselcard.information.health.HealthInformationDTO;
import com.springboot.api.dto.counselcard.information.independentlife.IndependentLifeInformationDTO;
import com.springboot.api.dto.counselcard.information.living.LivingInformationDTO;
import com.springboot.enums.CardRecordStatus;

import lombok.Builder;

@Builder
public record SelectCounselCardRes(
        String counselCardId, BaseInformationDTO baseInformation, HealthInformationDTO healthInformation,
        LivingInformationDTO livingInformation, IndependentLifeInformationDTO independentLifeInformation,
        CardRecordStatus cardRecordStatus) {
}
