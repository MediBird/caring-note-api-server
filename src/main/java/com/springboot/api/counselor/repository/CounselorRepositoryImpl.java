package com.springboot.api.counselor.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    public CounselorRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Counselor.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<Counselor> findAllWithRoleTypeOrder(Pageable pageable) {
        QCounselor counselor = QCounselor.counselor;

        // ROLE_ASSISTANT가 먼저 오는 순서 조건 생성
        OrderSpecifier<?> roleTypeOrder = new CaseBuilder()
                .when(counselor.roleType.eq(RoleType.ROLE_ASSISTANT)).then(0)
                .otherwise(1)
                .asc();

        // 쿼리 생성
        JPAQuery<Counselor> query = queryFactory
                .selectFrom(counselor)
                .orderBy(
                        roleTypeOrder,
                        counselor.updatedDatetime.desc());

        // 전체 카운트 쿼리
        long total = queryFactory
                .select(counselor.count())
                .from(counselor)
                .fetchOne();

        // 페이징 적용 및 결과 조회
        List<Counselor> results = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 페이징 결과 반환
        return new PageImpl<>(results, pageable, total);
    }
}