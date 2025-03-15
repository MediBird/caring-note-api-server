package com.springboot.api.counselcard.dto;

import com.springboot.api.counselcard.dto.information.base.BaseInformationDTO;
import com.springboot.api.counselcard.dto.information.health.HealthInformationDTO;
import com.springboot.api.counselcard.dto.information.independentlife.IndependentLifeInformationDTO;
import com.springboot.api.counselcard.dto.information.living.LivingInformationDTO;

import lombok.Builder;

@Builder
public record SelectPreviousCounselCardRes(
        BaseInformationDTO baseInformation, HealthInformationDTO healthInformation,
        LivingInformationDTO livingInformation, IndependentLifeInformationDTO independentLifeInformation) {
}
