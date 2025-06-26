package com.springboot.api.counselsession.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.api.common.dto.PageReq;
import com.springboot.api.common.dto.PageRes;
import com.springboot.api.common.util.QuerydslPagingUtil;
import com.springboot.api.counselcard.entity.QCounselCard;
import com.springboot.api.counselsession.dto.counselsession.QSelectCounselSessionListItem;
import com.springboot.api.counselsession.dto.counselsession.SelectCounselSessionListItem;
import com.springboot.api.counselsession.entity.CounselSession;
import com.springboot.api.counselsession.entity.QCounselSession;
import com.springboot.api.counselsession.entity.QCounseleeConsent;
import com.springboot.enums.CounselorStatus;
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
            .toList();
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
    public PageRes<SelectCounselSessionListItem> findSessionByCursorAndDate(LocalDate date, PageReq pageReq) {

        QCounselCard counselCard = QCounselCard.counselCard;
        QCounseleeConsent counseleeConsent = QCounseleeConsent.counseleeConsent;

        BooleanBuilder builder = new BooleanBuilder();

        if (date != null) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
            builder.and(counselSession.scheduledStartDateTime.goe(startOfDay));
            builder.and(counselSession.scheduledStartDateTime.lt(endOfDay));
        }

        JPAQuery<SelectCounselSessionListItem> contentQuery = queryFactory
            .select(new QSelectCounselSessionListItem(
                counselSession.id,
                counselSession.scheduledStartDateTime,
                counselSession.counselee.id,
                counselSession.counselee.name,
                new CaseBuilder()
                    .when(counselSession.counselor.status.eq(CounselorStatus.INACTIVE))
                    .then("")
                    .otherwise(counselSession.counselor.id),
                new CaseBuilder()
                    .when(counselSession.counselor.status.eq(CounselorStatus.INACTIVE))
                    .then("탈퇴사용자")
                    .otherwise(counselSession.counselor.name),
                counselSession.status,
                counselCard.cardRecordStatus,
                counseleeConsent.isConsent))
            .from(counselSession)
            .leftJoin(counselSession.counselee)
            .leftJoin(counselSession.counselor)
            .leftJoin(counselCard).on(counselSession.eq(counselCard.counselSession))
            .leftJoin(counseleeConsent).on(counselSession.eq(counseleeConsent.counselSession))
            .where(builder)
            .orderBy(counselSession.scheduledStartDateTime.asc());

        JPAQuery<Long> countQuery = queryFactory
            .select(counselSession.count())
            .from(counselSession)
            .where(builder);

        return QuerydslPagingUtil.applyPagination(pageReq, contentQuery, countQuery);
    }

    @Override
    public Long countByStatus(ScheduleStatus status) {
        return queryFactory
            .select(counselSession.count())
            .from(counselSession)
            .where(counselSession.status.eq(status))
            .fetchOne();
    }

    @Override
    public Long countDistinctCounseleeForCurrentMonth() {
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
    public List<String> cancelOverDueSessionsAndReturnAffectedCounseleeIds() {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);

        List<Tuple> canceledSessions = queryFactory
            .select(counselSession.id, counselSession.counselee.id)
            .from(counselSession)
            .where(
                counselSession.status.in(ScheduleStatus.SCHEDULED, ScheduleStatus.IN_PROGRESS),
                counselSession.scheduledStartDateTime.before(twentyFourHoursAgo)
            )
            .fetch();

        if (canceledSessions.isEmpty()) {
            return List.of();
        }
        List<String> sessionIds = canceledSessions.stream()
            .map(tuple -> tuple.get(counselSession.id))
            .toList();

        List<String> affectedCounseleeIds = canceledSessions.stream()
            .map(tuple -> tuple.get(counselSession.counselee.id))
            .distinct()
            .toList();

        queryFactory
            .update(counselSession)
            .set(counselSession.status, ScheduleStatus.CANCELED)
            .where(counselSession.id.in(sessionIds))
            .execute();

        return affectedCounseleeIds;
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
    public PageRes<CounselSession> findByCounseleeNameAndCounselorNameAndScheduledDateTimeAndStatus(
        PageReq pageReq,
        String counseleeNameKeyword,
        List<String> counselorNames,
        List<LocalDate> scheduledDates,
        List<ScheduleStatus> statuses) {

        BooleanBuilder builder = new BooleanBuilder();

        if (counseleeNameKeyword != null && !counseleeNameKeyword.isEmpty()) {
            builder.and(counselSession.counselee.name.containsIgnoreCase(counseleeNameKeyword));
        }

        if (counselorNames != null && !counselorNames.isEmpty()) {
            builder.and(counselSession.counselor.name.in(counselorNames));
            builder.and(counselSession.counselor.status.eq(CounselorStatus.ACTIVE));
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

        if (statuses != null && !statuses.isEmpty()) {
            builder.and(counselSession.status.in(statuses));
        }

        JPAQuery<CounselSession> contentQuery = queryFactory
            .selectFrom(counselSession)
            .where(builder)
            .orderBy(counselSession.scheduledStartDateTime.desc());

        JPAQuery<Long> countQuery = queryFactory
            .select(counselSession.count())
            .from(counselSession)
            .where(builder);

        return QuerydslPagingUtil.applyPagination(pageReq, contentQuery, countQuery);
    }

    @Override
    public List<CounselSession> findValidCounselSessionsByCounseleeId(String counseleeId) {
        return queryFactory.
            selectFrom(counselSession)
            .where(
                counselSession.counselee.id.eq(counseleeId),
                counselSession.status.ne(ScheduleStatus.CANCELED))
            .orderBy(counselSession.scheduledStartDateTime.asc())
            .fetch();
    }

    @Override
    public void bulkUpdateCounselSessionNum(Map<String, Integer> sessionUpdates) {
        CaseBuilder caseBuilder = new CaseBuilder();
        CaseBuilder.Cases<Integer, NumberExpression<Integer>> caseExpression = null;

        for (Map.Entry<String, Integer> entry : sessionUpdates.entrySet()) {
            if (caseExpression == null) {
                caseExpression = caseBuilder
                    .when(counselSession.id.eq(entry.getKey()))
                    .then(entry.getValue());
            } else {
                caseExpression = caseExpression
                    .when(counselSession.id.eq(entry.getKey()))
                    .then(entry.getValue());
            }
        }

        if (caseExpression == null) {
            return;
        }

        NumberExpression<Integer> finalExpression = caseExpression.otherwise(counselSession.sessionNumber);

        queryFactory.update(counselSession)
            .set(counselSession.sessionNumber, finalExpression)
            .where(counselSession.id.in(sessionUpdates.keySet()))
            .execute();
    }

    @Override
    public Long countDistinctCounselorsByCompletedSessionsInYear(int year) {
        return queryFactory
            .select(counselSession.counselor.countDistinct())
            .from(counselSession)
            .where(
                counselSession.status.eq(ScheduleStatus.COMPLETED),
                counselSession.startDateTime.year().eq(year),
                counselSession.counselor.isNotNull(),
                counselSession.counselor.status.eq(CounselorStatus.ACTIVE))
            .fetchOne();
    }
}