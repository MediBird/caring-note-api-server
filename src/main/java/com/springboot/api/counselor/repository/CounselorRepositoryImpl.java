package com.springboot.api.counselor.repository;

import com.springboot.api.common.dto.PageReq;
import com.springboot.api.common.dto.PageRes;
import com.springboot.api.common.util.QuerydslPagingUtil;
import com.springboot.enums.CounselorStatus;
import java.util.List;

import java.util.Optional;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.api.counselor.entity.Counselor;
import com.springboot.api.counselor.entity.QCounselor;
import com.springboot.enums.RoleType;

@Repository
public class CounselorRepositoryImpl extends QuerydslRepositorySupport implements CounselorRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QCounselor counselor = QCounselor.counselor;

    public CounselorRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Counselor.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public Boolean existsActiveByEmail(String email) {
        Integer count = queryFactory
            .selectOne()
            .from(counselor)
            .where(counselor.email.eq(email)
                .and(counselor.status.eq(CounselorStatus.ACTIVE)))
            .fetchFirst();
        return count != null;
    }

    @Override
    public Optional<Counselor> findActiveByUsername(String username) {
        return Optional.ofNullable(
            queryFactory
                .selectFrom(counselor)
                .where(counselor.username.eq(username)
                    .and(counselor.status.eq(CounselorStatus.ACTIVE)))
                .fetchOne()
        );
    }

    @Override
    public Optional<Counselor> findActiveById(String counselorId) {
        return Optional.ofNullable(
            queryFactory
                .selectFrom(counselor)
                .where(counselor.id.eq(counselorId)
                    .and(counselor.status.eq(CounselorStatus.ACTIVE)))
                .fetchOne()
        );
    }

    @Override
    public List<Counselor> findActiveByRoleTypes(List<RoleType> roleTypes) {
        return queryFactory
            .selectFrom(counselor)
            .where(counselor.roleType.in(roleTypes)
                .and(counselor.status.eq(CounselorStatus.ACTIVE)))
            .fetch();
    }

    @Override
    public PageRes<Counselor> findAllActiveWithRoleTypeOrder(PageReq pageReq) {
        // ROLE_ASSISTANT가 먼저 오는 순서 조건 생성
        OrderSpecifier<?> roleTypeOrder = new CaseBuilder()
            .when(counselor.roleType.eq(RoleType.ROLE_ASSISTANT)).then(0)
            .otherwise(1)
            .asc();

        JPAQuery<Counselor> contentQuery = queryFactory
            .selectFrom(counselor)
            .where(counselor.status.eq(CounselorStatus.ACTIVE))
            .orderBy(
                roleTypeOrder,
                counselor.updatedDatetime.desc());

        JPAQuery<Long> countQuery = queryFactory
            .select(counselor.count())
            .from(counselor)
            .where(counselor.status.eq(CounselorStatus.ACTIVE));

        return QuerydslPagingUtil.applyPagination(pageReq, contentQuery, countQuery);
    }
}