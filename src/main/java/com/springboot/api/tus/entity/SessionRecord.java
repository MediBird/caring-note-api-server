package com.springboot.api.tus.entity;

import com.springboot.api.common.entity.BaseEntity;
import com.springboot.api.counselsession.entity.CounselSession;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Table(name = "session_record")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionRecord extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "counsel_session_id", nullable = false)
    private CounselSession counselSession;

    private Long duration;

    private Boolean isMerged;

    private SessionRecord(CounselSession counselSession, Long duration, Boolean isMerged) {
        this.counselSession = counselSession;
        this.duration = duration;
        this.isMerged = isMerged;
    }

    public static SessionRecord of(CounselSession counselSession) {
        return new SessionRecord(counselSession, 0L, false);
    }

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }

    public void updateDuration(Long updatedDuration) {
        this.duration = updatedDuration;
    }

    public String getFolderPath(String uploadPath) {
        return Path.of(uploadPath, this.getId()).toAbsolutePath().toString();
    }

    public void fileMerged() {
        this.isMerged = true;
    }
}
