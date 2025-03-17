package com.springboot.api.counselcard.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.api.counselcard.entity.CounselCard;
import com.springboot.api.counselcard.entity.QCounselCard;
import com.springboot.api.counselsession.entity.QCounselSession;
import com.springboot.enums.CardRecordStatus;
import com.springboot.enums.ScheduleStatus;

@Repository
public class CounselCardRepositoryImpl extends QuerydslRepositorySupport implements
    CounselCardRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QCounselCard counselCard;

    public CounselCardRepositoryImpl(JPAQueryFactory queryFactory) {
        super(CounselCard.class);
        this.queryFactory = queryFactory;
        this.counselCard = QCounselCard.counselCard;
    }

    @Override
    public Optional<CounselCard> findCounselCardWithCounselee(String counselSessionId) {
        return Optional.ofNullable(queryFactory
            .selectFrom(counselCard)
            .innerJoin(counselCard.counselSession).fetchJoin()
            .innerJoin(counselCard.counselSession.counselee).fetchJoin()
            .fetchJoin()
            .where(counselCard.counselSession.id.eq(counselSessionId))
            .fetchOne());
    }

    @Override
    public Optional<CounselCard> findCounselCardByCounselSessionId(String counselSessionId) {
        return Optional.ofNullable(queryFactory
            .selectFrom(counselCard)
            .innerJoin(counselCard.counselSession).fetchJoin()
            .where(counselCard.counselSession.id.eq(counselSessionId))
            .fetchOne());
    }

    @Override
    public Optional<CounselCard> findLastRecordedCounselCard(String counseleeId) {
        return Optional.ofNullable(queryFactory
            .selectFrom(counselCard)
            .where(counselCard.counselSession.counselee.id.eq(counseleeId)
                .and(counselCard.counselSession.status.ne(ScheduleStatus.CANCELED))
                .and(counselCard.cardRecordStatus.ne(CardRecordStatus.NOT_STARTED)))
            .orderBy(counselCard.counselSession.scheduledStartDateTime.desc())
            .limit(1)
            .fetchOne());
    }

    @Override
    public List<CounselCard> findRecordedCardsByPreviousSessions(String currentSessionId) {
        QCounselSession counselSession = QCounselSession.counselSession;
        QCounselCard currentCounselCard = QCounselCard.counselCard;

        LocalDateTime currentSessionStartTime = queryFactory
            .select(counselSession.scheduledStartDateTime)
            .from(counselSession)
            .where(counselSession.id.eq(currentSessionId))
            .fetchOne();

        if (currentSessionStartTime == null) {
            throw new IllegalArgumentException("상담 세션을 찾을 수 없습니다: " + currentSessionId);
        }

        return queryFactory
                .selectFrom(currentCounselCard)
                .join(currentCounselCard.counselSession, counselSession)
            .fetchJoin()
            .where(
                counselSession.scheduledStartDateTime.before(currentSessionStartTime),
                counselSession.status.ne(ScheduleStatus.CANCELED),
                        currentCounselCard.cardRecordStatus.eq(
                                CardRecordStatus.COMPLETED)
            )
            .orderBy(counselSession.scheduledStartDateTime.desc())
            .fetch();
    }
}
