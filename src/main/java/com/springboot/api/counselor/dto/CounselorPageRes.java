package com.springboot.api.counselor.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.springboot.api.common.dto.PageRes;
import com.springboot.api.counselor.entity.Counselor;
import com.springboot.enums.RoleType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CounselorPageRes {
    private final List<CounselorListItem> counselors;
    private final PageRes pageInfo;

    public CounselorPageRes(List<CounselorListItem> counselors, PageRes pageInfo) {
        this.counselors = counselors;
        this.pageInfo = pageInfo;
    }

    public static CounselorPageRes fromPage(Page<Counselor> counselorPage) {
        List<CounselorListItem> counselorList = counselorPage.getContent().stream()
                .map(CounselorListItem::fromCounselor)
                .collect(Collectors.toList());

        PageRes pageInfo = PageRes.from(counselorPage);

        return new CounselorPageRes(counselorList, pageInfo);
    }

    @Builder
    @Getter
    public static class CounselorListItem {
        private final String id;
        private final String name;
        private final RoleType roleType;
        private final String username; // 계정
        private final String phoneNumber;
        private final LocalDate registrationDate; // 가입일
        private final String description; // 비고

        // Counselor 엔티티로부터 CounselorListItem 객체 생성
        public static CounselorListItem fromCounselor(Counselor counselor) {
            return CounselorListItem.builder()
                    .id(counselor.getId())
                    .name(counselor.getName())
                    .roleType(counselor.getRoleType())
                    .username(counselor.getUsername())
                    .phoneNumber(counselor.getPhoneNumber())
                    .registrationDate(counselor.getRegistrationDate())
                    .description(counselor.getDescription() != null ? counselor.getDescription() : "")
                    .build();
        }
    }
}