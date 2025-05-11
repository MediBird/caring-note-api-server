package com.springboot.api.tus.entity;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.springboot.api.common.entity.BaseEntity;
import com.springboot.api.counselsession.entity.CounselSession;

import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tus_file_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TusFileInfo extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "counsel_session_id", nullable = false)
    private CounselSession counselSession;

    private Long contentLength;

    private Boolean isDefer;

    private Long contentOffset;

    private String originalName;

    private String savedName;

    private Long duration;

    private TusFileInfo(CounselSession counselSession, String originalName, Long contentLength, Boolean isDefer) {
        this.counselSession = Objects.requireNonNull(counselSession);
        this.originalName = Objects.requireNonNullElse(originalName, "NONE");
        this.contentLength = contentLength;
        this.isDefer = Optional.ofNullable(isDefer).orElse(false);
        this.contentOffset = 0L;
        this.savedName = new ULID().nextULID();

        if (isDefer == null && contentLength == null) {
            throw new IllegalArgumentException("isDefer, contentLength 둘 다 null일 수 없습니다.");
        }
        if (Boolean.FALSE.equals(isDefer)) {
            throw new IllegalArgumentException("isDefer는 true 또는 null만 허용됩니다.");
        }
        if (isDefer != null && contentLength != null) {
            throw new IllegalArgumentException("isDefer와 contentLength는 동시에 설정될 수 없습니다.");
        }
    }

    public static TusFileInfo of(CounselSession counselSession, String originalName, Long contentLength, Boolean isDefer, Long duration) {
        TusFileInfo tusFileInfo = new TusFileInfo(counselSession, originalName, contentLength, isDefer);
        tusFileInfo.duration = duration;
        return tusFileInfo;
    }

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }

    public void updateOffset(Integer uploadLength) {
        contentOffset += uploadLength;
    }

    public void updateDuration(Long duration) {
        this.duration = duration;
    }

    public Path getFilePath(String uploadPath, String extension) {
        return Paths.get(uploadPath, this.counselSession.getId(), this.savedName + extension);
    }
}
