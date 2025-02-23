package com.springboot.api.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
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
                DateTemplate<LocalDate> dateOnly = Expressions.dateTemplate(
                                LocalDate.class,
                                "DATE({0})",
                                counselSession.scheduledStartDateTime);
                return queryFactory
                                .select(dateOnly)
                                .distinct()
                                .from(counselSession)
                                .where(
                                                counselSession.scheduledStartDateTime.year().eq(year),
                                                counselSession.scheduledStartDateTime.month().eq(month))
                                .orderBy(counselSession.scheduledStartDateTime.asc())
                                .fetch();
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