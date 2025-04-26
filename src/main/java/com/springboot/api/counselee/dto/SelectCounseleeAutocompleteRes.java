package com.springboot.api.counselee.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.springboot.api.counselee.entity.Counselee;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "내담자 자동완성 응답")
public class SelectCounseleeAutocompleteRes {

    @Schema(description = "내담자 ID", example = "1234abcd-5678-efgh-9012-ijkl34567890")
    private String counseleeId;

    @Schema(description = "내담자 이름", example = "홍길동")
    private String name;

    @Schema(description = "생년월일", example = "1990-01-01")
    private LocalDate birthDate;

    public static SelectCounseleeAutocompleteRes from(Counselee counselee) {
        return SelectCounseleeAutocompleteRes.builder()
            .counseleeId(counselee.getId())
            .name(counselee.getName())
            .birthDate(counselee.getDateOfBirth())
            .build();
    }

    public static List<SelectCounseleeAutocompleteRes> fromList(List<Counselee> counselees) {
        return counselees.stream()
            .map(SelectCounseleeAutocompleteRes::from)
            .collect(Collectors.toList());
    }
}