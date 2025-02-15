package com.springboot.api.repository;

import com.springboot.api.domain.CounselSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

public interface CounselSessionRepository extends JpaRepository<CounselSession, String> {

  @Override
  void deleteById(@NonNull String id);

  @Query("""
      SELECT cs FROM CounselSession cs
      WHERE (cs.scheduledStartDateTime >= :startOfDay)
        AND (cs.scheduledStartDateTime < :endOfDay)
        AND (:cursorId IS NULL OR cs.id > :cursorId)
        AND (:counselorId IS NULL OR cs.counselor.id = :counselorId)
      ORDER BY cs.id ASC
      """)
  List<CounselSession> findByDateAndCursor(
      @Param("startOfDay") LocalDateTime startOfDay,
      @Param("endOfDay") LocalDateTime endOfDay,
      @Param("cursorId") String cursorId,
      @Param("counselorId") String counselorId,
      Pageable pageable);

  @Query("""
      SELECT cs FROM CounselSession cs
      WHERE (:cursorId IS NULL OR cs.id > :cursorId)
        AND (:counselorId IS NULL OR cs.counselor.id = :counselorId)
      ORDER BY cs.id ASC
      """)
  List<CounselSession> findByCursor(
      @Param("cursorId") String cursorId,
      @Param("counselorId") String counselorId,
      Pageable pageable);

  @Query("""
      SELECT cs FROM CounselSession cs
      WHERE cs.counselee.id = :counseleeId
       AND cs.id < :id
      ORDER BY cs.id DESC
      """)
  List<CounselSession> findByCounseleeIdPrevious(
      @Param("counseleeId") String counseleeId, @Param("id") String id, Pageable pageable);

  List<CounselSession> findByCounseleeIdAndScheduledStartDateTimeLessThan(String counseleeId,
      LocalDateTime scheduledStartDateTime);

  @Query("""
      SELECT DISTINCT cs.scheduledStartDateTime.toLocalDate()
      FROM CounselSession cs
      WHERE YEAR(cs.scheduledStartDateTime) = :year
      AND MONTH(cs.scheduledStartDateTime) = :month
      ORDER BY cs.scheduledStartDateTime.toLocalDate() ASC
      """)
  List<LocalDate> findDistinctDatesByYearAndMonth(
      @Param("year") int year,
      @Param("month") int month);

}
