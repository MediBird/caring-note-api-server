package com.springboot.api.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.api.domain.CounselSession;
import com.springboot.api.domain.QCounselSession;
import com.springboot.enums.ScheduleStatus;

@Repository
public class CounselSessionRepositoryImpl implements CounselSessionRepositoryCustom {

        private final JPAQueryFactory queryFactory;
        private final QCounselSession counselSession = QCounselSession.counselSession;

        public CounselSessionRepositoryImpl(JPAQueryFactory queryFactory) {
                this.queryFactory = queryFactory;
        }

        @Override
        public List<LocalDate> findDistinctDatesByYearAndMonth(int year, int month) {
                return queryFactory
                                .select(counselSession.scheduledStartDateTime)
                                .from(counselSession)
                                .where(
                                                counselSession.scheduledStartDateTime.year().eq(year),
                                                counselSession.scheduledStartDateTime.month().eq(month))
                                .orderBy(counselSession.scheduledStartDateTime.asc())
                                .fetch()
                                .stream()
                                .map(LocalDateTime::toLocalDate)
                                .distinct()
                                .collect(Collectors.toList());
        }

        @Override
        public List<CounselSession> findCompletedSessionsByYearAndMonth(int year, int month) {
                return queryFactory
                                .selectFrom(counselSession)
                                .where(
                                                counselSession.startDateTime.year().eq(year),
                                                counselSession.startDateTime.month().eq(month),
                                                counselSession.status.eq(ScheduleStatus.COMPLETED),
                                                counselSession.startDateTime.isNotNull(),
                                                counselSession.endDateTime.isNotNull())
                                .fetch();
        }

        @Override
        public long countByStatus(ScheduleStatus status) {
                return queryFactory
                                .select(counselSession.count())
                                .from(counselSession)
                                .where(counselSession.status.eq(status))
                                .fetchOne();
        }

        @Override
        public long countDistinctCounseleeForCurrentMonth() {
                LocalDate now = LocalDate.now();
                return queryFactory
                                .select(counselSession.counselee.countDistinct())
                                .from(counselSession)
                                .where(
                                                counselSession.scheduledStartDateTime.year().eq(now.getYear()),
                                                counselSession.scheduledStartDateTime.month().eq(now.getMonthValue()))
                                .fetchOne();
        }
}