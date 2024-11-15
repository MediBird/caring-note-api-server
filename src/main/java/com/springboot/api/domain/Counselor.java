package com.springboot.api.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.springboot.enums.CounselorStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "counselors", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"email"}),
    @UniqueConstraint(columnNames = {"phoneNumber"})
})
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"roles", "counselingSessions", "counselSchedules"})
@ToString(callSuper = true, exclude = {"roles", "counselingSessions", "counselSchedules"})
public class Counselor extends BaseEntity {

    // 이름
    @Column(nullable = false)
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;

    // 이메일
    @Column(nullable = false, unique = true)
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String email;

    // 전화번호
    @Column(unique = true)
    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10~11자리의 숫자여야 합니다.")
    private String phoneNumber;

    // 프로필 사진 URL
    private String profileImageUrl;

    // 복약 상담 횟수
    @Min(value = 0, message = "복약 상담 횟수는 0 이상이어야 합니다.")
    private int medicationCounselingCount;

    // 상담한 내담자 수
    @Min(value = 0, message = "상담한 내담자 수는 0 이상이어야 합니다.")
    private int counseledCounseleeCount;

    // 참여 일수
    @Min(value = 0, message = "참여 일수는 0 이상이어야 합니다.")
    private int participationDays;

    // 구글 SSO 식별자
    @Column(unique = true)
    private String googleSsoId;

    // 애플 SSO 식별자
    @Column(unique = true)
    private String appleSsoId;

    // 등록 날짜
    @Column(updatable = false)
    private LocalDate registrationDate;

    // 상태 (활성, 비활성)
    @Enumerated(EnumType.STRING)
    private CounselorStatus status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "counselor_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    // 상담자가 참여한 상담 세션들
    @OneToMany(mappedBy = "counselor", cascade = CascadeType.ALL)
    private List<CounselingSession> counselingSessions;

    @OneToMany(mappedBy = "counselor", cascade = CascadeType.ALL)
    private List<CounselSchedule> counselSchedules;

    // 엔티티가 저장되기 전에 호출되어 ID와 등록 날짜 설정
    @PrePersist
    protected void onCreate() {
        super.onCreate();
        registrationDate = LocalDate.now();
        if (status == null) {
            status = CounselorStatus.ACTIVE;
        }
    }

}
