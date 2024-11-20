package com.springboot.api.domain;

import com.springboot.enums.CounselorStatus;
import com.springboot.enums.RoleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "counselors", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"email"}),
    @UniqueConstraint(columnNames = {"phone_number"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"counselSessions"})
@ToString(callSuper = true, exclude = {"counselSessions"})
public class Counselor extends BaseEntity implements UserDetails {

    // 이름
    @Column(nullable = false)
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;

    // 이메일
    @Column(nullable = false, unique = true)
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String email;

    private String password;

    // 전화번호
    @Column(unique = true)
    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10~11자리의 숫자여야 합니다.")
    private String phoneNumber;

    // 프로필 사진 URL
    private String profileImageUrl;

    // 구글 SSO 식별자
    @Column(unique = true)
    private String googleSsoId;

    // 애플 SSO 식별자
    @Column(unique = true)
    private String appleSsoId;

    @Column(updatable = false)
    private LocalDate registrationDate;

    // 상태 (활성, 비활성)
    @Enumerated(EnumType.STRING)
    private CounselorStatus status;

    // Admin, User
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    // 복약 상담 횟수
    @Min(value = 0, message = "복약 상담 횟수는 0 이상이어야 합니다.")
    private int medicationCounselingCount;

    // 상담한 내담자 수
    @Min(value = 0, message = "상담한 내담자 수는 0 이상이어야 합니다.")
    private int counseledCounseleeCount;

    // 참여 일수
    @Min(value = 0, message = "참여 일수는 0 이상이어야 합니다.")
    private int participationDays;


    // 상담자가 참여한 상담 세션들
    @OneToMany(mappedBy = "counselor", cascade = CascadeType.ALL)
    private List<CounselSession> counselSessions;


    // 엔티티가 저장되기 전에 호출되어 ID와 등록 날짜 설정
    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
        if (status == null) {
            status = CounselorStatus.ACTIVE;
        }
        registrationDate = LocalDate.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(new SimpleGrantedAuthority(getRoleType().name()));
    }

    @Override
    public String getUsername() {
        return getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return getStatus().equals(CounselorStatus.ACTIVE);  // 계정이 만료되지 않았음
    }

    @Override
    public boolean isAccountNonLocked() {
        return getStatus().equals(CounselorStatus.ACTIVE);  // 계정이 잠기지 않음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // 비밀번호가 만료되지 않음
    }

    @Override
    public boolean isEnabled() {
        return getStatus().equals(CounselorStatus.ACTIVE);  // 계정이 활성화됨
    }

}
