package com.springboot.api.tus.repository;

import com.springboot.api.tus.entity.TusFileInfo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TusFileInfoRepository extends JpaRepository<TusFileInfo, String> {

    @EntityGraph(attributePaths = "sessionRecord")
    Optional<TusFileInfo> findById(String id);

    List<TusFileInfo> findAllBySessionRecordCounselSessionId(String counselSessionId);
}
