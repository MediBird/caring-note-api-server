package com.springboot.api.tus.entity;

import com.springboot.api.common.entity.BaseEntity;
import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "tus_file_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TusFileInfo extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "session_record_id", nullable = false)
    private SessionRecord sessionRecord;

    private Boolean isDefer;

    private Long contentOffset;

    private String savedName;

    private TusFileInfo(SessionRecord sessionRecord) {
        this.sessionRecord = sessionRecord;
        this.isDefer = true;
        this.contentOffset = 0L;
        this.savedName = new ULID().nextULID();
    }

    public static TusFileInfo of(SessionRecord sessionRecord) {
        return new TusFileInfo(sessionRecord);
    }

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
    }

    public void updateOffset(Integer uploadLength) {
        contentOffset += uploadLength;
    }

    public Path getFilePath(String uploadPath, String extension) {
        return Paths.get(uploadPath, this.sessionRecord.getId(), this.savedName + extension);
    }

    public String getLocation(String pathPrefix) {
        return pathPrefix + "/" + sessionRecord.getId() + "/" + this.getId();
    }

    public boolean isNotOffsetEqual(Long contentOffset) {
        return !this.contentOffset.equals(contentOffset);
    }
}
