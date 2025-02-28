package com.springboot.api.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.api.domain.Counselee;
import com.springboot.api.domain.QCounselee;

@Repository
public class CounseleeRepositoryImpl extends QuerydslRepositorySupport implements CounseleeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CounseleeRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Counselee.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<Counselee> findWithFilters(
            String name,
            List<LocalDate> birthDates,
            List<String> affiliatedWelfareInstitutions,
            Pageable pageable) {

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

        JPAQuery<Counselee> query = queryFactory
                .selectFrom(counselee)
                .where(builder)
                .orderBy(counselee.registrationDate.desc());

        long total = queryFactory
                .select(counselee.count())
                .from(counselee)
                .where(builder)
                .fetchOne();

        List<Counselee> results = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results, pageable, total);
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
        return Optional.ofNullable(queryFactory
                .selectFrom(counselee)
                .innerJoin(counselee.counselSessions)
                .where(counselee.counselSessions.any().id.eq(counselSessionId))
                .fetchOne());
    }
}