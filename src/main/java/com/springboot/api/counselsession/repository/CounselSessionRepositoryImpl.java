package com.springboot.api.counselsession.repository;

import com.querydsl.core.Tuple;
import com.springboot.api.counselcard.entity.QCounselCard;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.api.counselsession.entity.QCounselSession;
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
    public List<Tuple> findSessionByCursorAndDate(LocalDate date, String cursorId, String counselorId,
            Pageable pageable) {

        QCounselCard counselCard = QCounselCard.counselCard;

        BooleanBuilder builder = new BooleanBuilder();

        if (date != null) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            builder.and(counselSession.scheduledStartDateTime.goe(startOfDay));
            builder.and(counselSession.scheduledStartDateTime.lt(endOfDay));
        }

        if (cursorId != null) {
            builder.and(counselSession.id.gt(cursorId));
        }

        if (counselorId != null) {
            builder.and(counselSession.counselor.id.eq(counselorId));
        }

        return queryFactory
                .select(counselSession, counselCard.cardRecordStatus)
                .from(counselSession)
                .leftJoin(counselSession.counselee).fetchJoin()
                .leftJoin(counselCard).on(counselSession.eq(counselCard.counselSession))
                .where(builder)
                .orderBy(counselSession.scheduledStartDateTime.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
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

    @Override
    public long cancelOverDueSessions() {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);

        return queryFactory
                .update(counselSession)
                .set(counselSession.status, ScheduleStatus.CANCELED)
                .where(
                        counselSession.status.eq(ScheduleStatus.SCHEDULED),
                        counselSession.scheduledStartDateTime.before(twentyFourHoursAgo))
                .execute();
    }

    @Override
    public List<CounselSession> findPreviousCompletedSessionsOrderByEndDateTimeDesc(String counseleeId,
            LocalDateTime beforeDateTime) {
        return queryFactory
                .selectFrom(counselSession)
                .where(
                        counselSession.counselee.id.eq(counseleeId),
                        counselSession.status.eq(ScheduleStatus.COMPLETED),
                        counselSession.scheduledStartDateTime.lt(beforeDateTime))
                .orderBy(counselSession.endDateTime.desc())
                .fetch();
    }

    @Override
    @SuppressWarnings("Convert2Diamond")
    public Page<CounselSession> findByCounseleeNameAndCounselorNameAndScheduledDateTime(
            String counseleeNameKeyword,
            List<String> counselorNames,
            List<LocalDate> scheduledDates,
            Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        if (counseleeNameKeyword != null && !counseleeNameKeyword.isEmpty()) {
            builder.and(counselSession.counselee.name.containsIgnoreCase(counseleeNameKeyword));
        }

        if (counselorNames != null && !counselorNames.isEmpty()) {
            builder.and(counselSession.counselor.name.in(counselorNames));
        }

        if (scheduledDates != null && !scheduledDates.isEmpty()) {
            BooleanBuilder dateBuilder = new BooleanBuilder();
            for (LocalDate date : scheduledDates) {
                LocalDateTime startOfDay = date.atStartOfDay();
                LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
                dateBuilder.or(
                        counselSession.scheduledStartDateTime.goe(startOfDay)
                                .and(counselSession.scheduledStartDateTime.lt(endOfDay)));
            }
            builder.and(dateBuilder);
        }

        List<CounselSession> content = queryFactory
                .selectFrom(counselSession)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(counselSession.scheduledStartDateTime.desc())
                .fetch();

        Long total = queryFactory
                .select(counselSession.count())
                .from(counselSession)
                .where(builder)
                .fetchOne();

        return new PageImpl<CounselSession>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public List<String> findAllPharmacistNames() {
        return queryFactory
                .select(counselSession.counselor.name)
                .from(counselSession)
                .join(counselSession.counselor)
                .where(counselSession.counselor.roleType.eq(com.springboot.enums.RoleType.ROLE_USER))
                .distinct()
                .orderBy(counselSession.counselor.name.asc())
                .fetch();
    }

    @Override
    public Optional<CounselSession> findByIdWithCounseleeAndCounselor(String counselSessionId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(counselSession)
                        .leftJoin(counselSession.counselee).fetchJoin()
                        .leftJoin(counselSession.counselor).fetchJoin()
                        .where(counselSession.id.eq(counselSessionId))
                        .fetchOne());
    }

    @Override
    public Optional<CounselSession> findByIdWithCounselee(String counselSessionId) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(counselSession)
                        .leftJoin(counselSession.counselee).fetchJoin()
                        .where(counselSession.id.eq(counselSessionId))
                        .fetchOne());
    }

    @Override
    public int countSessionNumberByCounseleeId(String counseleeId, LocalDateTime beforeDateTime) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(counselSession.counselee.id.eq(counseleeId));

        if (beforeDateTime != null) {
            builder.and(counselSession.scheduledStartDateTime.lt(beforeDateTime));
        }

        // 취소가 아닌 상담 세션들로 회차 계산
        builder.and(counselSession.status.ne(ScheduleStatus.CANCELED));

        Long count = queryFactory
                .select(counselSession.count())
                .from(counselSession)
                .where(builder)
                .fetchOne();

        return count != null ? count.intValue() : 0;
    }
}