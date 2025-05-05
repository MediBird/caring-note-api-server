package com.springboot.api.tus.repository;

import com.springboot.api.tus.entity.TusFileInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TusFileInfoRepository extends JpaRepository<TusFileInfo, String> {

    List<TusFileInfo> findAllByCounselSessionIdOrderByUpdatedDatetimeAsc(String counselSessionId);
}
