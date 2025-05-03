package com.springboot.api.tus.repository;

import com.springboot.api.tus.entity.TusFileInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TusFileInfoRepository extends JpaRepository<TusFileInfo, String> {

    Optional<TusFileInfo> findByCounselSessionId(String counselSessionId);
}
