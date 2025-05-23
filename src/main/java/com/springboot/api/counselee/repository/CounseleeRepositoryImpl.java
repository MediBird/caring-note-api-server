package com.springboot.api.counselee.repository;

import com.springboot.api.common.dto.PageReq;
import com.springboot.api.common.dto.PageRes;
import com.springboot.api.common.util.QuerydslPagingUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.api.counselee.entity.Counselee;
import com.springboot.api.counselee.entity.QCounselee;
import com.springboot.api.counselsession.entity.QCounselSession;

@Repository
public class CounseleeRepositoryImpl extends QuerydslRepositorySupport implements CounseleeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CounseleeRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Counselee.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public PageRes<Counselee> findWithFilters(String name, List<LocalDate> birthDates,
        List<String> affiliatedWelfareInstitutions, PageReq pageReq) {

        QCounselee counselee = QCounselee.counselee;
        BooleanBuilder builder = new BooleanBuilder();

        if (name != null) {
            builder.and(counselee.name.containsIgnoreCase(name));
        }

        if (birthDates != null && !birthDates.isEmpty()) {
            builder.and(counselee.dateOfBirth.in(birthDates));
        }

        if (affiliatedWelfareInstitutions != null && !affiliatedWelfareInstitutions.isEmpty()) {
            builder.and(counselee.affiliatedWelfareInstitution.in(affiliatedWelfareInstitutions));
        }

        JPAQuery<Counselee> contentQuery = queryFactory
            .selectFrom(counselee)
            .where(builder)
            .orderBy(counselee.updatedDatetime.desc());

        JPAQuery<Long> countQuery = queryFactory
            .select(counselee.count())
            .from(counselee)
            .where(builder);

        return QuerydslPagingUtil.applyPagination(pageReq, contentQuery, countQuery);
    }

    @Override
    public List<LocalDate> findDistinctBirthDates() {
        QCounselee counselee = QCounselee.counselee;
        return queryFactory
            .select(counselee.dateOfBirth)
            .distinct()
            .from(counselee)
            .orderBy(counselee.dateOfBirth.desc())
            .fetch();
    }

    @Override
    public List<String> findDistinctAffiliatedWelfareInstitutions() {
        QCounselee counselee = QCounselee.counselee;
        return queryFactory
            .select(counselee.affiliatedWelfareInstitution)
            .distinct()
            .from(counselee)
            .where(counselee.affiliatedWelfareInstitution.isNotNull())
            .orderBy(counselee.affiliatedWelfareInstitution.asc())
            .fetch();
    }

    @Override
    public Optional<Counselee> findByCounselSessionId(String counselSessionId) {
        QCounselee counselee = QCounselee.counselee;
        QCounselSession counselSession = QCounselSession.counselSession;

        return Optional.ofNullable(queryFactory
            .select(counselee)
            .from(counselSession)
            .where(counselSession.id.eq(counselSessionId))
            .fetchOne());
    }

    @Override
    public List<Counselee> findByNameContaining(String keyword) {
        QCounselee counselee = QCounselee.counselee;
        return queryFactory
            .selectFrom(counselee)
            .where(counselee.name.containsIgnoreCase(keyword))
            .orderBy(
                new CaseBuilder()
                    .when(counselee.name.startsWithIgnoreCase(keyword))
                    .then(1)
                    .otherwise(2)
                    .asc(),
                counselee.name.asc())
            .limit(5)
            .fetch();
    }
}