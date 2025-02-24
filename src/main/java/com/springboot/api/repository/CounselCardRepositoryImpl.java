package com.springboot.api.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.api.domain.CounselCard;
import com.springboot.api.domain.QCounselCard;
import com.springboot.enums.CardRecordStatus;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CounselCardRepositoryImpl extends QuerydslRepositorySupport implements CounselCardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CounselCardRepositoryImpl(JPAQueryFactory queryFactory) {
        super(CounselCard.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<CounselCard> findLastRecordedCounselCard(String counseleeId) {
        QCounselCard counselCard = QCounselCard.counselCard;
        return Optional.ofNullable(queryFactory
                .selectFrom(counselCard)
                .where(counselCard.counselSession.counselee.id.eq(counseleeId)
                        .and(counselCard.cardRecordStatus.eq(CardRecordStatus.RECORDED)))
                .orderBy(counselCard.counselSession.scheduledStartDateTime.desc())
                .limit(1)
                .fetchOne());
    }
}
